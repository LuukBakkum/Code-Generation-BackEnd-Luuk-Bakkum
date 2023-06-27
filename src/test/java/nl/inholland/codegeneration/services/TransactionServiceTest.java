package nl.inholland.codegeneration.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.inholland.codegeneration.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.EntityNotFoundException;

import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.services.mappers.TransactionDTOMapper;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionDTOMapper transactionDTOMapper;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TransactionService transactionService;

    private TransactionRequestDTO transactionRequestDTO;
    private Transaction validTransaction;
    private TransactionResponseDTO transactionResponseDTO;
    private User user;
    private Account accountFrom;
    private Account accountTo;

    private User authenticationUser = new User(null, null, null, null, null, null, null, null, null, null, null, null);

    @BeforeEach
    public void setup() {

        // preparing data (could be moved to the individual tests)
        user = new User(1L, null, null, null, null, null, null, null, null, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(2000), null);
        accountFrom = new Account("accountFromIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        accountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);

        transactionRequestDTO = new TransactionRequestDTO("accountFromIban", "accountToIban", BigDecimal.valueOf(100),
                "description");

        validTransaction = new Transaction(1L, LocalDateTime.now(), accountFrom, accountTo, BigDecimal.valueOf(100),
                authenticationUser, "description");
        authenticationUser = new User(null, Collections.singletonList(Role.EMPLOYEE), "sarawilson", "sara123", null,
                null, null, null, null, new BigDecimal(200), new BigDecimal(400), null);

        // security mocks

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationUser, "sara123",
                authenticationUser.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        transactionDTOMapper = new TransactionDTOMapper(accountRepository);
        transactionDTOMapper.toTransaction = mock(Function.class);
        transactionDTOMapper.toResponseDTO = mock(Function.class);
        transactionService = new TransactionService(transactionRepository, transactionDTOMapper);
    }

    @Test
    public void add_ValidTransaction_Success() {


        validTransaction = new Transaction(1L, LocalDateTime.now(), accountFrom, accountTo, BigDecimal.valueOf(100),
                authenticationUser, "description");
        user = new User(1L, null, null, null, null, null, null, null, null, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(2000), null);
        transactionResponseDTO = new TransactionResponseDTO(1L, LocalDateTime.now(), "accountFromIban", "sarawilson",
                "accountToIban", "sarawilson", BigDecimal.valueOf(100), "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(validTransaction);
        when(transactionRepository.findDailyTransactionsValueOfUser(anyLong()))
                .thenReturn(Optional.of(BigDecimal.valueOf(10)));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(validTransaction);
        when(transactionDTOMapper.toResponseDTO.apply(any(Transaction.class))).thenReturn(transactionResponseDTO);

        TransactionResponseDTO addedTransaction = transactionService.add(transactionRequestDTO);

        assertEquals(addedTransaction, transactionResponseDTO);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void add_BankAcountInvalid() {

        Account inValidAccountFrom = new Account("accountFromIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), true);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), true);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                BigDecimal.valueOf(100), authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        InvalidDataAccessApiUsageException exception = assertThrows(InvalidDataAccessApiUsageException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Invalid bank account provided!", exception.getMessage());

    }

    @Test
    public void add_AmountLowerThenZero() {
        BigDecimal amount = new BigDecimal("-100");

        Account inValidAccountFrom = new Account("accountFromIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                amount, authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Amount cannot be lower or equal to zero!", exception.getMessage());

//        assertThatThrownBy(() -> transactionService.add(transactionRequestDTO))
//                .isInstanceOf(IllegalStateException.class).hasMessage("Insufficient balance!");
    }

    @Test
    public void add_InsuficientBalance() {
        BigDecimal amount = new BigDecimal("10");
        BigDecimal balance = amount.subtract(new BigDecimal("1"));

        Account inValidAccountFrom = new Account("accountFromIban", AccountType.CURRENT, user, balance,
                new BigDecimal("120"), false);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                amount, authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Insufficient balance!", exception.getMessage());

    }

    // Amount cannot surpass daily limit!
    @Test
    public void add_pastDayLimit() {

        BigDecimal dayLimit = new BigDecimal("10");
        user.setDayLimit(dayLimit);

        Account inValidAccountFrom = new Account("accountFromIban", AccountType.CURRENT, user, new BigDecimal(200),
                new BigDecimal("120"), false);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                new BigDecimal("19"), authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Amount cannot surpass daily limit!", exception.getMessage());

    }

    // Amount cannot surpass transaction limit!
    @Test
    public void add_pastTransactionLimit() {

        BigDecimal transactionLimit = new BigDecimal("100");
        user.setDayLimit(new BigDecimal("1000"));
        user.setTransactionLimit(transactionLimit);

        authenticationUser.setTransactionLimit(transactionLimit);
        Account inValidAccountFrom = new Account("accountFromIban", AccountType.CURRENT, user, new BigDecimal(200),
                new BigDecimal("-1000"), false);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                new BigDecimal(200), authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Amount cannot surpass transaction limit!", exception.getMessage());

    }

    // Cannot make a transaction from a savings account to an account that is not of
    // the same user!
    @Test
    public void add_savingAcountAndToAccountNotSameUser() {


        user = new User(2L, null, null, null, null, null, null, null, null, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(2000), null);
        User user2 = new User(1L, null, null, null, null, null, null, null, null, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(2000), null);
        Account inValidAccountFrom = new Account("accountFromIban", AccountType.SAVINGS, user2, new BigDecimal(200),
                new BigDecimal("-1000"), false);
        Account inValidAccountTo = new Account("accountToIban", AccountType.CURRENT, user, new BigDecimal("120"),
                new BigDecimal("-1000"), false);
        Transaction inValidTransaction = new Transaction(1L, LocalDateTime.now(), inValidAccountFrom, inValidAccountTo,
                new BigDecimal(200), authenticationUser, "description");

        when(transactionDTOMapper.toTransaction.apply(transactionRequestDTO)).thenReturn(inValidTransaction);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> transactionService.add(transactionRequestDTO));

        assertEquals("Cannot make a transaction from a savings account to an account that is not of the same user!",
                exception.getMessage());

    }

    @Test
    void testGetAll() throws Exception {
        QueryParams<Transaction> queryParams = new QueryParams<>();
        PageRequest pageRequest = PageRequest.of(queryParams.getPage(), queryParams.getLimit());
        List<Transaction> transactionList = Arrays.asList(new Transaction(), new Transaction());
        List<TransactionResponseDTO> expectedResponseDTOList = Arrays.asList(new TransactionResponseDTO(transactionList.get(0)), new TransactionResponseDTO(transactionList.get(1)));

        when(transactionRepository.findAll(queryParams.buildFilter(), pageRequest)).thenReturn(new PageImpl<>(transactionList));
        when(transactionDTOMapper.toResponseDTO).thenReturn(mock(Function.class));
        when(transactionList.stream().map(transactionDTOMapper.toResponseDTO).collect(Collectors.toList())).thenReturn(expectedResponseDTOList);

 
        List<TransactionResponseDTO> actualResponseDTOList = transactionService.getAll(queryParams);

 
        assertEquals(expectedResponseDTOList, actualResponseDTOList);
        verify(transactionRepository, times(1)).findAll(queryParams.buildFilter(), pageRequest);
        verify(transactionDTOMapper, times(1)).toResponseDTO.apply(transactionList.get(0));
        verify(transactionList, times(1)).stream();
        verify(transactionDTOMapper.toResponseDTO, times(transactionList.size())).apply(any(Transaction.class));


    fail("Not implemented");
    }

    @Test
    void testGetById() {

        Long transactionId = 1L;

        TransactionResponseDTO expectedResponseDTO = new TransactionResponseDTO(validTransaction);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(validTransaction));
        when(transactionDTOMapper.toResponseDTO.apply(validTransaction)).thenReturn(expectedResponseDTO);

        TransactionResponseDTO actualResponseDTO = transactionService.getById(transactionId);

        assertEquals(expectedResponseDTO, actualResponseDTO);
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionDTOMapper.toResponseDTO, times(1)).apply(validTransaction);


    }

    @Test
    void testGetById_invalidTransaction() {

        Long transactionId = 1L;

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.getById(transactionId));

        assertEquals("Transaction not found!", exception.getMessage());
    }
}
