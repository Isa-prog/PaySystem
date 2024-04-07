package kg.devcats.internlabs.core.dto.response;

public class PutRollbackResult {
    private Status status;
    private DetailError detailError;
    private String detailMessageError;
    private String paymentStatus;

    public PutRollbackResult(DetailError detailError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
    }

    public PutRollbackResult(DetailError detailError, String detailMessageError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public PutRollbackResult(Status status, DetailError detailError, String detailMessageError) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public PutRollbackResult(Status status, String paymentStatus) {
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public PutRollbackResult(Status status, DetailError detailError, String detailMessageError, String paymentStatus) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public enum Status{
        SUCCESS, ERROR
    }
    public enum DetailError{
//        ROLLBACK_FAILED("rollback.failed"),
        IP_ADDRESS_FORBIDDEN,
        PAYMENT_STATUS_NOT_SUCCESS("payment.status.not.success"),
        ROLLBACK_AMOUNT_LIMIT("rollback.amount.limit"),
        ROLLBACK_NOT_ALLOWED("rollback.not.allowed"),
        NOT_ENOUGH_ACCOUNT_BALANCE("not.enough.account.balance"),
        PAYMENT_NOT_CANCELLABLE("payment.not.cancellable"),
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
