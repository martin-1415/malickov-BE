package cz.malickov.backend.controller;


import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.error.UserNotFoundException;
import cz.malickov.backend.mapper.UserMapper;
import cz.malickov.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PostMapping("/newUser")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserOutboundDTO> createUser(@RequestBody @Valid UserInboundDTO userInboundDTO) {
        User savedUser = this.userService.registerUser(userInboundDTO);

        return ResponseEntity.ok(userMapper.toOutboundDTO(savedUser));
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getActiveUsers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserOutboundDTO> getActiveUsers() {
        return userService.getActiveUsers();
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @GetMapping("/getInactiveUsers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserOutboundDTO> getNonActiveUser() {
        return userService.getInactiveUsers();
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @PutMapping("/updateUser/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public UserOutboundDTO updateUser(@PathVariable UUID uuid, @RequestBody @Valid UserInboundDTO user2update) {

        if (user2update.uuid() != null && !uuid.equals(user2update.uuid())) {
            throw new UserNotFoundException("Path id ("+ uuid + ") and payload id ("+ user2update.uuid()+") mismatch.");
        }

        User updatedUser = userService.updateUser(user2update);
        return userMapper.toOutboundDTO(updatedUser);
    }
}
