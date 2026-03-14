package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;

import cz.malickov.backend.error.UserAlreadyExistsException;
import cz.malickov.backend.error.UserNotFoundException;
import cz.malickov.backend.mapper.UserMapper;
import cz.malickov.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // will be used to reset password
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository,
                       @Value("${security.bcrypt.strength}") int bCryptStrength,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(bCryptStrength);
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #userInboundDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public User registerUser(UserInboundDTO userInboundDTO) {

        String email = userInboundDTO.email();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            log.info("User with email {} already exists", email);
            throw new UserAlreadyExistsException("User with email '" + email + "' already exists");
        }


        User user = userMapper.toEntity(userInboundDTO);
        user.setActive(true);

        // 4 chars hash for identifier
        String s = userInboundDTO.firstName().concat(user.getEmail()).concat(userInboundDTO.lastName()).concat(new Date().toString());
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = 31 * hash + s.charAt(i);
        }
        user.setIdentifier(userInboundDTO.firstName().concat("_").concat(userInboundDTO.lastName())
                          .concat("_").concat(Integer.toUnsignedString(hash, 36).substring(0, 5)));

        userRepository.save(user);
        log.debug("User {} registered successfully", email);

        return userRepository.findByEmail(email)
                .orElseThrow();
    }


    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #updatedUser.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public User updateUser(UserInboundDTO updatedUser) {
        String email = updatedUser.email();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email+ "' not found."));

        // only names, email and role can be updated here
        userMapper.updateEntity(updatedUser,user);
        userRepository.save(user);
        log.debug("User {} updated successfully", email);

        return user;
    }


    public List<UserOutboundDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .sorted(Comparator.comparing(User::getLastName))
                .map(userMapper::toOutboundDTO)
                .collect(Collectors.toList());
    }

    /*
      Used in login to get user based on his email
     */
    public UserOutboundDTO getOutboundUserDtoBasedOnEmail(String email){

        Optional<User> optinalUser = this.userRepository.findByEmail(email);
        if (optinalUser.isPresent()) {
            User user = optinalUser.get();

            return userMapper.toOutboundDTO(user);
        }
        return null;
    }
}