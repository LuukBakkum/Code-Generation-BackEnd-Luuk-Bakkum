Feature: Accounts

Background:
  Given user is logged in as "EMPLOYEE" with username "johndoe" password "john123"
  Given the API is running
  Given account header is set

  #werkt
  Scenario: Successfully get all accounts
    # Given the API is running
    When a request is made to GET all "/accounts"
    Then the account responses should be 200
    And the response body should contain a list of account objects


# Feature: Get Account by IBAN
  #werkt
  Scenario: Successfully get an account by IBAN
    # Given the API is running
    And there is an account with IBAN "NL05INHO0993038392"
    When a request is made to GET "/accounts/NL05INHO0993038392"
    Then the response status should be 200

  #werkt
  Scenario: Attempt to get a non-existent account by IBAN
    # Given the API is running
    And there is no account with IBAN "NL06INHO0000000004"
    When a request is made to GET "/accounts/NL06INHO0000000004"
    Then the response status should be 404

    #werkt
  # Feature: Insert Account
  Scenario: Successfully insert an account
    # Given the API is running
    And the request body contains valid account data
    When a request is made to POST "/accounts"
    Then the account response status should be 201
    And the response body should contain the inserted account object

    #werkt
  Scenario: Attempt to insert an account with invalid data
    # Given the API is running
    And the request body contains invalid account data
    When a request is made to POST "/accounts"
    Then the response status should be 404

    #werkt
# Feature: Update Account
  Scenario: Successfully update an account
    # Given the API is running
    And there is an account with IBAN "NL82INHO0341537476"
    And the request body contains valid account data
    When a request is made to PUT "/accounts/NL82INHO0341537476"
    Then the response status should be 200
    And the response body should contain the updated account object

    #werkt
  Scenario: Attempt to update a non-existent account
    # Given the API is running
    And there is no account with IBAN "NL82INHO03412347476"
    And the request body contains valid account data
    When a request is made to PUT "/accounts/NL82INHO03412347476"
    Then the response status should be 404


# Feature: Delete Account
  Scenario: Successfully delete an account
    # Given the API is running
    And there is an account with IBAN "NL06INHO0857625210"
    When a request is made to DELETE "/accounts/NL06INHO0857625210"
    Then the delete response status should be 204

  Scenario: Attempt to delete a non-existent account
    # Given the API is running
    And there is no account with IBAN "NL06INHO0857625210"
    When a request is made to DELETE "/accounts/NL06INHO0857625210"
    Then the delete response status should be 404
