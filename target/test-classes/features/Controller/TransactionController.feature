Feature: Transactions
  As a owner of a bank account
  I want to transfer money to another bank account
  So that I can pay for my groceries

  Background:
    Given user is logged in as "EMPLOYEE" with username "johndoe" password "john123"
    Given the API is running
    Given header is set

  #werkt
  Scenario: Transfer money to another bank account
    Given I have 50 on my bank account
    When I transfer 20 to the other bank account
    Then I should have 30 on my bank account

  #werkt
  Example: Transfer money to another bank account
      | amount | balance |
      | 20     | 30      |
      | 30     | 20      |
      | 40     | 10      |
      | 50     | 0       |

  #werkt
  Scenario: Successfully get all transactions
    #Given logged in as "EMPLOYEE"
    #Given the API is running
    When a request is to GET "/transactions"
    Then the response should be 200
    And should contain a List of transaction objects

  #werk
  Example: Successfully get all transactions
      | id | amount | balance |
      | 1  | 20     | 30      |
      | 2  | 30     | 20      |

  #werkt
  Scenario: Successfully get a specific transaction by id
    Given a transaction with id 1 exists
    When a request is to GET a "/transactions/" by id 1
    Then the response should be 200 and contain the transaction with id 1

  #werkt
  Example: Successfully get a specific transaction by id
      | id   | amount | balance |
      | 9999 | 20     | 30      |

  #werkt
  Scenario: Try to get a non-existent transaction by id
    Given no "/transactions/" with id 9999 exists
    When a request is to GET a "/transactions/" by bad id 9999
    Then the error response should be 404

  #werkt
  Example: Try to get a non-existent transaction by id
      | id   | amount | balance |
      | 9999 | 20     | 30      |

  #werkt
  Scenario: Successfully create a transaction
    Given a transaction for €23 with description "Boodschappen" with accountFrom iban "NL05INHO0993038392" and accountTo iban "NL88INHO0001204817" exist
    When a request is to POST the "/transactions"
    Then the create response should be 201

  #werkt
  Example: Successfully create a transaction
        | amount | description | accountFrom | accountTo |
        | 23     | Boodschappen | NL05INHO0993038392 | NL88INHO0001204817 |

  # Scenario: Try to create a transaction with wrong input
  #   Given a transaction for €-23674 with description "Boodschappen" with accountFrom iban "NL05INHO0993038392" and accountTo iban "NL88INHO0001204817" exist
  #   When a request is to POST the "/transactions"
  #   Then the create response should be 400
  #   And the response message should be "Amount cannot be lower than zero!"

    