package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.LoginFailedException;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final int maxAgeMillis;
    private final UserRepository userRepository;

    public LoginController(LoginService loginService,
                           @Value("${security.JwtValidityMillis}") int maxAgeMillis,
                           UserRepository userRepository){
        this.loginService = loginService;
        this.maxAgeMillis = maxAgeMillis;
        this.userRepository = userRepository;
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

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new UserOutboundDTO(loggedUser.getUserId(),
                                loggedUser.getFirstName(),
                                loggedUser.getLastName(),
                                loggedUser.getEmail(),
                                loggedUser.isActive(),
                                loggedUser.getRoleName(),
                                loggedUser.getCredit()));
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
}
