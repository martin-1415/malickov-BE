package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;

import cz.malickov.backend.error.GeneralException;
import cz.malickov.backend.error.userExceptions.UserAlreadyExistsException;
import cz.malickov.backend.error.userExceptions.UserNotFoundException;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository,
                       @Value("${security.bcrypt.strength}") int bCryptStrength,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(bCryptStrength);
        this.userMapper = userMapper;
    }

    /*
     * saves new user into DB
     * @param userInboundDTO: user object
     * @return userOutboundDTO object
    */
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #userInboundDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public UserOutboundDTO registerUser(UserInboundDTO userInboundDTO) {

        String email = userInboundDTO.email();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new UserAlreadyExistsException(email);
        }


        User user = userMapper.toEntity(userInboundDTO);
        user.setActive(true);

        // 5 chars hash for identifier
        String s = userInboundDTO.firstName().concat(user.getEmail()).concat(userInboundDTO.lastName()).concat(new Date().toString());
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = 31 * hash + s.charAt(i);
        }
        user.setIdentifier(userInboundDTO.firstName().concat("_").concat(userInboundDTO.lastName())
                          .concat("_").concat(Integer.toUnsignedString(hash, 36).substring(0, 5)));

        User savedUser = userRepository.save(user);
        log.debug("User {} registered successfully", email);

        return userMapper.toOutboundDTO(savedUser);
    }


    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #updatedUserDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public UserOutboundDTO updateUser(UserInboundDTO updatedUserDTO) {

        UUID uuid = updatedUserDTO.uuid();
        User userToUpdate = userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("User with uuid '" + uuid+ "' not found."));

        // only names, email, telephone, active and role can be updated here
        userMapper.updateEntity(updatedUserDTO,userToUpdate);
        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toOutboundDTO(updatedUser);
    }

    /*
     * get all active users
     * @return List<userOutboundDTO>
     */
    public List<UserOutboundDTO> getActiveUsers() {
        List<User> users = userRepository.findByActiveTrue();
        return users.stream()
                .sorted(Comparator.comparing(User::getLastName))
                .map(userMapper::toOutboundDTO)
                .collect(Collectors.toList());
    }

    /*
     * get all non active users
     * @return List<userOutboundDTO>
     */
    public List<UserOutboundDTO> getInactiveUsers() {
        List<User> users = userRepository.findByActiveFalse();
        return users.stream()
                .sorted(Comparator.comparing(User::getLastName))
                .map(userMapper::toOutboundDTO)
                .collect(Collectors.toList());
    }

    /*
     * sets new password to a user with null password
     * @param UserLoginDTO: email and new password
     * @return userOutboundDTO
     */
    public UserOutboundDTO setPassword(UserLoginDTO userLogin){
        User user = this.userRepository.findByEmail(userLogin.email())
                .orElseThrow(() -> new UserNotFoundException("User with email "+ userLogin.email() + " does not exists"));

        if( user.getPassword() == null ) {
            user.setPassword(bCryptPasswordEncoder.encode(userLogin.password()));
            userRepository.save(user);
            return userMapper.toOutboundDTO(user);
        }else{
            throw new GeneralException("Old password has to be deleted first.");
        }
    }


    /*
      Used during login to get user based on his email which was extracted from cookies
     */
    public UserOutboundDTO getUserOutboundDtoByUserEmail(String email){

        Optional<User> optinalUser = this.userRepository.findByEmail(email);
        if (optinalUser.isPresent()) {
            User user = optinalUser.get();

            return userMapper.toOutboundDTO(user);
        }
        return null;
    }

    /*
     * deletes password
     * @param String: user_uuid
     * @return void
     */
    public UserOutboundDTO deletePassword(String uuid) {
        Optional<User> optinalUser = this.userRepository.findByUserUuid(UUID.fromString(uuid));
        User user;
        User savedUser;

        if (optinalUser.isPresent()) {
            user = optinalUser.get();
            user.setPassword(null);
            savedUser = userRepository.save(user);
        }else{
            log.warn("User with uuid {} does not exists", uuid);
            throw new UserNotFoundException("User with uuid "+ uuid + " does not exists");
        }
        return userMapper.toOutboundDTO(savedUser);
    }
}