
package cz.malickov.backend;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.enums.Role;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, 10);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        Role role = Role.PARENT;
        UserInboundDTO mockUser = new UserInboundDTO("John","Doe","ffd@gggd.cz",true, role);

        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.registerUser(mockUser);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("ffd@gggd.cz", result.getEmail());
        assertEquals(role, result.getRoleName());
        assertTrue(result.isActive());
        assertNotNull(result.getCreatedAt());
    }
}
