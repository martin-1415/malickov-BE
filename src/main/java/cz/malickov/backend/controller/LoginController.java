package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;


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
    @ResponseStatus(HttpStatus.OK)
    public void login(@RequestBody UserLoginDTO userLogin, HttpServletResponse response) {


        String jwt = loginService.verify(userLogin); // returns the token
        ResponseCookie cookie = ResponseCookie.from("JWT", jwt)
                .httpOnly(true) // no javascript access
                .secure(true) // https
                .path("/")
                .maxAge(maxAgeMillis/1000) //  seconds, same period as JWT
                .sameSite("None") // can be strict, but then links from e.g. google should not work
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
