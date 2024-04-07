package kg.devcats.internlabs.core.dto.response;

import kg.devcats.internlabs.core.entity.PaymentStatusEnum;

public class PaymentResult {
    private Status status;
    private DetailError detailError;
    private String detailMessageError;
    private Long paymentId;
    private PaymentStatusEnum paymentStatus;

    public PaymentResult(Status status, Long paymentId, PaymentStatusEnum paymentStatus) {
        this.status = status;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }

    public PaymentResult(DetailError detailError, String detailMessageError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public PaymentResult(DetailError detailError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
    }

    public PaymentResult(DetailError detailError, String detailMessageError, Long paymentId, PaymentStatusEnum paymentStatus) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }

    public PaymentResult(Status status, DetailError detailError, String detailMessageError) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public PaymentResult(Status status, DetailError detailError, String detailMessageError, Long paymentId) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.paymentId = paymentId;
    }
    public PaymentResult(Status status, String detailMessageError) {
        this.status = status;
        this.detailMessageError = detailMessageError;
    }
    public PaymentResult(Status status, String detailMessageError, Long paymentId, PaymentStatusEnum paymentStatus) {
        this.status = status;
        this.detailMessageError = detailMessageError;
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DetailError getDetailError() {
        return detailError;
    }

    public void setDetailError(DetailError detailError) {
        this.detailError = detailError;
    }

    public String getDetailMessageError() {
        return detailMessageError;
    }

    public void setDetailMessageError(String detailMessageError) {
        this.detailMessageError = detailMessageError;
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

    public enum Status {
        SUCCESS, EXISTS, ERROR
    }

    public enum DetailError {
        IP_ADDRESS_FORBIDDEN,
        ACCOUNT_IS_BLOCKED("account.is.blocked"),
        ACCOUNT_NOT_FOUND("account.not.found"),
        SERVICE_NOT_FOUND("service.not.found"),
        SERVICE_LIMIT("service.limit"),
        AMOUNT_NOT_CORRECT("amount.not.correct"),
        DUPLICATE_PAYMENT("duplicate.payment"),
        TOO_BIG_AMOUNT("amount.too.big"),
        TOO_SMALL_AMOUNT("amount.too.small"),
        CLIENT_NOT_FOUND("client.not.found"),
        NOT_ENOUGH_MONEY("not.enough.money"),
        BALANCE_NOT_UPDATED("balance.not.updated"),
        OTHER("unknown.error"),
        PAYMENT_STATUS_NOT_IN_PROGRESS("payment.status.not.in.progress"),
        PAYMENT_NOT_FOUND("payment.not.found");

        private String message;

        DetailError(String message) {
            this.message = message;
        }

        DetailError() {

        }

        public String getMessage() {
            return message;
        }

    }
}
