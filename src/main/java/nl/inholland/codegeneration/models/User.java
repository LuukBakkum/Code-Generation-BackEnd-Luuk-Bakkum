package nl.inholland.codegeneration.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Filterable
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.ORDINAL)
    @CollectionTable(name = "roles")
    @Column(name = "role")
    private List<Role> roles;

    @Filterable(role = Role.EMPLOYEE)
    @Column(name = "username", nullable = false, unique = true, precision = 255)
    private String username;

    @Column(name = "password", nullable = false, precision = 255)
    private String password;

    @Column(name = "firstName", nullable = false, precision = 255)
    private String firstName;

    @Column(name = "lastName", nullable = false, precision = 255)
    private String lastName;

    @Column(name = "email", nullable = false, precision = 255)
    private String email;

    @Column(name = "phoneNumber", nullable = false, precision = 255)
    private String phoneNumber;

    @Column(name = "birthdate", nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthdate;

    @Column(name = "dayLimit", nullable = false, precision = 32, scale = 2)
    private BigDecimal dayLimit = new BigDecimal("1000");

    @Column(name = "transactionLimit", nullable = false, precision = 32, scale = 2)
    private BigDecimal transactionLimit = new BigDecimal("200");

    @Filterable(role = Role.EMPLOYEE, defaultValue = "false")
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted = false;

    public User update(User user) {
        this.setRoles(user.getRoles());
        this.setUsername(user.getUsername());
        if (!user.getPassword().isEmpty()) {
            if (!(user.getPassword().length() >= 5)) {
                throw new ValidationException("Password is too short!");
            }
            this.setPassword(user.getPassword());
        }
        this.setFirstName(user.getFirstName());
        this.setLastName(user.getLastName());
        this.setEmail(user.getEmail());
        this.setPhoneNumber(user.getPhoneNumber());
        this.setBirthdate(user.getBirthdate());
        this.setDayLimit(user.getDayLimit());
        this.setTransactionLimit(user.getTransactionLimit());
        return this;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        // return true;
        return !this.getIsDeleted();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
