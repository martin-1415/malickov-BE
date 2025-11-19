package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.error.LoginFailedException;
import cz.malickov.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;


@RestController
public class LoginController {

    private final LoginService loginService;
    private final int maxAgeMillis;

    public LoginController(LoginService loginService,
                           @Value("${security.JwtValidityMillis}") int maxAgeMillis) {
        this.loginService = loginService;
        this.maxAgeMillis = maxAgeMillis;
    }


    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserLoginDTO userLogin) throws LoginException {

        try {
            String jwt = loginService.verify(userLogin);

            ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                    .httpOnly(true) // no javascript access
                    .secure(true) // https
                    .path("/")
                    .maxAge(maxAgeMillis / 1000) //  seconds, same period as JWT
                    .sameSite("None") // can be strict, but then links from e.g. google should not work
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .build();

        }catch(Exception e){
            throw new LoginFailedException();
        }
    }
}
