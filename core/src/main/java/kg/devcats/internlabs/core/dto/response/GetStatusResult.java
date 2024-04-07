package kg.devcats.internlabs.core.dto.response;

public class GetStatusResult {
    private Status status;
    private DetailError detailError;
    private String detailMessageError;
    private String paymentStatus;

    public GetStatusResult(DetailError detailError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
    }

    public GetStatusResult(Status status, String paymentStatus) {
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public GetStatusResult(DetailError detailError, String detailMessageError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public GetStatusResult(Status status, DetailError detailError, String detailMessageError) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
    }

    public GetStatusResult(Status status, DetailError detailError, String detailMessageError, String payment_status) {
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

    public void setPaymentStatus(String payment_status) {
        this.paymentStatus = payment_status;
    }

    public enum Status {
        SUCCESS, ERROR
    }
    public enum DetailError{
        IP_ADDRESS_FORBIDDEN,
        REQUEST_IS_NULL("request.is.null"),
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
