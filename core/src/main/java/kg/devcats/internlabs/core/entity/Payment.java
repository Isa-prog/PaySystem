package kg.devcats.internlabs.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount")
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;

    @Column(name = "created_at")
    @NotNull(message = "Created at must not be null")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "requisite_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name="service_id")
    @NotNull(message = "Service must not be null")
    private Services services;

    @ManyToOne
    @JoinColumn(name="client_id")
    @NotNull(message = "Client must not be null")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name="status_id")
    private PaymentStatusEnum paymentStatus;

    @Column(name = "rollback_date")
    private LocalDateTime rollbackDate;

    @Column(name = "external_id")
    private Long externalId;


    public Payment() {
    }

    public Payment(BigDecimal amount, LocalDateTime createdAt, Account account, Services services, Client client, PaymentStatusEnum paymentStatus, Long externalId) {
        this.amount = amount;
        this.createdAt = createdAt;
        this.account = account;
        this.services = services;
        this.client = client;
        this.paymentStatus = paymentStatus;
        this.externalId = externalId;
    }

    public Payment(BigDecimal amount, LocalDateTime createdAt, LocalDateTime paidAt, Account account, Services services, Client client, PaymentStatusEnum paymentStatus, Long externalId) {
        this.amount = amount;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.account = account;
        this.services = services;
        this.client = client;
        this.paymentStatus = paymentStatus;
        this.externalId = externalId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Services getService() {
        return services;
    }

    public void setService(Services services) {
        this.services = services;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public LocalDateTime getRollbackDate() {
        return rollbackDate;
    }

    public void setRollbackDate(LocalDateTime rollbackDate) {
        this.rollbackDate = rollbackDate;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", account=" + account +
                ", service=" + services +
                ", client=" + client +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
