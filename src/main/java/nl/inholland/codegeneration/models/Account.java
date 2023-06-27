package nl.inholland.codegeneration.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = Account.class)
public class Account {
    @Filterable
    @Id
    @GenericGenerator(name = "ibangen", strategy = "nl.inholland.codegeneration.services.IBANGenerator")
    @GeneratedValue(generator = "ibangen")
    @Column(name = "iban")
    private String iban;

    @Column(name = "accountType", nullable = false, columnDefinition = "smallint default 1")
    @Enumerated()
    private AccountType accountType = AccountType.CURRENT;

    @NestedFilterable(nestedProperty = "id")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance", nullable = false, precision = 32, scale = 2)
    private BigDecimal balance = new BigDecimal(0);

    @Column(name = "absoluteLimit", nullable = false, precision = 32, scale = 2)
    private BigDecimal absoluteLimit = new BigDecimal(0);

    @Filterable(role = Role.EMPLOYEE, defaultValue = "false")
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    @JsonProperty("user")
    public void setUserById(Long userId) {
        // Create a new user object from the given id
        User newUser = new User();
        newUser.setId(userId);
        this.user = newUser;
    }
}
