package nl.inholland.codegeneration.services;

import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.DTO.request.ATMRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.TransactionRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.services.mappers.TransactionDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ATMService extends TransactionService {
    @Autowired
    public ATMService(TransactionRepository transactionRepository, TransactionDTOMapper transactionDTOMapper) {
        super(transactionRepository, transactionDTOMapper);
    }

    public TransactionResponseDTO deposit(ATMRequestDTO atmRequestDTO) {
        return this.add(new TransactionRequestDTO(IBANGenerator.getMeinBankIBAN(), atmRequestDTO.accountIban(), atmRequestDTO.amount(), "Mock ATM deposit at Mein Bank HQ"));
    }

    public TransactionResponseDTO withdraw(ATMRequestDTO atmRequestDTO) {
        return this.add(new TransactionRequestDTO(atmRequestDTO.accountIban(), IBANGenerator.getMeinBankIBAN(), atmRequestDTO.amount(), "Mock ATM withdrawal at Mein Bank HQ"));
    }
}