package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.error.ApiException;
import cz.malickov.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import cz.malickov.backend.enums.Role;



@RestController
public class UserController {


    private  final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','MANAGER')")
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/newParent")
    public ResponseEntity<UserOutboundDTO> createUser(@RequestBody UserInboundDTO userInboundDTO, Authentication authentication) {



        authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUserRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse(null); // null role cannot get here

        // manager can create only parents, not employees, autentication adds ROLE_ prefix
        if ( currentUserRole.equals("ROLE_MANAGER") && userInboundDTO.getRoleName() != Role.PARENT) {
            throw new ApiException(HttpStatus.FORBIDDEN,"Your role cannot create employees of the kindergarten.");
        }
        UserOutboundDTO savedUser = this.userService.registerUser(userInboundDTO);
        return ResponseEntity.ok().body(savedUser);

    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PreAuthorize("hasRole('DIRECTOR')")
    @GetMapping("/allUsers")
    public ResponseEntity<List<UserOutboundDTO>> getAllUser() {
        List<UserOutboundDTO> usersDto = userService.getAllUsers();
        return ResponseEntity.ok(usersDto);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/updateUser")
    public ResponseEntity<String> addUser(@RequestBody UserInboundDTO userUpdated) {

        try {
            userService.updateUser(userUpdated);
            return ResponseEntity.ok("User updated");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("User not updated: "+e.getMessage() );
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLogin) {
        return ResponseEntity.ok(userService.verify(userLogin));
    }
}
