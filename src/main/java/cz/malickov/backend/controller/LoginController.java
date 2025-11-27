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
                        .path("/")
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
    Function validates JWT token
     */
    // @TODO send back new refresh and auth tokens in cookies
    @GetMapping("/isAuthenticated")
    public ResponseEntity<Map<String,Object>> isAuthenticated(
            @CookieValue(value="JWT", required=false) String token
    ) {
        boolean tokenExpired;
        try{
            tokenExpired = jwtService.isTokenExpired(token);
        } catch (SignatureException e) {
            return ResponseEntity.ok().body(Map.of(
                    "authenticated", false));
        }

        if (token == null || tokenExpired) {
            return ResponseEntity.ok().body(Map.of(
                    "authenticated", false));
        }

        return ResponseEntity.ok().body(Map.of(
                "authenticated", true));
    }


    /*
Function  returns user info based on JWT
 */
    // @TODO send back new refresh and auth tokens in cookies
    @GetMapping("/returnOutboundUserBasedOnJWT")
    public ResponseEntity<Map<String,Object>> returnUserBasedOnJWT(
            @CookieValue(value="JWT", required=false) String token
    ) {
        if (token == null || jwtService.isTokenExpired(token)) {
            log.info("Token expired or not valid");
            throw new LoginFailedException();
        }

        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok().body(Map.of(
                "user",userService.getOutboundUserDtoBasedOnEmail(email)));
    }

}
