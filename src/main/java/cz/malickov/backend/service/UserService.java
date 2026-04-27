package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;

import cz.malickov.backend.error.GeneralException;
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
    public User registerUser(UserInboundDTO userInboundDTO) {

        String email = userInboundDTO.email();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            log.info("User with email {} already exists", email);
            throw new UserAlreadyExistsException("User with email '" + email + "' already exists");
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

        userRepository.save(user);
        log.debug("User {} registered successfully", email);

        return userRepository.findByEmail(email)
                .orElseThrow();
    }


    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #updatedUserDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public User updateUser(UserInboundDTO updatedUserDTO) {
        String email = updatedUserDTO.email();
        UUID uuid = updatedUserDTO.uuid();

        User userToUpdate = userRepository.findByUserUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("User with uuid '" + uuid+ "' not found."));

        Optional<User> anotherExistingUserWithTheSameEmail = userRepository.findByEmail(email);
        if(anotherExistingUserWithTheSameEmail.isPresent()){
           throw new UserAlreadyExistsException("Another user has the same email: " + email);
        }

        // only names, email, active and role can be updated here
        userMapper.updateEntity(updatedUserDTO,userToUpdate);
        userRepository.save(userToUpdate);
        log.debug("User {} updated successfully", email);

        return userToUpdate;
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
    public List<UserOutboundDTO> getNonActiveUsers() {
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

        Optional<User> optinalUser = this.userRepository.findByEmail(userLogin.email());
        if (optinalUser.isPresent()) {
            User user = optinalUser.get();
            if( user.getPassword() == null ) {
                user.setPassword(bCryptPasswordEncoder.encode(userLogin.password()));
                userRepository.save(user);
            }else{
                throw new GeneralException("Old password has to be deleted first.");
            }
            return userMapper.toOutboundDTO(user);
        }
        log.warn("User with email {} does not exists", userLogin.email());
        throw new UserNotFoundException("User with email "+ userLogin.email() + " does not exists");
    }


    /*
      Used during login to get user based on his email which was extracted from cookies
     */
    public UserOutboundDTO getOutboundUserDtoBasedOnEmail(String email){

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
    public void deletePassword(String uuid) {
        Optional<User> optinalUser = this.userRepository.findByUserUuid(UUID.fromString(uuid));
        if (optinalUser.isPresent()) {
            User user = optinalUser.get();
            user.setPassword(null);
            userRepository.save(user);
        }else{
            log.warn("User with uuid {} does not exists", uuid);
            throw new UserNotFoundException("User with uuid "+ uuid + " does not exists");
        }

    }
}