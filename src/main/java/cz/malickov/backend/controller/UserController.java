package cz.malickov.backend.controller;


import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.ApiException;
import cz.malickov.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


import cz.malickov.backend.enums.Role;



@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/newParent")
    @ResponseStatus(HttpStatus.OK)
    public UserOutboundDTO createUser(@RequestBody @Valid UserInboundDTO userInboundDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<String> currentUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst(); // only one role per person

        // manager can create only parents, not employees, authentication adds ROLE_ prefix
        if ( (currentUserRole.isPresent() && currentUserRole.get().equals("ROLE_MANAGER")) && userInboundDTO.getRoleName() != Role.PARENT) {
            throw new ApiException(HttpStatus.FORBIDDEN,"Your role cannot create employees of the kindergarten. Just clients.");
        }
        User savedUser = this.userService.registerUser(userInboundDTO);

        return UserOutboundDTO.UserOutboundDTOfromEntity( savedUser);

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PreAuthorize("hasRole('DIRECTOR')")
    @GetMapping("/allUsers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserOutboundDTO> getAllUser() {
        return userService.getAllUsers();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/updateUser")
    @ResponseStatus(HttpStatus.OK)
    public UserOutboundDTO addUser(@RequestBody @Valid UserInboundDTO userUpdated) {
        User updatedUser = userService.updateUser(userUpdated);
        return UserOutboundDTO.UserOutboundDTOfromEntity(updatedUser);
    }
}
