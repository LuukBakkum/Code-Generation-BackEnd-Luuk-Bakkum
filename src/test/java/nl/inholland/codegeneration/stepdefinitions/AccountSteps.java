package nl.inholland.codegeneration.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.lu.an;
import io.cucumber.java.en.Then;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.AccountType;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.RestTemplate;
import nl.inholland.codegeneration.stepdefinitions.SharedStringService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountSteps {

    private Account account;
    private RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private ResponseEntity<AccountResponseDTO> responseAccount;
    private AccountRequestDTO accountRequestDTO;
    private ResponseEntity<List<String>> responses;

    private HttpHeaders headers = new HttpHeaders();
    private String authToken;

    @Autowired
    private ScenarioContext context;

    @Autowired
    private SharedStringService sharedStringService;

    @Given("account header is set")
    public void header_is_set() {
        authToken = sharedStringService.getToken();
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
    }

    //Scenario: Successfully get all accounts
    @When("a request is made to GET all {string}")
    public void a_request_is_made_to_get_all_accounts(String path) {

        String url = "http://localhost:8080" + path;

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        responses = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(List.class));
        context.setResponse(response);
        assertEquals(HttpStatus.OK, responses.getStatusCode());       
        
        }
        // assertEquals(HttpStatus.OK, response.getStatusCode());
    @Then("the account responses should be {int}")
    public void the_response_status_should_be(Integer responseStatus) {        
        assertEquals(responses.getStatusCode().value(), responseStatus.intValue());
    }
    @And("the response body should contain a list of account objects")
    public void the_response_body_should_contain_a_list_of_account_objects() {        
        assertTrue(responses.getBody().size() > 0);
    }    
    //Scenario: Successfully get an account by iban
    @And("there is an account with IBAN {string}")
    public void there_is_an_account_with_iban(String iban) {
        String url = "http://localhost:8080/accounts/" + iban;
        
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        response = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(AccountResponseDTO.class));
        context.setResponse(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); 
    }
    //Scenario: Successfully get an account by iban
    @When("a request is made to GET {string}")
    public void a_request_is_made_to_get_accounts(String path) {
        
        try {
            String url = "http://localhost:8080" + path;
            
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(AccountResponseDTO.class));
            context.setResponse(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                context.setResponse(response);
            } else{
                throw e;
            }
        }
    }
    //Scenario: Attempt to get a non-existent account by IBAN
    @And("there is no account with IBAN {string}")
    public void there_is_no_account_with_iban(String iban) {
        try {
            String url = "http://localhost:8080/accounts/" + iban;

            HttpEntity<AccountRequestDTO> entity = new HttpEntity<>(accountRequestDTO, headers);
            response = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(AccountResponseDTO.class));
            context.setResponse(response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw e;
            }
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
    }    
    //Scenario: Successfully insert an account
    @And("the request body contains valid account data")
    public void the_request_body_contains_valid_account_data() {
        accountRequestDTO = new AccountRequestDTO(2L, new BigDecimal(10), AccountType.CURRENT.getValue());
    }
    @When("a request is made to POST {string}")
    public void a_request_is_made_to_post_accounts(String path) {
        try {
            String url = "http://localhost:8080" + path;

        HttpEntity<AccountRequestDTO> entity = new HttpEntity<>(accountRequestDTO, headers);
        
        responseAccount = restTemplate.exchange(url, HttpMethod.POST, entity, AccountResponseDTO.class);
        assertEquals(HttpStatus.CREATED, responseAccount.getStatusCode());
        } catch (Exception e) {
            if (e.getMessage().contains("400")) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                context.setResponse(response);
            } else{
                throw e;
            }
        }
        
    }
    @Then("the account response status should be {int}")
    public void the_account_response_status_should_be(Integer responseStatus) {
        assertEquals(responseStatus.intValue(), responseAccount.getStatusCode().value());
    }
    @And("the response body should contain the inserted account object")
    public void the_response_body_should_contain_the_inserted_account_object() {
        assertEquals(new BigDecimal(10), responseAccount.getBody().absoluteLimit());
    }
    //Scenario: Attempt to insert an account with invalid data
    @And("the request body contains invalid account data")
    public void the_request_body_contains_invalid_account_data() {
        accountRequestDTO = new AccountRequestDTO(0L, new BigDecimal(-10), AccountType.CURRENT.getValue());
    }
    //Scenario: Successfully update an account
    @When("a request is made to PUT {string}")
    public void a_request_is_made_to_put_accounts(String path) {
        try {
            String url = "http://localhost:8080" + path;
        HttpEntity<AccountRequestDTO> entity = new HttpEntity<>(accountRequestDTO, headers);
        responseAccount = restTemplate.exchange(url, HttpMethod.PUT, entity, AccountResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (Exception e) {
            if (e.getMessage().contains("401")) {
                response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
                context.setResponse(response);
            } else{
                throw e;
            }
        }
        
    }
    @And("the response body should contain the updated account object")
    public void the_response_body_should_contain_the_updated_account_object() {
        assertEquals(new BigDecimal(10), responseAccount.getBody().absoluteLimit());
    }
    //Scenario: Successfully delete an account
    ResponseEntity<Void> responseEntity;
    @When("a request is made to DELETE {string}")
    public void a_request_is_made_to_delete_accounts(String path) {
        String url = "http://localhost:8080" + path;

        HttpEntity<Void> entity = new HttpEntity<>(headers); // You usually do not need to send a body with DELETE
        try {
            // if (BigDecimal.ZERO.equals(response.balance())
            responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        } catch (HttpClientErrorException e) {
            responseEntity = new ResponseEntity<>(e.getStatusCode());
            throw e;
        }
    }
    @Then("the delete response status should be {int}")
    public void the_delete_response_status_should_be(Integer responseStatus) {
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

}

//test

    //Scenario: Successfully get all accounts
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }

    //Scenario: Successfully get an account by iban
    // @Given("the API is running")
    // public void the_api_is_running() {
    // should be running
    // }
    // @When("a request is made to GET {string}")
    // public void a_request_is_made_to_get_accounts(String path) {
    //     String url = "http://localhost:8080" + path;
    //     response = restTemplate.getForEntity(url, Account[].class);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //    assertEquals(responseStatus, response.getStatusCodeValue());
    // }

    //Scenario: Attempt to get a non-existent account by IBAN
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }
    // @When("a request is made to GET {string}")
    // public void a_request_is_made_to_get_accounts(String path) {
    //     String url = "http://localhost:8080" + path;
    //     response = restTemplate.getForEntity(url, Account[].class);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }

    //Scenario: Successfully insert an account
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }

    //Scenario: Successfully insert an account


    //Scenario: Attempt to insert an account with invalid data
    // @Given("the API is running")
    // public void the_api_is_running() {
    // should be running
    // }
    // @When("a request is made to POST {string}")
    // public void a_request_is_made_to_post_accounts(String path) {
    //     String url = "http://localhost:8080" + path;
    //     response = restTemplate.postForEntity(url, accountRequestDTO, Account[].class);
    //     assertEquals(HttpStatus.CREATED, response.getStatusCode());
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }
    
    //Scenario: Successfully update an account
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }
    // @And("there is an account with IBAN {string}")
    // public void there_is_an_account_with_iban(String iban) {
    //     String url = "http://localhost:8080/accounts/" + iban;
    //     account = restTemplate.getForObject(url, Account.class);
    //     restTemplate.postForEntity(url, account, Account.class);
    // }
    // @And("the request body contains valid account data")
    // public void the_request_body_contains_valid_account_data() {
    //     accountRequestDTO = new AccountRequestDTO(1L, new BigDecimal(10), AccountType.CURRENT.getValue());
    // }
    //Scenario: Successfully update an account
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }

    //Scenario: Attempt to update a non-existent account
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }
    // @And("there is no account with IBAN {string}")
    // public void there_is_no_account_with_iban(String iban) {
    //     String url = "http://localhost:8080/accounts/" + iban;
    //     try {
    //         account = restTemplate.getForObject(url, Account.class);
    //     } catch (HttpClientErrorException e) {
    //         assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    //     }
    // }
    // @And("the request body contains valid account data")
    // public void the_request_body_contains_valid_account_data() {
    //     accountRequestDTO = new AccountRequestDTO(1L, new BigDecimal(10), AccountType.CURRENT.getValue());
    // }
    // @When("a request is made to PUT {string}")
    // public void a_request_is_made_to_put_accounts(String path) {
    //     String url = "http://localhost:8080" + path;
    //     response = restTemplate.postForEntity(url, accountRequestDTO, Account[].class);
    //     assertEquals(HttpStatus.OK, response.getStatusCode());
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }
    // @And("the response body should contain the updated account object")
    // public void the_response_body_should_contain_the_updated_account_object() {
    //     assertEquals(account.getIban(), response.getBody()[0].getIban());
    // }

    //Scenario: Successfully delete an account
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }
    // @And("there is an account with IBAN {string}")
    // public void there_is_an_account_with_iban(String iban) {
    //     String url = "http://localhost:8080/accounts/" + iban;
    //     account = restTemplate.getForObject(url, Account.class);
    //     restTemplate.postForEntity(url, account, Account.class);
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }

    //Scenario: Attempt to delete a non-existent account
    // @Given("the API is running")
    // public void the_api_is_running() {
    //     // should be running
    // }
    // @And("there is no account with IBAN {string}")
    // public void there_is_no_account_with_iban(String iban) {
    //     String url = "http://localhost:8080/accounts/" + iban;
    //     try {
    //         account = restTemplate.getForObject(url, Account.class);
    //     } catch (HttpClientErrorException e) {
    //         assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    //     }
    // }
    // @When("a request is made to DELETE {string}")
    // public void a_request_is_made_to_delete_accounts(String path) {
    //     String url = "http://localhost:8080" + path;
    //     restTemplate.delete(url);
    // }
    // @Then("the response status should be {int}")
    // public void the_response_status_should_be(Integer responseStatus) {
    //     assertEquals(responseStatus, response.getStatusCodeValue());
    // }
