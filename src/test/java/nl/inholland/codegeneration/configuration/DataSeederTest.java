package nl.inholland.codegeneration.configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.AccountType;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DataSeederTest {

  @Mock
  UserRepository userRepository;

  @Mock
  AccountRepository accountRepository;

  @Mock
  TransactionRepository transactionRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  DataSeeder dataSeeder;

  @Test
  void testRun() throws Exception {

    ApplicationArguments args = mock(ApplicationArguments.class);
    User user = new User(1L, List.of(Role.CUSTOMER), "sarawilson", "sara123", "Sara", "Wilson",
        "sara.wilson@yahoo.com",
        "0612345678", LocalDate.of(1990, 11, 13), new BigDecimal(1000), new BigDecimal(200),
        false);

    Account account = new Account("NL88INHO0001204817", AccountType.CURRENT, user, new BigDecimal("120"),
        new BigDecimal("-1000"), false);
    List<Account> accounts = List.of(account, account, account, account, account, account, account, account, account,
        account, account, account, account, account, account);

    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

    when(accountRepository.findAll()).thenReturn(accounts);

    dataSeeder.run(args);

    Mockito.verify(userRepository, Mockito.times(13)).save(Mockito.any(User.class));

    Mockito.verify(accountRepository, Mockito.times(15)).save(Mockito.any(Account.class));

    Mockito.verify(transactionRepository, Mockito.times(10)).save(Mockito.any(Transaction.class));

  }
}
