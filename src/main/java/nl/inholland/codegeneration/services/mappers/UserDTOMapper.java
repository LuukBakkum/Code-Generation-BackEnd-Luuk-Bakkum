package nl.inholland.codegeneration.services.mappers;

import nl.inholland.codegeneration.models.DTO.request.MappableUserRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
import nl.inholland.codegeneration.models.Role;
import nl.inholland.codegeneration.models.User;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collectors;

import nl.inholland.codegeneration.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDTOMapper {
    private TransactionRepository transactionRepository;

    @Autowired
    public UserDTOMapper(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Function<MappableUserRequestDTO, User> toUser = (mappableUserRequestDTO) -> {
        User user = new User();
        user.setRoles(mappableUserRequestDTO.roles().stream().map(Role::fromInt).collect(Collectors.toList()));
        user.setUsername(mappableUserRequestDTO.username());
        user.setPassword(mappableUserRequestDTO.password());
        user.setFirstName(mappableUserRequestDTO.firstName());
        user.setLastName(mappableUserRequestDTO.lastName());
        user.setEmail(mappableUserRequestDTO.email());
        user.setPhoneNumber(mappableUserRequestDTO.phoneNumber());
        user.setBirthdate(mappableUserRequestDTO.birthdate());
        user.setDayLimit(mappableUserRequestDTO.dayLimit());
        user.setTransactionLimit(mappableUserRequestDTO.transactionLimit());
        return user;
    };

//    public Function<UserUpdateRequestDTO, User> toUserFromUpdate = (userUpdateRequestDTO) -> {
//        User user = new User();
//        user.setRoles(userUpdateRequestDTO.roles().stream().map(Role::fromInt).collect(Collectors.toList()));
//        user.setUsername(userUpdateRequestDTO.username());
//        user.setPassword(userUpdateRequestDTO.password());
//        user.setFirstName(userUpdateRequestDTO.firstName());
//        user.setLastName(userUpdateRequestDTO.lastName());
//        user.setEmail(userUpdateRequestDTO.email());
//        user.setPhoneNumber(userUpdateRequestDTO.phoneNumber());
//        user.setBirthdate(userUpdateRequestDTO.birthdate());
//        user.setDayLimit(userUpdateRequestDTO.dayLimit());
//        user.setTransactionLimit(userUpdateRequestDTO.transactionLimit());
//        return user;
//    };

    public Function<User, UserResponseDTO> toResponseDTO = (user) -> new UserResponseDTO(user, user.getDayLimit().subtract(this.transactionRepository.findDailyTransactionsValueOfUser(user.getId()).orElse(new BigDecimal(0))));
}
