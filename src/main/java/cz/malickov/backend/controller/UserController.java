package cz.malickov.backend.controller;

import cz.malickov.backend.dto.UserLoginDTO;
import cz.malickov.backend.dto.UserOutboundDTO;
import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;



@RestController
public class UserController {


    private  final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserOutboundDTO> getUser(@RequestBody UserInboundDTO userInboundDTO) {

        UserOutboundDTO saved = this.userService.registerUser(userInboundDTO);
        return ResponseEntity.ok().body(saved);

    }

    @CrossOrigin(origins = "http://localhost:4200")
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
