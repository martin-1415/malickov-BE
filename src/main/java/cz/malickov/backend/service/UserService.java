package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.UserAlreadyExists;
import cz.malickov.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public UserOutboundDTO registerUser(UserInboundDTO userInboundDTO) {

        User user = User.builder()
                .lastName(userInboundDTO.getLastName())
                .firstName(userInboundDTO.getFirstName())
                .email(userInboundDTO.getEmail())
                .password(bCryptPasswordEncoder.encode(userInboundDTO.getPassword()))
                .roleName(userInboundDTO.getRoleName())
                .active(true)
                .build();
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);


        if (validateUser(user)) {
            userRepository.save(user);
        }else{
            throw new RuntimeException("Provided data for new user are not valid.");
        }

        return new UserOutboundDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isActive(),
                user.getRoleName().toString()
        );
    }

    private boolean validateUser(User user) {
        if (user.getLastName() == null) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("First name is required");
        }

        if (!this.validateEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email has incorrect form.");
        } else {

            Optional<String> foundEmail = userRepository.findAll()// not optimal, but no performance needed here
                    .stream()
                    .map(users -> users.getEmail())
                    .filter(emails -> emails.equals(user.getEmail()))
                    .findFirst();

            if (foundEmail.isPresent()) {
                throw new UserAlreadyExists(user.getEmail());
            }

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
            throw new RuntimeException("User not found with email: " + userUpdated.getEmail());
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


    public String verify(UserLoginDTO userLogin) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
        
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userLogin.getEmail());
        }else{
            throw new RuntimeException("Invalid username or password");
        }
    }

}