package nl.inholland.codegeneration.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import nl.inholland.codegeneration.models.DTO.request.UserRequestDTO;
import nl.inholland.codegeneration.models.DTO.request.UserUpdateRequestDTO;
import nl.inholland.codegeneration.models.DTO.response.UserResponseDTO;
import nl.inholland.codegeneration.services.mappers.UserDTOMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import nl.inholland.codegeneration.exceptions.APIException;
import nl.inholland.codegeneration.models.Account;
import nl.inholland.codegeneration.models.QueryParams;
import nl.inholland.codegeneration.models.User;
import nl.inholland.codegeneration.repositories.AccountRepository;
import nl.inholland.codegeneration.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAll(QueryParams<User> queryParams, @Nullable Boolean hasAccount) throws Exception {
        Specification<User> specification = queryParams.buildFilter();
        if (hasAccount != null) {
            specification = addHasAccountSpecification(hasAccount, specification);
        }

        // Get page with query params
        Page<User> userPage = userRepository.findAll(specification, PageRequest.of(queryParams.getPage(), queryParams.getLimit()));
        // Map page to list of UserResponseDTOs with userDTOMapper
        return userPage.getContent().stream().map(userDTOMapper.toResponseDTO).collect(Collectors.toList());
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found!"));
        return userDTOMapper.toResponseDTO.apply(user);
    }

    public UserResponseDTO add(UserRequestDTO userRequestDTO) {
        User user = userDTOMapper.toUser.apply(userRequestDTO);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsDeleted(false);
        return userDTOMapper.toResponseDTO.apply(userRepository.save(user));
    }

    public UserResponseDTO update(UserUpdateRequestDTO userUpdateRequestDTO, Long id) {
        if (!Objects.equals(id, userUpdateRequestDTO.id())) {
            throw new IllegalStateException("Id in request body must match id in url!");
        }
        User user = userDTOMapper.toUser.apply(userUpdateRequestDTO);
        if (!user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found!"));
        existingUser.update(user);
        return userDTOMapper.toResponseDTO.apply(userRepository.save(existingUser));
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws APIException {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found!"));
        if (user.getIsDeleted()) {
            throw new EntityNotFoundException("User not found!");
        }
        user.setIsDeleted(true);
        User deletedUser = userRepository.save(user);
        // Get all accounts of deleted user
        List<Account> accounts = accountRepository.findAllByUserIdAndIsDeletedFalse(id);
        // Delete all accounts of deleted user
        accounts.forEach(account -> account.setIsDeleted(true));
        List<Account> deletedAccounts = accountRepository.saveAll(accounts);
        // Check user is deleted and all accounts are deleted
        if (!deletedUser.getIsDeleted() || deletedAccounts.stream().anyMatch(account -> !account.getIsDeleted())) {
            throw new APIException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        }
    }

    // Add account specification to current specification
    private Specification<User> addHasAccountSpecification(boolean hasAccount, Specification<User> currentSpecification) {
        Specification<User> hasAccountSpecification = (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Subquery<Account> accountSubquery = query.subquery(Account.class);
            Root<Account> account = accountSubquery.from(Account.class);
            accountSubquery.select(account);
            accountSubquery.where(builder.equal(account.get("user"), root));
            return builder.exists(accountSubquery);
        };
        if (hasAccount) {
            return Specification.where(hasAccountSpecification).and(currentSpecification);
        }
        return Specification.not(hasAccountSpecification).and(currentSpecification);
    }
}
