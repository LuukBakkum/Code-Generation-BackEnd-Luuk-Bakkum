package nl.inholland.codegeneration.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.lu.a;
import io.cucumber.messages.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.JsonMappingException;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.stepdefinitions.SharedStringService;

import org.springframework.beans.factory.annotation.Autowired;

public class SharedSteps {
    public RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private ScenarioContext context;

    @Autowired
    private SharedStringService sharedStringService;

    private ResponseEntity<String> response;
    private String authToken;
    private HttpHeaders headers;

    @Given("the API is running")
    public void the_api_is_running() {

        String url = "http://localhost:8080/health";

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + authToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        
        
        ResponseEntity<String> healthCheckResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        if (!"UP".equals(healthCheckResponse.getBody())) {
            throw new RuntimeException("API is not running");
        }
    }
    @Given("user is logged in as {string} with username {string} password {string}")
    public void user_is_logged_in_as_with_username_password(String role, String username, String password) throws JsonMappingException, JsonProcessingException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        
        this.headers = new HttpHeaders();
        String url = "http://localhost:8080/authenticate/login";
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("Response Body: " + response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        
        
        if (response.getStatusCode().value() != 200) { // Assuming 201 is the status for successful creation
            throw new RuntimeException("User could not be created");
        }

        authToken = root.path("token").asText();
        sharedStringService.setToken(authToken);
        // authToken = response.getBody();
        headers.set("Authorization", "Bearer " + authToken);
    }

    // @When("I send a POST request to {string} with:")
    // public void i_send_a_post_request_to_with(String path, String body) {
    //     String url = "http://localhost:8080" + path;
    //     HttpEntity<String> request = new HttpEntity<>(body);
    //     response = restTemplate.postForEntity(url, request, String.class);
    //     context.setResponse(response);
    // }
    @And("a user with username {string} and password {string} already exists")
        public void a_user_with_username_and_password_already_exists(String username, String password) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            String url = "http://localhost:8080/authenticate/login";
            HttpEntity<User> request = new HttpEntity<>(user, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().value() != 200) { // Assuming 201 is the status for successful creation
                throw new RuntimeException("User could not be created");
    }
        }

    @When("I send a POST request to {string} with:")
    public void i_send_a_post_request_to_with(String path, String requestBody) throws Exception {
        try {
            String url = "http://localhost:8080" + path;
    
            // Convert the JSON string to a Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> body = mapper.readValue(requestBody, Map.class);
    
            // Create the request entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
    
            // Make the POST request
            response = restTemplate.postForEntity(url, entity, String.class);
            context.setResponse(response);
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                context.setResponse(response);
            } else if (e.getMessage().contains("401")) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
                context.setResponse(response);
            } else if (e.getMessage().contains("500")){
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                context.setResponse(response);
            }
            else {
                throw new Exception(e);
            }
        }
    }

    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer statusCode) {
    //     assertEquals(statusCode, context.getResponse().getStatusCodeValue());
    // }

    @Then("the response should contain an authentication token")
    public void the_response_should_contain_an_authentication_token() {
        assertTrue(context.getResponse().getBody().contains("token"));
    }

    @And("the response status should be {int}")
    public void the_response_should_contain(Integer code) {
        assertEquals(context.getResponse().getStatusCode().value(), code.intValue());
    }

    @Then("the error response should be {int}")
    public void the_error_response_should_be(Integer int1) {
        assertEquals(context.getResponse().getStatusCode().value(), int1.intValue());
    }

    // @Given("logged in as {string}")
    //     public void logged_in_as(String role){
    //         try {
    //             if(!role.equals("EMPLOYEE")){
    //                 String username = "johndoe";
    //                 String password = "john123";
    //                 String url = "http://localhost:8080/authenticate/login";
    
    //                 ObjectMapper mapper = new ObjectMapper();
    
    //                 // Create a Map or a POJO to hold your post data
    //                 Map<String, String> params = new HashMap<>();
    //                 params.put("username", username);
    //                 params.put("password", password);
            
    //                 // Convert the map to JSON string
    //                 String jsonRequest = mapper.writeValueAsString(params);
            
    //                 // Set headers
    //                 HttpHeaders headers = new HttpHeaders();
    //                 headers.setContentType(MediaType.APPLICATION_JSON);
            
    //                 // Create the request entity
    //                 HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
                    
    //                 ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

    //                 authToken = response.getHeaders().get("Authorization").get(0);
    //             }


    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }


    // }
}
