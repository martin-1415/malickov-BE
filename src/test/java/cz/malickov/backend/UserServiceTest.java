
package cz.malickov.backend;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.enums.Role;
import cz.malickov.backend.mapper.UserMapper;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, 10, userMapper);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        Role role = Role.PARENT;
        UserInboundDTO mockUser = new UserInboundDTO( null,"John","Doe","ffd@gggd.cz",true, role);

        // When
        User result = userService.registerUser(mockUser);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("ffd@gggd.cz", result.getEmail());
        assertEquals(role, result.getRoleName());
        assertTrue(result.isActive());
    }

    @Test
    void userMapStructTest() {
        Role role = Role.PARENT;
        UserInboundDTO mockUser = new UserInboundDTO( null,"John","Doe","ffd@gggd.cz",true, role);

        //when
        User user =  this.userMapper.toEntity(mockUser);
        // Then
        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("ffd@gggd.cz", user.getEmail());
        assertEquals(role, user.getRoleName());
        assertTrue(user.isActive());
    }

}
