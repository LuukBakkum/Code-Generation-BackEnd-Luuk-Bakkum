package nl.inholland.codegeneration.configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.AccountType;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.Transaction;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.TransactionRepository;
import nl.inholland.codegeneration.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {
        private final UserRepository userRepository;
        private final AccountRepository accountRepository;
        private final TransactionRepository transactionRepository;
        private final PasswordEncoder passwordEncoder;

        // Test data
        @Override
        public void run(ApplicationArguments args) {
                // Users
                this.saveTestUsers();

                // Accounts
                this.saveTestAccounts();

                List<Account> accounts = accountRepository.findAll();
                // Transactions
                this.saveTestTransactions(accounts);
        }

        private void saveTestUsers() {
                userRepository.save(new User(null, List.of(Role.EMPLOYEE, Role.CUSTOMER), "meinbank",
                        passwordEncoder.encode("meinbank123"), "Mein",
                        "Bank", "admin@meinbank.com",
                        "06 00000001", LocalDate.of(2000, 1, 1), new BigDecimal(80000000), new BigDecimal(40000000),
                        false));
                userRepository.save(new User(null, List.of(Role.EMPLOYEE, Role.CUSTOMER), "johndoe",
                        passwordEncoder.encode("john123"), "John",
                        "Doe", "john.doe@gmail.com",
                        "06 71122334", LocalDate.of(2003, 9, 1), new BigDecimal(100000), new BigDecimal(1000),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "sarawilson",
                        passwordEncoder.encode("sara123"),
                        "Sara", "Wilson", "sara.wilson@yahoo.com",
                        "06 12345678", LocalDate.of(1990, 11, 13), new BigDecimal(2000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "tomlee", passwordEncoder.encode("tom123"),
                        "Tom",
                        "Lee", "tom.lee@hotmilf.com",
                        "06 42154678", LocalDate.of(1978, 7, 6), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "luukbakkum",
                        passwordEncoder.encode("luuk123"),
                        "Luuk", "Bakkum", "luuk.bakkum@gmail.com",
                        "06 51239876", LocalDate.of(2000, 3, 8), new BigDecimal(3000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "bownchown", passwordEncoder.encode("bob123"), "",
                        "",
                        "bob.brown@hotmale.com",
                        "06 20123456", LocalDate.of(1995, 9, 21), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "maryjo", passwordEncoder.encode("mary123"),
                        "Mary",
                        "Johnson", "mary.johnson@yahoo.com",
                        "06 12345678", LocalDate.of(1992, 12, 18), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "tomsmith", passwordEncoder.encode("tom123"),
                        "Tom",
                        "Smith", "tom.smith@hotmail.com",
                        "06 36549871", LocalDate.of(1987, 8, 4), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "saradoe", passwordEncoder.encode("sara123"),
                        "Sara",
                        "Doe", "sara.doe@gmail.com",
                        "06 87654321", LocalDate.of(1975, 6, 17), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "johnwilson",
                        passwordEncoder.encode("john123"),
                        "John", "Wilson", "john.wilson@yahoo.com",
                        "06 54321098", LocalDate.of(1983, 4, 29), new BigDecimal(1000), new BigDecimal(200),
                        false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "markbark",
                        passwordEncoder.encode("mark123"),
                        "Mark", "Bark", "mark.bark@gmail.com", "06 20587883", LocalDate.of(1903, 6, 13),
                        new BigDecimal(1000), new BigDecimal(200), true));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "boris24",
                        passwordEncoder.encode("boris123"),
                        "Boris", "de Beste", "boris@best.dev", "06 12345678", LocalDate.of(2005, 1, 13),
                        new BigDecimal(1000), new BigDecimal(200), false));
                userRepository.save(new User(null, List.of(Role.CUSTOMER), "floortje",
                        passwordEncoder.encode("floortje123"),
                        "Floortje", "Tjeertes", "floortje@tjeertes.dev", "06 00000001", LocalDate.of(1950, 11, 1),
                        new BigDecimal(10000), new BigDecimal(1000), false));
        }

        private void saveTestAccounts() {
                accountRepository.save(new Account(null, AccountType.CURRENT,
                        userRepository.findById(1L).orElseThrow(),
                        new BigDecimal("18000000000"), new BigDecimal("-100000000"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(2L).orElseThrow(),
                        new BigDecimal("436000"), new BigDecimal("-1000"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(3L).orElseThrow(),
                        new BigDecimal("520"), new BigDecimal("-2400"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(4L).orElseThrow(),
                        new BigDecimal("1100"), new BigDecimal("-2600"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(5L).orElseThrow(),
                        new BigDecimal("-25"), new BigDecimal("-300"), false));
                accountRepository.save(new Account(null, AccountType.SAVINGS, userRepository.findById(5L).orElseThrow(),
                        new BigDecimal("0"), new BigDecimal("-1200"), false));
                accountRepository.save(new Account(null, AccountType.SAVINGS, userRepository.findById(5L).orElseThrow(),
                        new BigDecimal("12000000"), new BigDecimal("-2600"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(6L).orElseThrow(),
                        new BigDecimal("110"), new BigDecimal("-2500"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(7L).orElseThrow(),
                        new BigDecimal("7000"), new BigDecimal("-1000"), false));
                accountRepository.save(new Account(null, AccountType.SAVINGS, userRepository.findById(7L).orElseThrow(),
                        new BigDecimal("1000"), new BigDecimal("-1000"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(8L).orElseThrow(),
                        new BigDecimal("-1100"), new BigDecimal("-2200"), false));
                accountRepository.save(new Account(null, AccountType.SAVINGS, userRepository.findById(8L).orElseThrow(),
                        new BigDecimal("1200"), new BigDecimal("-500"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(9L).orElseThrow(),
                        new BigDecimal("30000"), new BigDecimal("-6000"), false));
                accountRepository.save(new Account(null, AccountType.CURRENT, userRepository.findById(10L).orElseThrow(),
                        new BigDecimal("340"), new BigDecimal("-1500"), false));
                accountRepository.save(new Account(null, AccountType.SAVINGS, userRepository.findById(10L).orElseThrow(),
                        new BigDecimal("600"), new BigDecimal("-1000"), false));
        }

        private void saveTestTransactions(List<Account> accounts) {
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 5, 15, 18, 53, 11),
                        accounts.get(0), accounts.get(2), new BigDecimal("100"),
                        userRepository.findById(2L).orElseThrow(), "Die verdraaide belastingdienst!"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 27, 13, 28, 1),
                        accounts.get(3), accounts.get(4), new BigDecimal("5.50"),
                        userRepository.findById(3L).orElseThrow(), "Sushibox"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 5, 15, 13, 26, 54),
                        accounts.get(5), accounts.get(13), new BigDecimal("20000"),
                        userRepository.findById(9L).orElseThrow(), "Beretta AR70/90"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 27, 11, 45, 23),
                        accounts.get(9), accounts.get(1), new BigDecimal("12.50"),
                        userRepository.findById(7L).orElseThrow(), "Bowlen"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 5, 15, 15, 47, 47),
                        accounts.get(5), accounts.get(7), new BigDecimal("230000"),
                        userRepository.findById(5L).orElseThrow(), "Pizzo Maranzano, Basilicata, Veneto"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 25, 5, 33, 53),
                        accounts.get(11), accounts.get(5), new BigDecimal("100000"),
                        userRepository.findById(8L).orElseThrow(), "Maranzano pizzo"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 25, 3, 26, 25),
                        accounts.get(14), accounts.get(5), new BigDecimal("70000"),
                        userRepository.findById(10L).orElseThrow(), "Basilicata pizzo"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 24, 23, 52, 2),
                        accounts.get(0), accounts.get(5), new BigDecimal("50000"),
                        userRepository.findById(2L).orElseThrow(), "Veneto pizzo"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 24, 23, 13, 11),
                        accounts.get(12), accounts.get(11), new BigDecimal("37.20"),
                        userRepository.findById(8L).orElseThrow(), "Broodje ham"));
                transactionRepository.save(new Transaction(null, LocalDateTime.of(2023, 4, 24, 12, 54, 10),
                        accounts.get(14), accounts.get(13), new BigDecimal("12.50"),
                        userRepository.findById(10L).orElseThrow(), "Kaas"));
        }
}
