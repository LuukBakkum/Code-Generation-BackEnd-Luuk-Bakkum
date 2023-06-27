package nl.inholland.codegeneration.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import nl.inholland.codegeneration.models.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    List<Account> findAllByIsDeletedFalse();

    Optional<Account> findByIbanAndIsDeletedFalse(String iban);

    List<Account> findAllByUserIdAndIsDeletedFalse(Long id);


}
