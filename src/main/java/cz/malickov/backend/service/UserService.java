package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.ApiException;
import cz.malickov.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public UserService(UserRepository userRepository, @Value("${security.bcrypt.strength}") int bCryptStrength, AuthenticationManager authManager, JWTService jwtService ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(bCryptStrength);
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public User registerUser(UserInboundDTO userInboundDTO) {
        User user = User.builder()
                .lastName(userInboundDTO.getLastName())
                .firstName(userInboundDTO.getFirstName())
                .email(userInboundDTO.getEmail())
                //.password(bCryptPasswordEncoder.encode(userInboundDTO.getPassword())) zatim neposilam password bude to v aktivaci a zmene hesla
                .roleName(userInboundDTO.getRoleName())
                .active(true)
                .build();
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);


        if (validateUser(user)) {
            Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
            if (optionalUser.isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT,"User with such email already exixsts");
            }
            userRepository.save(user);
        }else{
            throw new ApiException(HttpStatus.BAD_REQUEST,"Provided data for new user are not valid.");
        }


        return user;
    }

    private boolean validateUser(User user) {
        if (user.getLastName().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Last name is required");
        }
        if (user.getFirstName().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"First name is required");
        }

        if (!this.validateEmail(user.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"Email has incorrect form.");
        }
        return true;
    }


    private static boolean validateEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }


    public User updateUser(UserInboundDTO userUpdated) {
        Optional<User> optionalUser = userRepository.findByEmail(userUpdated.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastName(userUpdated.getLastName());
            user.setFirstName(userUpdated.getFirstName());
            user.setEmail(userUpdated.getEmail());

            return userRepository.save(user);

        } else {
            throw new ApiException(HttpStatus.NOT_FOUND,"User not found, email: " + userUpdated.getEmail());
        }
    }


    public List<UserOutboundDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserOutboundDTO> usersDto= users.stream()
                .map(u -> UserOutboundDTO.builder()
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .active(u.isActive())
                        .roleName(u.getRoleName().toString())
                        .build()
                )
                .sorted(Comparator.comparing(UserOutboundDTO::getLastName)) // :: method refrence, stejny jako u -> u.getFirstName()
                .collect(Collectors.toList());

        return usersDto;
    }



    /**
     * authenticating a user against the database during the login
     * @param userLogin:  email and password
     * @return JWT token
     */
    public String verify(UserLoginDTO userLogin) {

        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(userLogin.getEmail());
            }
        }catch (AuthenticationException e){
            throw new ApiException(HttpStatus.FORBIDDEN,"Invalid username or password");
        }
        return null;
    }
}