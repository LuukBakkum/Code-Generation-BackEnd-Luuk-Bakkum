package nl.inholland.codegeneration.services;

import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.AccountType;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.BalanceResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.services.mappers.AccountDTOMapper;
import nl.inholland.codegeneration.services.mappers.TransactionDTOMapper;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
        @Mock
        private AccountRepository accountRepository;
        @Mock
        private UserRepository userRepository;
        @Mock
        private TransactionRepository transactionRepository;
        @Mock
        private AccountDTOMapper accountDTOMapper;
        @Mock
        private TransactionDTOMapper transactionDTOMapper;
        @Mock
        private TransactionService transactionService;

        @InjectMocks
        private AccountService accountService;

        User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

        @BeforeEach
        public void setup() {

                // security mocks

                authenticationUser.setUsername("sarawilson");
                authenticationUser.setPassword("sara123");
                authenticationUser.setRoles(Collections.singletonList(Role.EMPLOYEE)); // Assuming the user has the role
                                                                                       // "EMPLOYEE"

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                authenticationUser,
                                "sara123",
                                authenticationUser.getAuthorities());

                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authentication);

                // mapper mocks
                accountDTOMapper = new AccountDTOMapper(userRepository);
                accountDTOMapper.toAccount = Mockito.mock(Function.class);
                accountDTOMapper.toResponseDTO = Mockito.mock(Function.class);
                accountService = new AccountService(accountRepository, userRepository, transactionRepository,
                                accountDTOMapper,
                                transactionDTOMapper);
        }

        @Test
        @WithMockUser(roles = { "EMPLOYEE" }, username = "user")
        public void testGetAccountByIban_whenAccountExists() throws APIException {
                // make test user
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);

                // make test account
                String iban = "NL88INHO0001204817";
                Account account = new Account(iban, AccountType.CURRENT, user, new BigDecimal("120"),
                                new BigDecimal("-1000"),
                                false);

                AccountResponseDTO responseDTO = new AccountResponseDTO(iban, 0, iban, null, null);
                // when the accountRepository is called with the iban, return the account
                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(Optional.of(account));
                // when the AccountDTOMapper is called with the account, return the responseDTO
                when(accountDTOMapper.toResponseDTO.apply(account)).thenReturn(responseDTO);

                AccountResponseDTO result = accountService.getAccountByIban(iban);

                // check if the accountRepository is called once
                verify(accountRepository).findByIbanAndIsDeletedFalse(iban);
                // check if the AccountDTOMapper is called once to asure that a responseDTO is
                // returned
                verify(accountDTOMapper.toResponseDTO).apply(account);
                // check if the result is the same as the expected responseDTO
                // assertEquals(responseDTO, result);
                assertEquals(responseDTO, result);

        }

        @Test
        @WithMockUser(roles = { "EMPLOYEE" }, username = "user")
        public void testGetAccountByIban_whenAccountDoesNotExists() throws APIException {

                String iban = "NL88INHO0001204817";
                String iban2 = "NL88INHO0001204818";

                // make test user
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);

                // make test account

                Account account = new Account(iban2, AccountType.CURRENT, user, new BigDecimal("120"),
                                new BigDecimal("-1000"),
                                false);

                // reponse to be expected
                AccountResponseDTO responseDTO = new AccountResponseDTO(iban, 0, user.getUsername(), null, null);
                AccountResponseDTO responseDTO2 = new AccountResponseDTO(iban2, 0, user.getUsername(), null, null);
                // when the accountRepository is called with the iban, return the account
                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(Optional.of(account));
                // when the AccountDTOMapper is called with the account, return the responseDTO
                when(accountDTOMapper.toResponseDTO.apply(account)).thenReturn(responseDTO2);

                AccountResponseDTO result = accountService.getAccountByIban(iban);

                // check if the accountRepository is called once
                verify(accountRepository).findByIbanAndIsDeletedFalse(iban);
                // check if the AccountDTOMapper is called once to asure that a responseDTO is
                // returned
                verify(accountDTOMapper.toResponseDTO).apply(account);
                // check if the result is the same as the expected responseDTO
                assertNotEquals(responseDTO, result);

        }

        @Test
        @WithMockUser(roles = { "EMPLOYEE" }, username = "user")
        public void testInsertAccount() {

                String iban = "NL88INHO0001204817";
                // make test user
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "test.test@gmail.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);

                AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal("5000"),
                                AccountType.SAVINGS.getValue());
                Account addedAccount = new Account(iban, AccountType.SAVINGS, user, new BigDecimal("5000"),
                                new BigDecimal("0"), false);
                AccountResponseDTO expectedResponse = new AccountResponseDTO(iban, 0, iban, null, null);

                when(accountDTOMapper.toAccount.apply(requestDTO)).thenReturn(addedAccount);
                when(accountRepository.save(any(Account.class))).thenReturn(addedAccount);
                when(accountDTOMapper.toResponseDTO.apply(addedAccount)).thenReturn(expectedResponse);

                AccountResponseDTO result = accountService.insertAccount(requestDTO);

                // Verify the result
                assertNotNull(result);

                // Verify the method calls
                verify(accountRepository).save(any(Account.class));
        }

        @Test
        @WithMockUser(roles = { "EMPLOYEE" }, username = "user")
        public void testInsertAccount_userDoesNotExist() {

                String iban = "NL88INHO0001204817";
                // make test user
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "test.test@gmail.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                true);

                AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal("5000"),
                                AccountType.SAVINGS.getValue());
                Account addedAccount = new Account(iban, AccountType.SAVINGS, user, new BigDecimal("5000"),
                                new BigDecimal("0"), false);

                when(accountDTOMapper.toAccount.apply(requestDTO)).thenReturn(addedAccount);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.insertAccount(requestDTO));
                assertEquals("User not found!", exception.getMessage());

        }

        @Test
        void testGetAll() throws Exception {
                Specification<Account> specification = mock(FilterSpecification.class);
                Pageable pageRequest = PageRequest.of(0, 12);
                List<Account> accounts = new ArrayList<>();
                accounts.add(new Account("NL88INHO0001204817", AccountType.CURRENT, null, null, null, false));
                accounts.add(new Account("NL44INHO0003204819", AccountType.SAVINGS, null, null, null, false));

                when(accountRepository.findAll(specification, pageRequest).getContent()).thenReturn(accounts);
                // TODO: Mock the behavior of the Stream class
                when(accounts.stream().map(accountDTOMapper.toResponseDTO)
                                .collect(Collectors.toList())).thenReturn(null);

                List<AccountResponseDTO> result = accountService.getAll(null);

                assertNotNull(result);
                assertEquals(accounts.size(), result.size());
                assertEquals(accounts.stream().map(AccountResponseDTO::new).collect(Collectors.toList()), result);
        }

        @Test
        void testGetBalance() throws APIException {
                String iban = "NL88INHO0001204817";
                BigDecimal balance = new BigDecimal("1000");
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);

                Account existingAccount = new Account(iban, AccountType.CURRENT, user, balance, new BigDecimal(500),
                                false);
                Optional<Account> Account = Optional.of(existingAccount);

                when(accountRepository.findById(iban)).thenReturn(Account);

                BalanceResponseDTO result = accountService.getBalance(iban);

                assertDoesNotThrow(() -> accountService.getBalance(iban));
                assertEquals(result.balance(), existingAccount.getBalance());
                verify(accountRepository, times(2)).findById(iban);

        }

        @Test
        public void testGetBalance_nonExistingAccount() {
                String iban = "NL88INHO0001204817";
                Optional<Account> Account = Optional.empty();

                when(accountRepository.findById(iban)).thenReturn(Account);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.getBalance(iban));
                assertEquals("Account not found!", exception.getMessage());
                verify(accountRepository).findById(iban);

        }

        @Test
        void testGetTransactions() throws Exception {
                //QueryParams<Transaction> queryParams = new QueryParams<>(); // Add necessary arguments
                //List<TransactionResponseDTO> transactions = transactionService.getTransactions(queryParams, iban);
                //assertNotNull(transactions); // Checking if the transactions are not null
                // Add more assertions here depending on your requirements
        }

        @Test
        void testGetTransactions_AccountNotPressent() {
                String iban = "NL88INHO0001204817";

                QueryParams<Transaction> queryParams = new QueryParams<>();

                Optional<Account> Account = Optional.empty();

                when(accountRepository.findById(iban)).thenReturn(Account);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.getTransactions(queryParams, iban));

                assertEquals("Account not found!", exception.getMessage());

        }

        @Test
        void testGetTransactions_UserAccountNotDeleted() {

                String iban = "NL88INHO0001204817";

                QueryParams<Transaction> queryParams = new QueryParams<>();
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);

                authenticationUser.setRoles(Collections.singletonList(Role.CUSTOMER)); // Assuming the user has the role
                                                                                       // "EMPLOYEE"

                Account existingAccount = new Account(iban, AccountType.CURRENT, user, new BigDecimal("120"),
                                new BigDecimal("-1000"), true);

                Optional<Account> Account = Optional.of(existingAccount);

                when(accountRepository.findById(iban)).thenReturn(Account);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.getTransactions(queryParams, iban));

                assertEquals("Account not found!", exception.getMessage());
        }

        @Test
        public void testGetAllByUserId() throws Exception {
                Long userId = 1L;
                User user = new User(userId, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);
                List<Account> accounts = new ArrayList<>();
                accounts.add(new Account("NL88INHO0001204817", AccountType.CURRENT, user, new BigDecimal("120"),
                                new BigDecimal("-1000"), false));
                accounts.add(new Account("NL44INHO0003204819", AccountType.SAVINGS, user, new BigDecimal("500"),
                                new BigDecimal("0"), false));

                List<AccountResponseDTO> expectedResponse = accounts.stream().map(AccountResponseDTO::new)
                                .collect(Collectors.toList());

                when(userRepository.existsById(userId)).thenReturn(true);
                when(accountRepository.findAllByUserIdAndIsDeletedFalse(userId)).thenReturn(accounts);
                when((List<AccountResponseDTO>) accounts.stream().map(AccountResponseDTO::new)
                                .collect(Collectors.toList()))
                                .thenReturn(accounts.stream().map(AccountResponseDTO::new)
                                                .collect(Collectors.toList()));

                // TODO: Mock the behavior of the Stream class

                fail("Not yet implemented");

                List<AccountResponseDTO> result = accountService.getAllByUserId(null, userId);

                assertNotNull(result);
                assertEquals(expectedResponse.size(), result.size());
                assertEquals(expectedResponse, result);

                verify(userRepository).existsById(userId);
                verify(accountRepository).findAllByUserIdAndIsDeletedFalse(userId);
        }

        @Test
        public void getAllByUserId_userDoesNotExist() {
                Long userId = 1L;

                when(userRepository.existsById(userId)).thenReturn(false);

                APIException exception = assertThrows(APIException.class,
                                () -> accountService.getAllByUserId(null, userId));
                assertEquals("User not found!", exception.getMessage());
                assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

                verify(userRepository).existsById(userId);
        }

        @Test
        void testDeleteAccount_nonExistingAccount() {

                String iban = "NL88INHO0001204817";
                Optional<Account> addedAccount = Optional.empty();

                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(addedAccount);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.deleteAccount(iban));
                assertEquals("Account not found!", exception.getMessage());
        }

        @Test
        void testDeleteAccount_deletedAccount() {
                String iban = "NL88INHO0001204817";
                Account account = new Account(iban, AccountType.CURRENT, null, null, null, true);

                Optional<Account> DeletedAccount = Optional.of(account);

                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(DeletedAccount);

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                                () -> accountService.deleteAccount(iban));
                assertEquals("Account not found!", exception.getMessage());

        }

        @Test
        void testDeleteAccount_BalanceNotZero() {
                String iban = "NL88INHO0001204817";
                Account existingAccount = new Account(iban, AccountType.CURRENT, null, new BigDecimal(10), null,
                                false);

                Optional<Account> addedAccount = Optional.of(existingAccount);

                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(addedAccount);

                InvalidDataAccessApiUsageException exception = assertThrows(InvalidDataAccessApiUsageException.class,
                                () -> accountService.deleteAccount(iban));
                assertEquals("Account balance must be zero before deleting!", exception.getMessage());
        }

        @Test
        void testUpdateAccount() throws APIException {
                String iban = "NL88INHO0001204817";
                AccountRequestDTO requestDTO = new AccountRequestDTO(1L, new BigDecimal(100), 0);
                User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
                                "sara.wilson@yahoo.com",
                                "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
                                false);
                Account existingAccount = new Account(iban, AccountType.CURRENT, user, null, null, null);

                Account updatedAccount = new Account(iban, AccountType.SAVINGS, user, null, null, null);
                AccountResponseDTO expectedResponse = new AccountResponseDTO(updatedAccount);

                // Mock the repository methods
                when(accountDTOMapper.toAccount.apply(requestDTO)).thenReturn(updatedAccount);
                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(Optional.of(existingAccount));
                when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
                when(accountRepository.save(existingAccount)).thenReturn(updatedAccount);
                when(accountDTOMapper.toResponseDTO.apply(updatedAccount)).thenReturn(expectedResponse);

                // Call the method under test
                AccountResponseDTO result = accountService.updateAccount(requestDTO, iban);

                // Assert the result
                assertNotNull(result);
                // Assert other expected values in the result

                // Verify the method calls
                verify(accountDTOMapper.toAccount).apply(requestDTO);
                verify(accountRepository).findByIbanAndIsDeletedFalse(iban);
                verify(userRepository).findById(user.getId());
                verify(accountRepository).save(existingAccount);
                verify(accountDTOMapper.toResponseDTO).apply(updatedAccount);
        }

        @Test
        public void testUpdateAccount_Unauthorized() {
                String iban = "NL88INHO0001204817";
                AccountRequestDTO requestDTO = new AccountRequestDTO(null, null, AccountType.CURRENT.getValue()); // Provide
                                                                                                                  // necessary
                                                                                                                  // data
                                                                                                                  // for
                                                                                                                  // account
                                                                                                                  // update
                Account existingAccount = new Account(iban, AccountType.CURRENT, null, null, null, null); // Existing
                                                                                                          // account
                                                                                                          // with no
                                                                                                          // user

                when(accountDTOMapper.toAccount.apply(requestDTO)).thenReturn(existingAccount);

                APIException exception = assertThrows(APIException.class,
                                () -> accountService.updateAccount(requestDTO, iban));
                assertEquals("Unauthorized!", exception.getMessage());
                assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        }

        @Test
        public void testUpdateAccount_AccountNotFound() {
                 String iban = "NL88INHO0001204817";
                 AccountRequestDTO requestDTO = new AccountRequestDTO(null, null, 0); // Provide necessary data for account update
                 User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",null,null,null,null,null,null);
                 Account account = new Account(iban, AccountType.CURRENT, user, null, null, null); // Existing account with no user
                 Optional <User> existingUser = Optional.of(user);
                 Optional <Account> existingAccount = Optional.empty();



                when(accountDTOMapper.toAccount.apply(requestDTO)).thenReturn(account);
                when(accountRepository.findByIbanAndIsDeletedFalse(iban)).thenReturn(existingAccount);
                when(userRepository.findById(anyLong())).thenReturn(existingUser);

                APIException exception = assertThrows(APIException.class,
                                () -> accountService.updateAccount(requestDTO, iban));
                assertEquals("Account not for this user", exception.getMessage());
                assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

        }
}
