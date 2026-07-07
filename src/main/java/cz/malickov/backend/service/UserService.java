package cz.malickov.backend.service;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;

import cz.malickov.backend.exception.GeneralException;
import cz.malickov.backend.exception.userExceptions.UserAlreadyExistsException;
import cz.malickov.backend.exception.userExceptions.UserNotFoundException;
import cz.malickov.backend.mapper.UserMapper;
import cz.malickov.backend.model.CustomUserDetails;
import cz.malickov.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.*;


@Service
public class UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /*
     * saves new user into DB
     * @param userInboundDTO: user object
     * @return userOutboundDTO object
    */
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #userInboundDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    @Transactional
    public UserOutboundDTO registerUser(UserInboundDTO userInboundDTO) {

        String email = userInboundDTO.email();
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException("Email already exists.",new UserAlreadyExistsException(email));
                });

        User user = userMapper.toEntity(userInboundDTO);
        user.setActive(true);


        // 5 chars hash for identifier used for bank info
        int hashLength = 5;
        SecureRandom random = new SecureRandom();

        String hash = random.ints(hashLength, 'a', 'z' + 1)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();

        user.setIdentifier(userInboundDTO.firstName().concat("_").concat(userInboundDTO.lastName())
                          .concat("_").concat(hash));

        userRepository.saveAndFlush(user);

        return userMapper.toOutboundDTO(user);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_DIRECTOR') or (hasAuthority('ROLE_MANAGER') and #updatedUserDTO.role.name() == T(cz.malickov.backend.enums.Role).PARENT.name())")
    public UserOutboundDTO updateUser(UserInboundDTO updatedUserDTO) {

        User userToUpdate = userRepository.findById(updatedUserDTO.uuid())
                .orElseThrow(() -> new UserNotFoundException("User with uuid '" + updatedUserDTO.uuid() + "' not found."));
        // only names, email, telephone, active and role can be updated here
        userMapper.updateEntity(updatedUserDTO,userToUpdate);

        return userMapper.toOutboundDTO(userToUpdate);
    }

    /*
     * get all active users
     * @return List<userOutboundDTO>
     */
    public List<UserOutboundDTO> getActiveUsers() {
        List<User> users = userRepository.findByActiveTrueOrderByLastNameAsc();
        return users.stream()
                .map(userMapper::toOutboundDTO).toList();
    }

    /*
     * get all non active users
     * @return List<userOutboundDTO>
     */
    public List<UserOutboundDTO> getInactiveUsers() {
        List<User> users = userRepository.findByActiveFalseOrderByLastNameAsc();
        return users.stream()
                .map(userMapper::toOutboundDTO).toList();
    }

    /*
     * sets new password to a user with null password
     * @param UserLoginDTO: email and new password
     * @return userOutboundDTO
     */
    @Transactional
    public UserOutboundDTO setPassword(UserLoginDTO userLogin){
        User user = this.userRepository.findByEmail(userLogin.email())
                .orElseThrow(() -> new UserNotFoundException("User with such an email does not exist"));

        if( !StringUtils.hasText(user.getPassword()) ) { // false for: null ""  and " "
            user.setPassword(passwordEncoder.encode(userLogin.password()));
            return userMapper.toOutboundDTO(user);
        }else{
            throw new GeneralException("Old password has to be deleted first.");
        }
    }


    /*
      Used during login to get user based on his email which was extracted from cookies
     */
    public UserOutboundDTO getUserOutboundDtoByUserEmail(String email){
        User user= userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("User with email "+ email + " does not exists"));
        return userMapper.toOutboundDTO(user);
    }

    /*
     * deletes password
     * @param String: user_uuid
     * @return void
     */
    @Transactional
    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    public UserOutboundDTO deletePassword(String uuid) {
        User user =
                this.userRepository.findById(UUID.fromString(uuid))
                .orElseThrow(()->new UserNotFoundException("User with uuid "+ uuid + " does not exists"));
        user.setPassword(null);
        return userMapper.toOutboundDTO(user);
    }

    @Deprecated
    public Optional<User> getByEmail(@NotBlank @Email(message = "Email is not valid") String email) {
        return userRepository.findByEmail(email);
    }

    public UserOutboundDTO getLoggedUserOutboundDTO( CustomUserDetails userDetails){
        User user = userDetails.getUser();
        return userMapper.toOutboundDTO(user);
    }
}