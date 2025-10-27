package cz.malickov.backend.controller;


import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.UserNotFoundException;
import cz.malickov.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/newUser")
    @ResponseStatus(HttpStatus.CREATED)
    public UserOutboundDTO createUser(@RequestBody @Valid UserInboundDTO userInboundDTO) {
        User savedUser = this.userService.registerUser(userInboundDTO);
        return UserOutboundDTO.userOutboundDTOfromEntity( savedUser);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR')")
    @GetMapping("/getAllUsers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserOutboundDTO> getAllUser() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PutMapping("/updateUser/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserOutboundDTO updateUser(@PathVariable Long id, @RequestBody @Valid UserInboundDTO userUpdated) {

        if (userUpdated.id() != null && !id.equals(userUpdated.id())) {
            throw new UserNotFoundException("Path id ("+ id + ") and payload id ("+userUpdated.id()+") mismatch.");
        }

        User updatedUser = userService.updateUser(userUpdated);
        return UserOutboundDTO.userOutboundDTOfromEntity(updatedUser);
    }
}
