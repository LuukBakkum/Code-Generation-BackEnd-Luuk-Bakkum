package nl.inholland.codegeneration.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.lu.an;
import io.cucumber.java.en.Then;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;

import java.util.List;

import org.apiguardian.api.API;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.cucumber.java.en.Given;

public class UserSteps {

    @Autowired
    private ScenarioContext context;
    @Autowired
    private SharedStringService sharedStringService;
    private String authToken;
    private HttpHeaders headers = new HttpHeaders();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<UserResponseDTO[]> response;
    ResponseEntity<List<UserResponseDTO>> responses;

    @Given("user header is set")
    public void header_is_set() {
        authToken = sharedStringService.getToken();
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
    }

    // Scenario: Fetching All Users with specific filter and limit
    @Given("there are users in the system and I have 'EMPLOYEE' authority.")
    public void there_are_users_in_the_system_and_I_have_EMPLOYEE_authority() {

    }
    @When("I send a GET request to {string} endpoint with the filter set as {string} and limit set as {int}.")
    public void i_send_a_GET_request_to_users_endpoint_with_the_filter_set_as_firstName_John_and_limit_set_as_20(
            String path, String filter, Integer limit) {

        String url = "http://localhost:8080" + path + "?filter=" + filter + "&limit=" + limit;

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        responses = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(List.class));
    }

//     @When("I send a GET request to {string} endpoint with the filter set as {string} and limit set as {string}.")
// public void i_send_a_get_request_to_endpoint_with_the_filter_set_as_and_limit_set_as(String string, String string2, String string3) {
//     // Write code here that turns the phrase above into concrete actions
//     throw new io.cucumber.java.PendingException();
// }
    @Then("I should receive a {int} OK response with a list of UserResponseDTOs of users with first name 'John' and the list should not contain more than 20 users.")
    public void i_should_receive_a_OK_response_with_a_list_of_UserResponseDTOs_of_users_with_first_name_John_and_the_list_should_not_contain_more_than_20_users(Integer code) {
        assertEquals(HttpStatus.OK, responses.getStatusCode());
        assertTrue(responses.getBody().size() <= 20);
    }

    //Scenario: Fetching All Users with Invalid Filter Format
    @When("I send a GET request to {string} endpoint with the filter set as {string} (invalid format).")
    public void i_send_a_GET_request_to_users_endpoint_with_the_filter_set_as_firstName_John_invalid_format(String path,
            String filter) {
        String url = "http://localhost:8080/" + path + "?filter=" + filter;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    @Then("I should receive a {int} Bad Request response.")
    public void i_should_receive_a_400_Bad_Request_response(Integer code) {
        assertEquals(HttpStatus.BAD_REQUEST, responses.getStatusCode());
    }

    //Scenario: Fetching a Specific User
    @Given("there are users in the system and I have 'EMPLOYEE' or 'CUSTOMER' authority.")
    public void there_are_users_in_the_system_and_I_have_EMPLOYEE_or_CUSTOMER_authority() {
        //hier moet nog auth bij
    }
    @When("I send a GET request to {string} endpoint with a valid user ID.")
    public void i_send_a_GET_request_to_users_endpoint_with_a_valid_user_ID(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    @Then("I should receive a 200 OK response with UserResponseDTO of the user with the corresponding ID.")
    public void i_should_receive_a_200_OK_response_with_UserResponseDTO_of_the_user_with_the_corresponding_ID() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for (UserResponseDTO user : response.getBody()) {
            assertEquals("John", user.firstName());
        }
    }
    // Scenario: Fetching User by Invalid ID
    @When("I send a GET request to {string} endpoint with an ID that does not exist in the system.")
    public void i_send_a_GET_request_to_users_endpoint_with_an_ID_that_does_not_exist_in_the_system(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    @Then("I should receive a 404 Not Found response.")
    public void i_should_receive_a_404_Not_Found_response() {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    //Scenario: Unauthorized Access to Fetch a Specific User
    @Given("there are users in the system and I do not have 'EMPLOYEE' or 'CUSTOMER' authority.")
    public void there_are_users_in_the_system_and_I_do_not_have_EMPLOYEE_or_CUSTOMER_authority() {
//hier moet nog auth bij
    }
    @Then("I should receive a 401 Unauthorized response.")
    public void i_should_receive_a_401_Unauthorized_response() {
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    //Scenario: Fetching All Accounts of a Specific User
    @Given("there are users with accounts in the system and I have 'EMPLOYEE' authority.")
    public void there_are_users_with_accounts_in_the_system_and_I_have_EMPLOYEE_authority() {

    }
    @Then("I should receive a 200 OK response with a list of AccountResponseDTOs corresponding to the user with the provided ID.")
    public void i_should_receive_a_200_OK_response_with_a_list_of_AccountResponseDTOs_corresponding_to_the_user_with_the_provided_ID() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        for (UserResponseDTO user : response.getBody()) {
            assertEquals("John", user.firstName());
        }
    }
    //Scenario: Unauthorized Access to Fetch All Accounts of a Specific User
    @Given("there are users with accounts in the system and I do not have 'EMPLOYEE' authority.")
    public void there_are_users_with_accounts_in_the_system_and_I_do_not_have_EMPLOYEE_authority() {
//hier moet nog auth bij
    }
    //Scenario: Add a user successfully
    @Given("I am authorized with 'EMPLOYEE' role")
    public void i_am_authorized_with_EMPLOYEE_role() {
//hier moet nog auth bij
    }
    @When("I send POST request to {string} with valid user details")
    public void i_send_POST_request_to_users_with_valid_user_details(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    // @Then("the response status should be 201")
    // public void the_response_status_should_be_201() {
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    // }
    @Then("the response should contain the added user details")
    public void the_response_should_contain_the_added_user_details() {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
    //Scenario: Add a user with invalid data
    @When("I send POST request to {string} with invalid user details")
    public void i_send_POST_request_to_users_with_invalid_user_details(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }    
    @And("the response should contain {string}")
    public void the_response_should_contain(String string) {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    //Scenario: Update a user successfully
    @When("I send PUT request to {string} with valid user details")
    public void i_send_PUT_request_to_users_with_valid_user_details(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    // @Then("the response status should be 200")
    // public void the_response_status_should_be_200() {
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }
    @And("the response should contain the updated user details")
    public void the_response_should_contain_the_updated_user_details() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    //Scenario: Delete a user successfully
    @When("I send DELETE request to {string}")
    public void i_send_DELETE_request_to_users(String path) {
        String url = "http://localhost:8080/" + path;
        response = restTemplate.getForEntity(url, UserResponseDTO[].class);
    }
    @Then("the response status should be 204")
    public void the_response_status_should_be_204() {
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    @And("the response body should be empty")
    public void the_response_body_should_be_empty() {
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Given("I have {string} authority.")
    public void i_have_authority(String string) {
            
    }
    @When("I send a GET request to {string} endpoint with the filter set as {string} \\(invalid format).")
    public void i_send_a_get_request_to_endpoint_with_the_filter_set_as_invalid_format(String string, String string2) {
        
    }

}
//test
    
    //Scenario: Fetching All Users with Invalid Filter Format
    // @Given("there are users in the system and I have 'EMPLOYEE' authority.")
    // public void there_are_users_in_the_system_and_I_have_EMPLOYEE_authority() {

    // }
        // @Given("there are users in the system and I have 'EMPLOYEE' authority.")
        // public void there_are_users_in_the_system_and_I_have_EMPLOYEE_authority() {

        // }

        // @When("I send a GET request to {string} endpoint with a valid user ID.")
        // public void i_send_a_GET_request_to_users_endpoint_with_a_valid_user_ID(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }

        //Scenario: Fetching All Accounts of a Specific User
        // @When("I send a GET request to {string} endpoint with a valid user ID.")
        // public void i_send_a_GET_request_to_users_endpoint_with_a_valid_user_ID(String path) {
            //     String url = "http://localhost:8080/api" + path;
            //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
            // }

        //Scenario: Fetching All Accounts of a Specific User with Invalid ID
        // @Given("there are users with accounts in the system and I have 'EMPLOYEE' authority.")
        // public void there_are_users_with_accounts_in_the_system_and_I_have_EMPLOYEE_authority() {

        // }
        // @When("I send a GET request to {string} endpoint with an ID that does not exist in the system.")
        // public void i_send_a_GET_request_to_users_endpoint_with_an_ID_that_does_not_exist_in_the_system(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }
        // @Then("I should receive a 404 Not Found response.")
        // public void i_should_receive_a_404_Not_Found_response() {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }

        // @When("I send a GET request to {string} endpoint with a valid user ID.")
        // public void i_send_a_GET_request_to_users_endpoint_with_a_valid_user_ID(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }
        // @Then("I should receive a 401 Unauthorized response.")
        // public void i_should_receive_a_401_Unauthorized_response() {
        //     assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        // }


        //Scenario: Add a user with invalid data
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }

        //Scenario: Update a user successfully
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }

        // //Scenario: Update a non-existing user
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }
        // @When("I send PUT request to {string} with valid user details")
        // public void i_send_PUT_request_to_users_with_valid_user_details(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }
        // @Then("the response status should be 404")
        // public void the_response_status_should_be_404() {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }
        // @And("the response should contain {string}")
        // public void the_response_should_contain(String string) {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }
        

        //Scenario: Update a non-existing user
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }
        // @When("I send PUT request to {string} with valid user details")
        // public void i_send_PUT_request_to_users_with_valid_user_details(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }
        // @Then("the response status should be 404")
        // public void the_response_status_should_be_404() {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }
        // @And("the response should contain {string}")
        // public void the_response_should_contain(String string) {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }

        //Scenario: Delete a user successfully
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }

        // //Scenario: Delete a non-existing user
        // @Given("I am authorized with 'EMPLOYEE' role")
        // public void i_am_authorized_with_EMPLOYEE_role() {

        // }
        // @When("I send DELETE request to {string}")
        // public void i_send_DELETE_request_to_users(String path) {
        //     String url = "http://localhost:8080/api" + path;
        //     this.response = restTemplate.getForEntity(url, UserResponseDTO[].class);
        // }
        // @Then("the response status should be 404")
        // public void the_response_status_should_be_404() {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }
        // @And("the response should contain {string}")
        // public void the_response_should_contain(String string) {
        //     assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        // }

