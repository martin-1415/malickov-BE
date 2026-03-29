package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.LoginFailedException;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.LoginService;
import cz.malickov.backend.service.UserService;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@Slf4j
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService loginService;
    private final int maxAgeMillis;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final UserService userService;

    public LoginController(LoginService loginService,
                           @Value("${security.JwtValidityMillis}") int maxAgeMillis,
                           UserRepository userRepository,
                           JWTService jwtService, UserService userService) {
        this.loginService = loginService;
        this.maxAgeMillis = maxAgeMillis;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /*
     * sets password to be null
     * @param String: user UUID
     * @return ok status
     */
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') and #uuid != null and #uuid != ''")
    @PutMapping("/deletePassword/{uuid}")
    public ResponseEntity<UserOutboundDTO> deletePassword(@PathVariable String uuid) {
        userService.deletePassword(uuid);
        return ResponseEntity.ok().build();
    }

    /*
     * sets new password to a user with null password
     * @param UserLoginDTO: email and new password
     * @return userOutbound and JWT token in cookies
     */
    @PostMapping("/setPassword")
    public ResponseEntity<UserOutboundDTO> setPassword(@RequestBody UserLoginDTO userLogin) {

        UserOutboundDTO outboundUser = userService.setPassword(userLogin);
        String jwt = jwtService.generateAuthToken(userLogin.email());
        ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                .httpOnly(true) // no javascript access
                .secure(true) // https
                .path("/")    // prefix of the path where cookies will be sent
                .maxAge(maxAgeMillis / 1000) //  seconds, same period as JWT
                .sameSite("None") // can be strict, but then links from e.g. google should not work
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body( outboundUser );
    }

    @PostMapping("/login")
    public ResponseEntity<UserOutboundDTO> login(@RequestBody UserLoginDTO userLogin) {

        try {
            String jwt = loginService.verify(userLogin);

            Optional<User> userOptional = this.userRepository.findByEmail(userLogin.email());
            if (userOptional.isPresent()) {
                User loggedUser = userOptional.get();

                ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                        .httpOnly(true) // no javascript access
                        .secure(true) // https
                        .path("/")    // prefix of the path where cookies will be sent
                        .maxAge(maxAgeMillis / 1000) //  seconds, same period as JWT
                        .sameSite("None") // can be strict, but then links from e.g. google should not work
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body( userService.getOutboundUserDtoBasedOnEmail(loggedUser.getEmail()) );
            }
            else{
                log.info("User with email {} not found.", userLogin.email());
                throw new LoginFailedException();
            }
        } catch (LoginFailedException e) {
            throw e;
        }catch(Exception e){
            log.error("Error during verifying login for email: {}", userLogin.email(), e);
            throw new LoginFailedException();
        }
    }

    /*
     * Function validating JWT token, will be used to refresh token
     *
     */
    @Deprecated
    @GetMapping("/isAuthenticated")
    public ResponseEntity<Map<String,Object>> isAuthenticated(
            @CookieValue(value="JWT", required=false) String token
    ) {
        try{
            jwtService.isTokenExpired(token);
        } catch (SignatureException | IllegalArgumentException e) { // this contains also null token and expired token which throws IllegalArgumentException
            return ResponseEntity.ok().body(Map.of(
                    "authenticated", false));
        }
        return ResponseEntity.ok().body(Map.of(
                "authenticated", true));
    }

    /*
    * Function used to get user info based on JWT, it also validates
    */
    // @TODO send back new refresh and auth tokens in cookies
    @GetMapping("/authentication")
    public ResponseEntity<?> returnUserBasedOnJWT(
            @CookieValue(value="JWT", required=false) String token
    ) {
        if (token == null || jwtService.isTokenExpired(token)) {
            log.info("Token expired or not valid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "authenticated", false,
                    "reason", "Token expired or not valid"
            ));
        }

        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok().body(userService.getOutboundUserDtoBasedOnEmail(email));
    }

    /*
     * Logoutfunction sends back empty cookies
     */
    @GetMapping("/logout")
    public ResponseEntity<Map<String,String>> clearCookies() {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)  // deletes cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out successfully"));
    }

}
