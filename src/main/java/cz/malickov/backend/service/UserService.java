package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;

import cz.malickov.backend.error.UserAlreadyExistsException;
import cz.malickov.backend.error.UserNotFoundException;
import cz.malickov.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // will be used to reset password


    public UserService(UserRepository userRepository,
                       @Value("${security.bcrypt.strength}") int bCryptStrength
                       ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(bCryptStrength);
    }

    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #userInboundDTO.roleName.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public User registerUser(UserInboundDTO userInboundDTO) {

        String email = userInboundDTO.email();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email '" + email + "' already exixsts");
        }
        
        User user = User.builder()
                .lastName(userInboundDTO.lastName())
                .firstName(userInboundDTO.firstName())
                .email(email)
                //.password(bCryptPasswordEncoder.encode(userInboundDTO.getPassword())) this is going to be set in ResetPassword method
                .roleName(userInboundDTO.roleName())
                .active(true)
                .build();
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);

        userRepository.save(user);
        log.info("User {} registered successfully", user.getEmail());

        return user;
    }


    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #updatedUser.roleName.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public User updateUser(UserInboundDTO updatedUser) {
        String email = updatedUser.email();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email+ "' not found."));

        user.setLastName(updatedUser.lastName());
        user.setFirstName(updatedUser.firstName());
        user.setEmail(email);

        return userRepository.save(user);
    }


    public List<UserOutboundDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map( UserOutboundDTO::userOutboundDTOfromEntity)
                .sorted(Comparator.comparing(UserOutboundDTO::getLastName))
                .collect(Collectors.toList());
    }
}