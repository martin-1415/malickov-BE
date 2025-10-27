package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.error.LoginException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public LoginService( AuthenticationManager authManager,
                         JWTService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    /**
     * authenticating a user against the database during the login
     * @param userLogin:  email and password
     * @return JWT token
     */
    public String verify(UserLoginDTO userLogin) {

        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.email(), userLogin.password()));
            if (authentication.isAuthenticated()) {
                return jwtService.generateAuthToken(userLogin.email());
            }
        }catch (AuthenticationException e){
            throw new LoginException();
        }
        return null;
    }

}
