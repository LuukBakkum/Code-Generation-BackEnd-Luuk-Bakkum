# /accounts


### GET        /accounts -> Employee
### GET        /accounts/{iban} -> Customer

- [x] If called by Customer; check if iban in url belongs to userId in JWT.

### GET        /accounts/{iban}/transactions -> Customer

- [x] If called by Customer; check if iban in url belongs to userId in JWT.

### GET        /accounts/{iban}/balance -> Customer

- [x] If called by Customer; check if iban in url belongs to userId in JWT.

### POST        /accounts -> Customer

- [x] Owner of account cannot have isDeleted set to true.
- [x] If called by Customer; check if userId in request body is equal to userId in JWT.
- [x] IBAN, balance, and isDeleted cannot be set, but use generated and/or default values.

### PUT        /accounts/{iban} -> Employee

- [x] IBAN, balance, user, and isDeleted are not to be changed.

### DELETE    /accounts/{iban} -> Customer

- [x] If called by Customer; check if iban in url belongs to userId in JWT