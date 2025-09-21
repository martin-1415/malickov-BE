package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody UserLoginDTO userLogin) {
        return userService.verify(userLogin);
    }
}
