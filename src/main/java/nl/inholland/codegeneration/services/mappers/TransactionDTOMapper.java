package nl.inholland.codegeneration.services.mappers;

import jakarta.persistence.EntityNotFoundException;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TransactionDTOMapper {
    private AccountRepository accountRepository;

    @Autowired
    public TransactionDTOMapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Function<TransactionRequestDTO, Transaction> toTransaction = (transactionRequestDTO) -> {
        Transaction transaction = new Transaction();
        transaction.setAccountFrom(accountRepository.findById(transactionRequestDTO.accountFromIban()).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
        transaction.setAccountTo(accountRepository.findById(transactionRequestDTO.accountToIban()).orElseThrow(() -> new EntityNotFoundException("Account not found!")));
        transaction.setAmount(transactionRequestDTO.amount());
        transaction.setDescription(transactionRequestDTO.description());
        return transaction;
    };

    public Function<Transaction, TransactionResponseDTO> toResponseDTO = TransactionResponseDTO::new;

    public TransactionResponseDTO convertToResponseDTO(Transaction transaction) {
        return toResponseDTO.apply(transaction);
    }
}
