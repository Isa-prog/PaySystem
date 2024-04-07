package kg.devcats.internlabs.core.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "requisite", columnDefinition="CHAR(12)")
    @NotNull(message = "Requisite must not be null")
    private String requisite;

    @Column(name = "balance")
    @NotNull(message = "Balance must not be null")
    private BigDecimal balance;

    @Column(name = "full_name")
    @NotNull(message = "Full name must not be null")
    private String fullName;

    @Column(name = "is_blocked")
    @NotNull(message = "IsBlocked must not be null")
    private Boolean isBlocked;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public Account() {
    }

    public Account(String requisite, BigDecimal balance, String fullName, Boolean isBlocked, LocalDateTime deletedAt) {
        this.requisite = requisite;
        this.balance = balance;
        this.fullName = fullName;
        this.isBlocked = isBlocked;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequisite() {
        return requisite;
    }

    public void setRequisite(String requisite) {
        this.requisite = requisite;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", requisite='" + requisite + '\'' +
                ", balance=" + balance +
                ", fullName='" + fullName + '\'' +
                ", isBlocked=" + isBlocked +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
