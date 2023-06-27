package nl.inholland.codegeneration.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.*;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.services.mappers.TransactionDTOMapper;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import nl.inholland.codegeneration.repositories.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDTOMapper transactionDTOMapper;

    public List<TransactionResponseDTO> getAll(QueryParams<Transaction> queryParams) throws Exception {
        // Get page with query params
        Page<Transaction> transactionPage = transactionRepository.findAll(queryParams.buildFilter(), PageRequest.of(queryParams.getPage(), queryParams.getLimit()));
        // Map page to list of TransactionResponseDTOs with transactionDTOMapper
        return transactionPage.getContent().stream().map(transactionDTOMapper.toResponseDTO).collect(Collectors.toList());
    }

    public TransactionResponseDTO getById(Long i) {
        Transaction transaction = transactionRepository.findById(i).orElseThrow(() -> new EntityNotFoundException("Transaction not found!"));
        return transactionDTOMapper.toResponseDTO.apply(transaction);
    }

    @Transactional(rollbackOn = Exception.class)
    public TransactionResponseDTO add(TransactionRequestDTO transactionRequestDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Transaction transaction = transactionDTOMapper.toTransaction.apply(transactionRequestDTO);

        // Validate transaction
        ValidateTransaction(transaction, user);

        transaction.getAccountFrom().setBalance(transaction.getAccountFrom().getBalance().subtract(transaction.getAmount()));
        transaction.getAccountTo().setBalance(transaction.getAccountTo().getBalance().add(transaction.getAmount()));
        transaction.setId(null);
        transaction.setPerformingUser(user);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionDTOMapper.toResponseDTO.apply(transactionRepository.save(transaction));
    }

    private void ValidateTransaction(Transaction transaction, User user) {
        if (checkEqualIBAN(transaction)) {
            throw new InvalidDataAccessApiUsageException("Cannot make a transaction with the same account!");
        } else if (checkAccountPermissions(transaction, user)) {
            throw new InvalidDataAccessApiUsageException("Invalid bank account provided!");
        } else if (checkDeletedAccount(transaction)) {
            throw new InvalidDataAccessApiUsageException("Invalid bank account provided!");
        } else if (checkAmount(transaction)) {
            throw new IllegalStateException("Amount cannot be lower or equal to zero!");
        } else if (checkBalance(transaction)) {
            throw new IllegalStateException("Insufficient balance!");
        } else if (checkDailyLimit(transaction)) {
            throw new IllegalStateException("Amount cannot surpass daily limit!");
        } else if (checkTransactionLimit(transaction, user)) {
            throw new IllegalStateException("Amount cannot surpass transaction limit!");
        } else if (checkAccountType(transaction, transaction.getAccountFrom())) {
            throw new IllegalStateException("Cannot make a transaction from a savings account to an account that is not of the same user!");
        } else if (checkAccountType(transaction, transaction.getAccountTo())) {
            throw new IllegalStateException("Cannot make a transaction to a savings account to an account that is not of the same user!");
        }
    }

    // Check if IBAN from is not equal to IBAN to
    private boolean checkEqualIBAN(Transaction transaction) {
        return Objects.equals(transaction.getAccountFrom().getIban(), transaction.getAccountTo().getIban());
    }

    // If user is not employee; check if users don't match
    private boolean checkAccountPermissions(Transaction transaction, User user) {
        boolean isEmployee = user.getRoles().contains(Role.EMPLOYEE);
        boolean matchingUsers = Objects.equals(user.getId(), transaction.getAccountFrom().getUser().getId());
        return (!isEmployee && !matchingUsers);
    }

    // Check if account from or account to is not deleted
    private boolean checkDeletedAccount(Transaction transaction) {
        return (transaction.getAccountFrom().getIsDeleted() || transaction.getAccountTo().getIsDeleted());
    }

    // Check the transaction amount
    private boolean checkAmount(Transaction transaction) {
        return (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0);
    }

    // Check if absolute limit is surpassed when transaction amount is subtracted from current balance
    private boolean checkBalance(Transaction transaction) {
        BigDecimal balanceAfterSubtraction = transaction.getAccountFrom().getBalance().subtract(transaction.getAmount());
        return (transaction.getAccountFrom().getAbsoluteLimit().compareTo(balanceAfterSubtraction) > 0);
    }

    private boolean checkDailyLimit(Transaction transaction) {
        // Check account from user id
        Long accountFromUserId = transaction.getAccountFrom().getUser().getId();
        // Get current transactions value
        BigDecimal dailyTransactionsValue = transactionRepository.findDailyTransactionsValueOfUser(accountFromUserId).orElse(new BigDecimal(0));
        // Add the amount of the transaction
        dailyTransactionsValue = dailyTransactionsValue.add(transaction.getAmount());
        // Check if current transactions value with the amount of the transaction is greater than the day limit
        return (dailyTransactionsValue.compareTo(transaction.getAccountFrom().getUser().getDayLimit()) > 0);
    }

    // Check if amount is greater than transaction limit
    private boolean checkTransactionLimit(Transaction transaction, User user) {
        return (transaction.getAmount().compareTo(user.getTransactionLimit()) > 0);
    }

    // Check if account is savings and users do not match
    private boolean checkAccountType(Transaction transaction, Account account) {
        boolean isSavings = account.getAccountType() == AccountType.SAVINGS;
        boolean matchingUsers = Objects.equals(transaction.getAccountFrom().getUser().getId(), transaction.getAccountTo().getUser().getId());
        return (isSavings && !matchingUsers);
    }
}
