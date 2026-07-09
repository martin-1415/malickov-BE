package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
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


@RestController
@Slf4j
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;
    private final long maxAgeMillis;
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
        ResponseCookie cookie = this.buildJwtCookie(jwt, false);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body( outboundUser );
    }

    @PostMapping("/login")
    public ResponseEntity<UserOutboundDTO> login(@RequestBody @Valid UserLoginDTO userLogin
                                                           ) {
        String jwt = loginService.verify(userLogin);

        UserOutboundDTO userDTO = this.userService.getUserOutboundDtoByUserEmail(userLogin.email());
        ResponseCookie cookie = this.buildJwtCookie(jwt, false);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body( userDTO);
    }

    /**
     * Function used to return user info based on JWT, it also validates JWT token
     *  AuthenticationPrincipal already proves JWT authentication, so no more checks needed here
     */
    // @TODO send back new refresh and auth tokens in cookies
    @GetMapping("/authenticationBasedOnCookies")
    public ResponseEntity<UserOutboundDTO> returnUserBasedOnJWT(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        return ResponseEntity.ok()
                .body(this.userService.getLoggedUserOutboundDTO(userDetails));
    }

    /**
     * Logoutfunction sends back empty cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> logout() {
        ResponseCookie cookie = this.buildJwtCookie("Good_Bye",true);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }

    private ResponseCookie buildJwtCookie(String jwt, boolean clearCookie) {
        long maxAge= clearCookie ? 0 : maxAgeMillis/1000;

        return ResponseCookie.from("JWT", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None")
                .build();
    }
}
