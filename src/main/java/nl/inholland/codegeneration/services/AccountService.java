package nl.inholland.codegeneration.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import nl.inholland.codegeneration.models.*;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.BalanceResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.TransactionResponseDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.services.mappers.AccountDTOMapper;
import nl.inholland.codegeneration.services.mappers.TransactionDTOMapper;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountDTOMapper accountDTOMapper;
    private final TransactionDTOMapper TransactionDTOMapper;

    public List<AccountResponseDTO> getAll(QueryParams<Account> queryParams) throws Exception {
        // Get page with query params
        Page<Account> accountPage = accountRepository.findAll(queryParams.buildFilter(), PageRequest.of(queryParams.getPage(), queryParams.getLimit()));
        // Map page to list of AccountResponseDTOs with accountDTOMapper
        return accountPage.getContent().stream().map(accountDTOMapper.toResponseDTO).collect(Collectors.toList());
    }

    public List<AccountResponseDTO> getAllByUserId(QueryParams<Account> queryParams, Long id) throws Exception {
        if (!userRepository.existsById(id)) {
            throw new APIException("User not found!", HttpStatus.NOT_FOUND, LocalDateTime.now());
        }

        queryParams.addFilter(new FilterCriteria("user.id", ":", id));
        List<Account> accounts = accountRepository.findAll(queryParams.buildFilter(), PageRequest.of(queryParams.getPage(), queryParams.getLimit())).getContent();

        return accounts.stream().map(accountDTOMapper.toResponseDTO).collect(Collectors.toList());
    }

    public AccountResponseDTO insertAccount(AccountRequestDTO request) {
        Account account = accountDTOMapper.toAccount.apply(request);
        if (account.getUser().getIsDeleted()) {
            throw new EntityNotFoundException("User not found!");
        } else if (!account.getUser().getRoles().contains(Role.CUSTOMER)) {
            throw new AccessDeniedException("Only customers can have accounts!");
        }
        
        Account addedAccount = new Account();
        addedAccount.setAccountType(account.getAccountType());
        addedAccount.setUser(account.getUser());
        addedAccount.setAbsoluteLimit(account.getAbsoluteLimit());
        return accountDTOMapper.toResponseDTO.apply(accountRepository.save(addedAccount));
    }

        //     throw new APIException("not accounts found", Htt
    public AccountResponseDTO getAccountByIban(String iban) throws APIException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findByIbanAndIsDeletedFalse(iban).orElseThrow(() -> new APIException("Account not found!", HttpStatus.NOT_FOUND, LocalDateTime.now()));
        CustomerIbanCheck(user, account);
        if (!user.getRoles().contains(Role.EMPLOYEE) && account.getIsDeleted()) {
            throw new EntityNotFoundException("Account not found!");
        }
        return accountDTOMapper.toResponseDTO.apply(account);
    }

    public AccountResponseDTO updateAccount(AccountRequestDTO request, String Iban) throws APIException {

        Account account = accountDTOMapper.toAccount.apply(request);

        if (account.getUser() == null) {
            throw new APIException("Unauthorized!", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        Optional<Account> updatedAccount = accountRepository.findByIbanAndIsDeletedFalse(Iban);

        Optional<User> user = userRepository.findById(account.getUser().getId());

        if (updatedAccount.isPresent() && user.isPresent()) {

            updatedAccount.get().setAccountType(account.getAccountType());
            updatedAccount.get().setUser(account.getUser());
            updatedAccount.get().setAbsoluteLimit(account.getAbsoluteLimit());

            return accountDTOMapper.toResponseDTO.apply(accountRepository.save(updatedAccount.get()));
        } else {
            throw new APIException("Account not for this user", HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        }

    }

    public void deleteAccount(String iban) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountRepository.findByIbanAndIsDeletedFalse(iban).orElseThrow(() -> new EntityNotFoundException("Account not found!"));
        if (!user.getRoles().contains(Role.EMPLOYEE) && !Objects.equals(account.getUser().getId(), user.getId())) {
            throw new InsufficientAuthenticationException("Forbidden!");
        }
        if(account.getIsDeleted()) {
            throw new EntityNotFoundException("Account not found!");
        }
        if (!BigDecimal.ZERO.equals(account.getBalance())) {
//            System.out.println(account.getBalance());
            throw new InvalidDataAccessApiUsageException("Account balance must be zero before deleting!");
        }
        account.setIsDeleted(true);
        accountRepository.save(account);
    }

    public List<TransactionResponseDTO> getTransactions(QueryParams<Transaction> queryParams, String iban) throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Account> account = this.accountRepository.findById(iban);
        if (account.isEmpty() || (!user.getRoles().contains(Role.EMPLOYEE) && account.get().getIsDeleted())) {
            throw new EntityNotFoundException("Account not found!");
        }
        CustomerIbanCheck(user, account.get());

        Specification<Transaction> specification = (Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Specification<Transaction> accountFromSpecification = new FilterSpecification<>(new FilterCriteria("iban", ":", iban), root.join("accountFrom"));
            predicates.add(accountFromSpecification.toPredicate(root, query, builder));
            Specification<Transaction> accountToSpecification = new FilterSpecification<>(new FilterCriteria("iban", ":", iban), root.join("accountTo"));
            predicates.add(accountToSpecification.toPredicate(root, query, builder));
            return builder.or(predicates.toArray(new Predicate[0]));
        };

        return transactionRepository.findAll(queryParams.buildFilter().and(specification)).stream()
                .map(TransactionDTOMapper.toResponseDTO).collect(Collectors.toList());
    }

    public BalanceResponseDTO getBalance(String iban) throws APIException {
        Account account = accountRepository.findById(iban).orElseThrow(() -> new EntityNotFoundException("Account not found!"));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomerIbanCheck(user, account);
        return accountDTOMapper.toBalanceDTO.apply(account);
    }

    private void CustomerIbanCheck(User user, Account account) throws APIException {
        if (!Objects.equals(account.getUser().getId(), user.getId()) && user.getRoles().contains(Role.CUSTOMER) && !user.getRoles().contains(Role.EMPLOYEE)) {
            throw new APIException("Forbidden!", HttpStatus.FORBIDDEN, LocalDateTime.now());
        }
    }
}