package cz.malickov.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControlerTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void testParentCannotCreateUser() {
        // Step 1: Login as Parent (3@3.cz)
        String loginJson = """
                {
                  "email": "4@4.cz",
                  "password": "abc"
                }
                """;

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate
                .postForEntity("/api/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Extract JWT token from cookies
        String jwtCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(jwtCookie).isNotNull();

        // Step 2: Try to create a user (should fail with 403 Forbidden)
        String createUserJson = """
                {
                  "firstName": "Test",
                  "lastName": "User",
                  "email": "test@test.cz",
                  "active": true,
                  "role": "PARENT"
                }
                """;

        HttpHeaders createUserHeaders = new HttpHeaders();
        createUserHeaders.setContentType(MediaType.APPLICATION_JSON);
        createUserHeaders.add(HttpHeaders.COOKIE, jwtCookie);

        HttpEntity<String> createUserRequest = new HttpEntity<>(createUserJson, createUserHeaders);

        ResponseEntity<String> createUserResponse = restTemplate
                .postForEntity("/api/user/newUser", createUserRequest, String.class);

        assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testTeacherCannotCreateDirector() {
        String loginJson = """
                {
                  "email": "3@3.cz",
                  "password": "abc"
                }
                """;

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate
                .postForEntity("/api/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String jwtCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(jwtCookie).isNotNull();

        String createDirectorJson = """
                {
                  "firstName": "Test",
                  "lastName": "Director",
                  "email": "teacher-director@test.cz",
                  "telephone": "123456789",
                  "active": true,
                  "role": "DIRECTOR"
                }
                """;

        HttpHeaders createUserHeaders = new HttpHeaders();
        createUserHeaders.setContentType(MediaType.APPLICATION_JSON);
        createUserHeaders.add(HttpHeaders.COOKIE, jwtCookie);

        HttpEntity<String> createUserRequest = new HttpEntity<>(createDirectorJson, createUserHeaders);

        ResponseEntity<String> createUserResponse = restTemplate
                .postForEntity("/api/user/newUser", createUserRequest, String.class);

        assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void testManagerCannotCreateDirector() {
        String loginJson = """
                {
                  "email": "2@2.cz",
                  "password": "abc"
                }
                """;

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate
                .postForEntity("/api/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String jwtCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertThat(jwtCookie).isNotNull();

        String createDirectorJson = """
                {
                  "firstName": "Test",
                  "lastName": "Director",
                  "email": "manager-director@test.cz",
                  "telephone": "123456789",
                  "active": true,
                  "role": "DIRECTOR"
                }
                """;

        HttpHeaders createUserHeaders = new HttpHeaders();
        createUserHeaders.setContentType(MediaType.APPLICATION_JSON);
        createUserHeaders.add(HttpHeaders.COOKIE, jwtCookie);

        HttpEntity<String> createUserRequest = new HttpEntity<>(createDirectorJson, createUserHeaders);

        ResponseEntity<String> createUserResponse = restTemplate
                .postForEntity("/api/user/newUser", createUserRequest, String.class);

        assertThat(createUserResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


}
