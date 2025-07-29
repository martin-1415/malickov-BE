
package cz.malickov.backend;

import cz.malickov.backend.entity.Role;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;



@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {

        Role userRole = new Role("ROLE_PARENT");
        User mockUser = new User("doe","john", "john@example.com", true, userRole);

        userService.saveUser(mockUser);
        verify(userRepository).save(any(User.class));
    }
}
