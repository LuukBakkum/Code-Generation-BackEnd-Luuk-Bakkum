Feature: Users

    Background:
        Given user is logged in as "EMPLOYEE" with username "johndoe" password "john123"
        Given the API is running
        Given user header is set
        

        #werkt
    #getAllUsers
    Scenario: Fetching All Users with specific filter and limit
        Given there are users in the system and I have 'EMPLOYEE' authority.
        When I send a GET request to '/users' endpoint with the filter set as 'firstName:John' and limit set as 20.
        Then I should receive a 200 OK response with a list of UserResponseDTOs of users with first name 'John' and the list should not contain more than 20 users.

    Scenario: Fetching All Users with Invalid Filter Format
        Given I have 'EMPLOYEE' authority.
        When I send a GET request to '/users' endpoint with the filter set as 'firstNameJohn' (invalid format).
        Then I should receive a 400 Bad Request response.

    #getUserById
    Scenario: Fetching a Specific User
        Given there are users in the system and I have 'EMPLOYEE' or 'CUSTOMER' authority.
        When I send a GET request to '/users/{id}' endpoint with a valid user ID.
        Then I should receive a 200 OK response with UserResponseDTO of the user with the corresponding ID.

    Scenario: Fetching User by Invalid ID
        Given I have 'EMPLOYEE' authority.
        When I send a GET request to '/users/{id}' endpoint with an ID that does not exist in the system.
        Then I should receive a 404 Not Found response.

    Scenario: Unauthorized Access to Fetch a Specific User
        Given there are users in the system and I have 'CUSTOMER' authority.
        When I send a GET request to '/users/{id}' endpoint with an ID that does not correspond to my user ID.
        Then I should receive a 403 Forbidden response.

    #getAccountsByUserId
    Scenario: Fetching All Accounts of a Specific User
        Given there are users with accounts in the system and I have 'EMPLOYEE' authority.
        When I send a GET request to '/users/{id}/accounts' endpoint with a valid user ID.
        Then I should receive a 200 OK response with a list of AccountResponseDTOs corresponding to the user with the provided ID.

    Scenario: Fetching All Accounts of a Specific User with Invalid ID
        Given I have 'EMPLOYEE' authority.
        When I send a GET request to '/users/{id}/accounts' endpoint with an ID that does not exist in the system.
        Then I should receive a 404 Not Found response.

    Scenario: Unauthorized Access to Fetch All Accounts of a Specific User
        Given there are users with accounts in the system and I have 'CUSTOMER' authority.
        When I send a GET request to '/users/{id}/accounts' endpoint with an ID that does not correspond to my user ID.
        Then I should receive a 403 Forbidden response.

    #post/users
    Scenario: Add a user successfully
        Given I am authorized with 'EMPLOYEE' role
        When I send POST request to "/users" with valid user details
        Then the response status should be 201
        And the response should contain the added user details

    Scenario: Add a user with invalid data
        Given I am authorized with 'EMPLOYEE' role
        When I send POST request to "/users" with invalid user details
        Then the response status should be 400
        And the response should contain "Bad Request"

#put/users (update user)
    Scenario: Update a user successfully
        Given I am authorized with 'EMPLOYEE' role
        When I send PUT request to "/users/{id}" with valid user details
        Then the response status should be 200
        And the response should contain the updated user details

    Scenario: Update a non-existing user
        Given I am authorized with 'EMPLOYEE' role
        When I send PUT request to "/users/{nonExistingId}" with valid user details
        Then the response status should be 404
        And the response should contain "Not Found"

#delete/users (delete user)
    Scenario: Delete a user successfully
        Given I am authorized with 'EMPLOYEE' role
        When I send DELETE request to "/users/{id}"
        Then the response status should be 204
        And the response body should be empty

    Scenario: Delete a non-existing user
        Given I am authorized with 'EMPLOYEE' role
        When I send DELETE request to "/users/{nonExistingId}"
        Then the response status should be 404
        And the response should contain "Not Found"
        