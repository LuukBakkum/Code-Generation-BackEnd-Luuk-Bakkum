package nl.inholland.codegeneration.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="accountFrom", nullable = false, referencedColumnName = "IBAN")
    private Account accountFrom;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="accountTo", nullable = false, referencedColumnName = "IBAN")
    private Account accountTo;

    @Column(name = "amount", nullable = false, columnDefinition = "Decimal(32,2)")
    private BigDecimal amount;

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