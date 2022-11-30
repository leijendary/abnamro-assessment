package nl.abnamro.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ApplicationTests {
    private static final String LOGIN_URL = "https://leijendary.us.auth0.com/oauth/token";
    private static final String CLIENT_ID = "jjivzCfCgE6zsdqcAr7kpDurmOT4ZcWc";
    private static final String CLIENT_SECRET = "yK5-ucc4-WJZ2L15nEeIh366FUd9SUNv3m5-vAg7rjcoRr8FGFjBa10_Jwwj0Hvy";
    private static final String USER_ID = "auth0|63863d91ea0961b9a9b924bf";

    protected final Faker faker = new Faker();
    protected final HttpHeaders basicHeaders = basicHeaders();
    protected final String token = getBearerToken();
    protected final HttpHeaders bearerHeaders = bearerHeaders();

    @Autowired
    protected ObjectMapper mapper;

    @Test
    void contextLoads() {
    }

    private String getBearerToken() {
        final var body = createBody();
        final var entity = new HttpEntity<>(body, basicHeaders);
        final var restTemplate = new RestTemplate();
        final var response = restTemplate
                .exchange(LOGIN_URL, HttpMethod.POST, entity, Map.class)
                .getBody();

        Assertions.assertNotNull(response, "Access token was not retrieved");

        return (String) response.get("access_token");
    }

    private MultiValueMap<String, String> createBody() {
        final var body = new LinkedMultiValueMap<String, String>();
        body.add("username", "abnamro@leijendary.com");
        body.add("password", "ABNAMRO1234!");
        body.add("grant_type", "password");
        body.add("audience", "http://localhost:8000");
        body.add("scope", "urn:recipe:page:v1 urn:recipe:create:v1 urn:recipe:get:v1 urn:recipe:update:v1 urn:recipe:delete:v1");

        return body;
    }

    private HttpHeaders basicHeaders() {
        final var headers = new HttpHeaders();
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return headers;
    }

    private HttpHeaders bearerHeaders() {
        final var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }

    protected ResultMatcher isCreated() {
        return status().isCreated();
    }

    protected ResultMatcher isOk() {
        return status().isOk();
    }

    protected ResultMatcher isJson() {
        return content().contentType(MediaType.APPLICATION_JSON);
    }

    protected void assertUser(final String response) {
        Assertions.assertEquals(USER_ID, response);
    }
}
