package nl.inholland.codegeneration.services.mappers;

import nl.inholland.codegeneration.models.*;
import nl.inholland.codegeneration.models.DTO.request.AccountRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.AccountResponseDTO;
import nl.inholland.codegeneration.models.DTO.response.BalanceResponseDTO;
import nl.inholland.codegeneration.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class AccountDTOMapperTest {
    @Mock
    private UserRepository userRepository;

    private AccountDTOMapper accountDTOMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountDTOMapper = new AccountDTOMapper(userRepository);
    }

    @Test
    public void testToAccount() {
        AccountRequestDTO dto = new AccountRequestDTO(1L, null, 0);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        Account account = accountDTOMapper.toAccount.apply(dto);

        assertNotNull(account);
        assertEquals(user, account.getUser());
        assertEquals(dto.absoluteLimit(), account.getAbsoluteLimit());
        assertEquals(AccountType.fromInt(dto.accountType()), account.getAccountType());
    }

    @Test
    public void testToResponseDTO() {
        Account account = new Account();
        User user = new User();
        user.setUsername("TestUser");
        account.setUser(user); // Set User object to Account

        AccountResponseDTO dto = accountDTOMapper.toResponseDTO.apply(account);

        assertNotNull(dto);
        assertEquals(user.getUsername(), dto.username()); // Check if the username is correctly set
    }

    @Test
    public void testToBalanceDTO() {
        Account account = new Account();

        BalanceResponseDTO dto = accountDTOMapper.toBalanceDTO.apply(account);

        assertNotNull(dto);
    }
}