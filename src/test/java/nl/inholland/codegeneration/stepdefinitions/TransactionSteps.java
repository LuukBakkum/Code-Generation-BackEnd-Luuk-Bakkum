package nl.inholland.codegeneration.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.services.TransactionService;

import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

public class TransactionSteps {
    Transaction transaction = new Transaction();
    private Account myAccount;
    private Account theirAccount;
    private ResponseEntity<List> responses;
    private RestTemplate restTemplate = new RestTemplate();
    TransactionService transactionService;

    @Autowired
    private ScenarioContext context;
    @Autowired
    private SharedStringService sharedStringService;
    private String authToken;

    private HttpHeaders headers = new HttpHeaders();

    @Given("header is set")
    public void header_is_set() {
        authToken = sharedStringService.getToken();
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
    }

    //Make transaction
    @Given("I have {int} on my bank account")
    public void i_have_on_my_bank_account(Integer amount) {
        myAccount = new Account();
        myAccount.setBalance(BigDecimal.valueOf(amount));
    }
    @When("I transfer {int} to the other bank account")
    public void i_transfer_to_the_other_bank_account(Integer amount) {
        theirAccount = new Account();
        transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setAccountFrom(myAccount);
        transaction.setAccountTo(theirAccount);
        // additional code to execute the transaction

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(myAccount.getIban(),
                theirAccount.getIban(), new BigDecimal(20), "cucumber test");

        // transactionService.add(transactionRequestDTO);

        myAccount.setBalance(myAccount.getBalance().subtract(transaction.getAmount()));
        theirAccount.setBalance(theirAccount.getBalance().add(transaction.getAmount()));
 
        // moet misschien nog iets met de response doen

    }
    @Then("I should have {int} on my bank account")
    public void i_should_have_on_my_bank_account(Integer expectedBalance) {
        assertEquals(expectedBalance.intValue(), myAccount.getBalance().intValue());
    }

    //Get transactions
    
    @WithMockUser(username = "testUser", roles = {"EMPLOYEE"})
    @When("a request is to GET {string}")
    public void a_request_is_to_GET_transactions(String path) {

        // Test implementation ------------------------------------------
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        // --------------------------------------------------------------

        String url = "http://localhost:8080" + path;

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        responses = restTemplate.exchange(url, HttpMethod.GET, entity, ParameterizedTypeReference.forType(List.class));
    }
    @WithMockUser(username = "testUser", roles = {"EMPLOYEE"})
    @And("should contain a List of transaction objects")
    public void should_contain_a_List_of_transaction_objects() {
        assertTrue(responses.getBody().size() > 0);
    }

    //Get transaction by id
    private ResponseEntity<TransactionResponseDTO> responseById;
    private TransactionResponseDTO transactionResponseDTO;
    private TransactionRequestDTO transactionRequestDTO;
    private Transaction transactionById;
    private Transaction transactionById2;

    @Given("a transaction with id {int} exists")
    public void a_transaction_with_id_exists(Integer id) {

        transactionById = new Transaction();
        transactionById.setId(Long.valueOf(id));
        transactionById.setAmount(BigDecimal.valueOf(20));
        transactionById.setAccountFrom(myAccount);
        transactionById.setAccountTo(theirAccount);
        transactionById.setDescription("cucumber test");
        transactionById2 = new Transaction();
        transactionById2.setId(Long.valueOf(id));
        transactionById2.setAmount(BigDecimal.valueOf(20));
        transactionById2.setAccountFrom(myAccount);
        transactionById2.setAccountTo(theirAccount);
        transactionById2.setDescription("cucumber test");
    }
    
    @When("a request is to GET a {string} by id {int}")
    public void a_request_is_to_GET_a_transaction_by_id(String path, Integer id) {

         // Test implementation ------------------------------------------
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        // --------------------------------------------------------------

        String url = "http://localhost:8080" + path + id.toString();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        responseById = restTemplate.exchange(url, HttpMethod.GET, entity, TransactionResponseDTO.class);
        transactionResponseDTO = responseById.getBody();
    }
    
    @Then("the response should be {int} and contain the transaction with id {int}")
    public void the_response_should_be_and_contain_the_transaction_with_id(Integer int1, Integer id) {
        assertEquals(responseById.getStatusCode().value(), int1.intValue());
        assertEquals(transactionResponseDTO.id(), id.intValue());
    }

    // try to get transaction with bad id
    private ResponseEntity<String> response;

    @Given("no {string} with id {int} exists")
    public void no_transaction_with_id_exists(String path, Integer id) {

            // Test implementation ------------------------------------------
            this.headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            // --------------------------------------------------------------

        String url = "http://localhost:8080" + path + id;
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            throw new RuntimeException("Transaction with id " + id + " should not exist.");
        } catch (HttpClientErrorException.NotFound ex) {
            // Transaction doesn't exist, which is what we want
        }
    }

    @When("a request is to GET a {string} by bad id {int}")
    public void a_request_is_to_GET_a_transaction_by_bad_id(String path, Integer id) {

        // Test implementation ------------------------------------------
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        // --------------------------------------------------------------

        String url = "http://localhost:8080" + path + Long.valueOf(id);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            context.setResponse(response);
        } catch (HttpClientErrorException ex) {
            response = ResponseEntity.status(ex.getRawStatusCode()).build();
            context.setResponse(response);
        }
    }

    // @WithMockUser(username = "testUser", roles = {"EMPLOYEE"})
    @Then("the response should be {int}")
    public void the_response_should_be(Integer int1) {
        assertEquals(responses.getStatusCode().value(), int1.intValue());
    }


    //Scenario: Successfully create a transaction
    @Given("a transaction for €{int} with description {string} with accountFrom iban {string} and accountTo iban {string} exist")
    public void a_transaction_with__exist(Integer amount, String description, String from, String to) {
        transactionRequestDTO = new TransactionRequestDTO(from, to, new BigDecimal(amount), description);        
    }
    @When("a request is to POST the {string}")
    public void a_request_is_to_post_the(String path) {
        // Test implementation ------------------------------------------
        this.headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        // --------------------------------------------------------------

        String url = "http://localhost:8080" + path;
        HttpEntity<TransactionRequestDTO> entity = new HttpEntity<>(transactionRequestDTO, headers);
        response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
    @Then("the create response should be {int}")
    public void the_create_response_should_be(Integer code) {
        assertEquals(response.getStatusCode().value(), code.intValue());
    }
    // for create bad transaction
    @And("the response message should be {string}")
    public void the_response_message_should_be(String message)
    {
    assertTrue(response.getBody().contains(message));        
    }

}