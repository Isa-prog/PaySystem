package kg.devcats.internlabs.core.dto;

import kg.devcats.internlabs.core.entity.PaymentStatusEnum;

import java.time.LocalDateTime;

public class PaymentDetailsDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private PaymentStatusEnum status; // Make sure this matches the type in your entity

    public PaymentDetailsDTO(Long id, LocalDateTime createdAt, LocalDateTime paidAt, PaymentStatusEnum status) {
        this.id = id;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PaymentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PaymentStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentDetailsDTO{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", paidAt=" + paidAt +
                ", status='" + status + '\'' +
                '}';
    }

// Constructor, getters, and setters
}

