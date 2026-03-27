
package cz.malickov.backend;

import cz.malickov.backend.dto.UserInboundDTO;
import cz.malickov.backend.entity.User;
import cz.malickov.backend.enums.Role;
import cz.malickov.backend.mapper.UserMapper;
import cz.malickov.backend.repository.UserRepository;
import cz.malickov.backend.service.JWTService;
import cz.malickov.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private JWTService jwtService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, 10, userMapper, jwtService);
    }

    @Test
    void shouldCreateUser() {
        Role roleName = Role.PARENT;
        User user = new User("lastName", "firstName", "email", true,
                "identifier", roleName);

        assertNotNull(user);
        assertEquals("lastName", user.getLastName());
        assertEquals("firstName", user.getFirstName());
        assertEquals("email", user.getEmail());
        assertTrue(user.isActive());
        assertEquals("identifier", user.getIdentifier());
        assertEquals(roleName, user.getRoleName());
    }

    @Test
    void shouldUpdateUser() {
        Role roleName = Role.PARENT;
        User user = new User("lastName", "firstName", "email", true,
                "identifier", roleName);
        Role roleManager = Role.MANAGER;
        UserInboundDTO mockUser = new UserInboundDTO( UUID.randomUUID(),"John","Doe",
                "ffd@gggd.cz",false, roleManager);
        userMapper.updateEntity(mockUser, user);

        assertNull(user.getUserUuid()); // test whether UUID is not changed
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("ffd@gggd.cz", user.getEmail());
        assertFalse(user.isActive());
        assertEquals("identifier", user.getIdentifier());
        assertEquals(roleManager, user.getRoleName());
    }

    @Test
    void shouldCreateUserFromUserInboundSuccessfully() {

        Role role = Role.PARENT;
        UserInboundDTO mockUser = new UserInboundDTO( null,"John","Doe",
                "abc@gggd.cz",true, role);


        // When
        User savedUser = userMapper.toEntity(mockUser);
        savedUser.setActive(true);
        savedUser.setIdentifier("John_Doe_abcd");

        when(userRepository.findByEmail("abc@gggd.cz"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(savedUser));

        when(userRepository.save(savedUser)).thenReturn(savedUser);
        // test
        User result = userService.registerUser(mockUser);


        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("abc@gggd.cz", result.getEmail());
        assertEquals(role, result.getRoleName());
        assertTrue(result.isActive());
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        Role role = Role.PARENT;
        UUID uuid = UUID.randomUUID();
        UserInboundDTO oldMockUser = new UserInboundDTO( uuid,"John","Doe",
                "aaa@bbb.cz",true,role);

        UserInboundDTO newMockUser = new UserInboundDTO( uuid,"John2","Doe2",
                "aaa@bbb.cz2",true,role);

        User oldUser = userMapper.toEntity(oldMockUser);
        // When
        when(userRepository.findByUserUuid(uuid))
                .thenReturn(Optional.of(oldUser));
        // Then
        User result = userService.updateUser(newMockUser);

        assertNotNull(result);
        assertEquals("John2", result.getFirstName());
        assertEquals("Doe2", result.getLastName());
        assertEquals("aaa@bbb.cz2", result.getEmail());
        assertEquals(role, result.getRoleName());
        assertTrue(result.isActive());
    }

    @Test
    void userMapStructTest() {
        Role role = Role.PARENT;
        UserInboundDTO mockUser = new UserInboundDTO( null,"John",
                "Doe","ffd@gggd.cz",true, role);

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
