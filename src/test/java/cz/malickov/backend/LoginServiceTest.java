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
class AuthorizationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testSuccesLoginWithPassAndEmail() {
        // login payload for DEV
        String json = """
                {
                  "email": "1@1.cz",
                  "password": "abc"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // POST to login endpoint
        ResponseEntity<String> response = restTemplate
                .postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void testFailedLoginWithWrongPassAndEmail() {
        // login payload for DEV
        String json = """
                {
                  "email": "1@1.cz",
                  "password": "abc-WRONG"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // POST to login endpoint
        ResponseEntity<String> response = restTemplate
                .postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void testGetProtectedEndpointWithToken() {
//        HttpHeaders headers = new HttpHeaders();
//
//        HttpEntity<Void> request = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "/allUsers",
//                HttpMethod.GET,
//                request,
//                String.class
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//    }
}