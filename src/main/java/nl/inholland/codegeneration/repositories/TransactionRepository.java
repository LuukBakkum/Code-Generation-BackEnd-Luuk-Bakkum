package nl.inholland.codegeneration.repositories;

import nl.inholland.codegeneration.models.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    @Query("SELECT t FROM Transaction t WHERE t.accountFrom.iban = :iban OR t.accountTo.iban = :iban")
    List<Transaction> findAllByAccountFromIban(@Param("iban") String iban);

    @Query("SELECT t FROM Transaction t WHERE t.accountFrom.iban = :iban  AND t.accountFrom.user.id = :userId")
    List<Transaction> findAllByAccountFromIbanAndUserId(@Param("iban") String iban, @Param("userId") Long id);

    @Query(value = "SELECT SUM(t.amount) FROM Transaction t WHERE TRUNC(t.timestamp) = CURDATE() AND t.accountFrom.user.id = :id")
    Optional<BigDecimal> findDailyTransactionsValueOfUser(@Param("id") Long id);
}
