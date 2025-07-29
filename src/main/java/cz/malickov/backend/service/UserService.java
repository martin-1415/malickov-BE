package cz.malickov.backend.service;

import cz.malickov.backend.dto.RoleDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // @TODO add authorization
    public void saveUser(User user) {
        // Validate required fields
        if (validateUser(user)) {
            userRepository.save(user);
        }
    }

    private boolean validateUser(User user) {
        if (user.getLastName() == null) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (user.getFirstName() == null) {
            throw new IllegalArgumentException("First name is required");
        }

        if (!this.validateEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email in correct form is required");
        } else {

            Optional<String> foundEmail = userRepository.findAll()// not optimal, but no performance needed here
                    .stream()
                    .map(users -> users.getEmail())
                    .filter(email -> email != null)  // Filter out null emails, should not be there but somehow was, i deleted the record
                    .filter(emails -> emails.equals(user.getEmail()))
                    .findFirst();

            if (foundEmail.isPresent()) {
                throw new IllegalArgumentException("Email already exists.");
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
        Optional<User> optionalUser = userRepository.findById(userUpdated.getUserId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastName(userUpdated.getLastName());
            user.setFirstName(userUpdated.getFirstName());
            user.setEmail(userUpdated.getEmail());
            return userRepository.save(user); // Hibernate tracks changes and updates the record
        } else {
            throw new RuntimeException("User not found with id: " + userUpdated.getUserId());
        }
    }


    public List<UserOutboundDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserOutboundDTO> usersDto= users.stream()
                .map(u -> UserOutboundDTO.builder()
                        .userId(u.getUserId())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .email(u.getEmail())
                        .userRole(new RoleDTO(u.getUserRole().getRoleName()))
                        .build()
                )
                .sorted(Comparator.comparing(UserOutboundDTO::getLastName)) // :: method refrence, stejny jako u -> u.getFirstName()
                .collect(Collectors.toList());

        return usersDto;
    }


    public Map<Character, Integer> getOccurence(String text){
        Map<Character, Integer> map = new HashMap<>();

        if(text == null){
            return map;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Integer occurence = map.get(c);
            if(occurence == null){
                map.put(c,1);
            }else{
                map.put(c,occurence+1);
            }

        return map;
        }

        return null;
    }





}