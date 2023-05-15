package nl.inholland.codegeneration.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;



@Entity
@Data
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@JsonDeserialize(as = Account.class)

public class Account {
    @Id
    @GenericGenerator(name = "ibangen", strategy = "nl.inholland.codegeneration.services.IBANGenerator")
    @GeneratedValue(generator = "ibangen")
    @Column(name = "iban")
    private String iban;

    @Column(name = "accountType", nullable = false, columnDefinition = "smallint default 1")
    @Enumerated()
    private AccountType accountType = AccountType.CURRENT;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "balance", nullable = false, columnDefinition = "Decimal(32,2) default '0.00'")
    private BigDecimal balance = new BigDecimal(0);

    @Column(name = "absoluteLimit", nullable = false, columnDefinition = "Decimal(32,2) default '1000.00'")
    private BigDecimal absoluteLimit = new BigDecimal(1000);

    @JsonProperty("customer")
    public void setCustomerById(Long userId) {
        // create a new user object from the given id
        User newUser = new User();
        newUser.setId(userId);
        this.user = newUser;
    }
}
