package nl.inholland.codegeneration.services;

import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.UserRepository;
import nl.inholland.codegeneration.services.mappers.UserDTOMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserDTOMapper userDTOMapper;
    @InjectMocks
    UserService userService;

    @Test
    public void testGetById_whenUserExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.getById(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetById_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        try {
            userService.getById(1L);
        } catch (EntityNotFoundException e) {
            // Expected exception
        }
    }

    @Test
    public void testAddUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        User user = new User();
        when(userDTOMapper.toUser.apply(userRequestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.add(userRequestDTO);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUser() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        User user = new User();
        user.setId(1L);
        when(userDTOMapper.toUser.apply(userRequestDTO)).thenReturn(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.update(userRequestDTO, 1L);
        verify(userRepository).save(user);
    }

    @Test
    public void testUpdateUser_invalidId() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        User user = new User();
        user.setId(2L);
        when(userDTOMapper.toUser.apply(userRequestDTO)).thenReturn(user);
        try {
            userService.update(userRequestDTO, 1L);
        } catch (InvalidDataAccessApiUsageException e) {
            // Expected exception
        }
    }

    @Test
    public void testDeleteUser() throws APIException {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.delete(1L);
        verify(userRepository).save(user);
    }
}