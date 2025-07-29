
package cz.malickov.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {



}

//package cz.malickov.backend;
//
//import cz.malickov.backend.service.UserService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Map;
//
//class UserServiceTest {
//
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        userService = new UserService(null); // We don't need UserRepository for this test
//    }
//
//    @Test
//    void getOccurence_EmptyString_ReturnsEmptyMap() {
//        // Arrange
//        String input = "";
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getOccurence_SingleCharacter_ReturnsSingleMapping() {
//        // Arrange
//        String input = "a";
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(1, result.get('a'));
//    }
//
//    @Test
//    void getOccurence_RepeatedCharacters_ReturnsCorrectCounts() {
//        // Arrange
//        String input = "hello";
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(4, result.size());
//        assertEquals(1, result.get('h'));
//        assertEquals(1, result.get('e'));
//        assertEquals(2, result.get('l'));
//        assertEquals(1, result.get('o'));
//    }
//
//    @Test
//    void getOccurence_CaseSensitive_TreatsDifferentCases() {
//        // Arrange
//        String input = "Hello";
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(5, result.size());
//        assertEquals(1, result.get('H'));
//        assertEquals(1, result.get('e'));
//        assertEquals(2, result.get('l'));
//        assertEquals(1, result.get('o'));
//    }
//
//    @Test
//    void getOccurence_SpecialCharacters_HandlesCorrectly() {
//        // Arrange
//        String input = "Hello!!123";
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(8, result.size());
//        assertEquals(1, result.get('H'));
//        assertEquals(1, result.get('e'));
//        assertEquals(2, result.get('l'));
//        assertEquals(1, result.get('o'));
//        assertEquals(2, result.get('!'));
//        assertEquals(1, result.get('1'));
//        assertEquals(1, result.get('2'));
//        assertEquals(1, result.get('3'));
//    }
//
//    @Test
//    void getOccurence_NullInput_ReturnsEmptyMap() {
//        // Arrange
//        String input = null;
//
//        // Act
//        Map<Character, Integer> result = userService.getOccurence(input);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//}