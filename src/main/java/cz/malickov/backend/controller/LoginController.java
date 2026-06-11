package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.exception.authExceptions.LoginFailedException;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.LoginService;
import cz.malickov.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;
    private final int maxAgeMillis;
    private final JWTService jwtService;
    private final UserService userService;

    public LoginController(LoginService loginService,
                           @Value("${security.JwtValidityMillis}") int maxAgeMillis,
                           JWTService jwtService, UserService userService) {
        this.loginService = loginService;
        this.maxAgeMillis = maxAgeMillis;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * sets new password to a user with null password
     * @param userLogin: email and new password
     * @return userOutbound and JWT token in cookies
     */
    @PostMapping("/setPassword")
    public ResponseEntity<UserOutboundDTO> setPassword(@RequestBody @Valid UserLoginDTO userLogin) {

        UserOutboundDTO outboundUser = userService.setPassword(userLogin);
        String jwt = jwtService.generateAuthToken(userLogin.email());
        ResponseCookie cookie = this.buildJwtCookie(jwt);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body( outboundUser );
    }

    @PostMapping("/login")
    public ResponseEntity<Optional<UserOutboundDTO>> login(@RequestBody @Valid UserLoginDTO userLogin
                                                           ) {
        try {
            String jwt = loginService.verify(userLogin);

            Optional<User> userOptional = this.userService.getByEmail(userLogin.email());
            if (userOptional.isPresent()) {
                User loggedUser = userOptional.get();

                ResponseCookie cookie = this.buildJwtCookie(jwt);
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body( userService.getUserOutboundDtoByUserEmail(loggedUser.getEmail()) );
            }
            else{
                log.info("User with email {} not found.", userLogin.email());
                throw new LoginFailedException();
            }
        }catch(Exception e){
            log.info("Verifying login for email: {} failed", userLogin.email(), e);
            throw new LoginFailedException();
        }
    }

    /**
     * Function used to return user info based on JWT, it also validates JWT token
     */
    // @TODO send back new refresh and auth tokens in cookies
    @GetMapping("/authentication")
    public ResponseEntity<UserOutboundDTO> returnUserBasedOnJWT(
            @CookieValue(value="JWT", required=false) String token,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (token == null || jwtService.isTokenExpired(token)) {
            log.info("Token expired or not valid");
            throw new LoginFailedException();
        }

        return ResponseEntity.ok()
                .body(this.userService.getLoggedUserOutboundDTO(userDetails));
    }

    /**
     * Logoutfunction sends back empty cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> clearCookies() {
        ResponseCookie cookie = this.buildJwtCookie("");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }

    private ResponseCookie buildJwtCookie(String jwt) {
        return ResponseCookie.from("JWT", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAgeMillis / 1000)
                .sameSite("None")
                .build();
    }

}
