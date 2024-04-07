package kg.devcats.internlabs.clientgateway.dto;

import kg.devcats.internlabs.core.entity.PaymentStatusEnum;

public class PaymentData {
    private Long paymentId;
    private PaymentStatusEnum paymentStatus;

    public PaymentData(Long paymentId, PaymentStatusEnum paymentStatus) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "paymentId=" + paymentId +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
