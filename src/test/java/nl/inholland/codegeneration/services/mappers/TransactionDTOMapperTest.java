package nl.inholland.codegeneration.services.mappers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionDTOMapperTest {

    private TransactionDTOMapper transactionDTOMapper;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        transactionDTOMapper = new TransactionDTOMapper(accountRepository);
    }

    @Test
    void testToTransaction() {
        // Arrange
        Account mockAccount = new Account();
        when(accountRepository.findById("accountFromIban")).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findById("accountToIban")).thenReturn(Optional.of(mockAccount));

        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO("accountFromIban", "accountToIban", new BigDecimal("100.00"), "description");

        // Act
        Transaction transaction = transactionDTOMapper.toTransaction.apply(transactionRequestDTO);

        // Assert
        assertEquals(mockAccount, transaction.getAccountFrom());
        assertEquals(mockAccount, transaction.getAccountTo());
        assertEquals(transactionRequestDTO.amount(), transaction.getAmount());
        assertEquals(transactionRequestDTO.description(), transaction.getDescription());
    }

    @Test
    void testToResponseDTO() {
        // Arrange
        Account account = new Account();
        account.setIban("iban");
        account.setBalance(new BigDecimal("100.00"));
        account.setAbsoluteLimit(new BigDecimal("1000.00"));
        account.setAccountType(null);
        account.setUser(new User(null, null, null, null, null, null, null, null, null, null, null, null));

        Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        transaction.setAccountTo(account);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("description");

        // Act
        TransactionResponseDTO transactionResponseDTO = transactionDTOMapper.toResponseDTO.apply(transaction);

        // Assert
        assertNotNull(transactionResponseDTO);
    }

    @Test
    void testConvertToResponseDTO() {

        // Arrange
        Account account = new Account();
        account.setIban("iban");
        account.setBalance(new BigDecimal("100.00"));
        account.setAbsoluteLimit(new BigDecimal("1000.00"));
        account.setAccountType(null);
        account.setUser(new User(null, null, null, null, null, null, null, null, null, null, null, null));

        Transaction transaction = new Transaction();
        transaction.setAccountFrom(account);
        transaction.setAccountTo(account);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("description");

        // Act
        TransactionResponseDTO transactionResponseDTO = transactionDTOMapper.convertToResponseDTO(transaction);

        // Assert
        assertNotNull(transactionResponseDTO);
    }
}

