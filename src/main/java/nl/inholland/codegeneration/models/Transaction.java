package nl.inholland.codegeneration.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Filterable
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @NestedFilterable(nestedProperty = {"user", "iban"})
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="accountFrom", nullable = false, referencedColumnName = "IBAN")
    private Account accountFrom;

    @NestedFilterable(nestedProperty = {"user", "iban"})
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="accountTo", nullable = false, referencedColumnName = "IBAN")
    private Account accountTo;

    @Filterable
    @Column(name = "amount", nullable = false, columnDefinition = "Decimal(32,2)")
    private BigDecimal amount;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="userId", nullable = false)
    private User performingUser;

    @Column(name = "description", nullable = false, columnDefinition = "varchar(255)")
    private String description;

    public Transaction update(Transaction transaction) {
        this.setTimestamp(transaction.getTimestamp());
        this.setAccountFrom(transaction.getAccountFrom());
        this.setAccountTo(transaction.getAccountTo());
        this.setAmount(transaction.getAmount());
        this.setPerformingUser(transaction.getPerformingUser());
        this.setDescription(transaction.getDescription());
        return this;
    }
}