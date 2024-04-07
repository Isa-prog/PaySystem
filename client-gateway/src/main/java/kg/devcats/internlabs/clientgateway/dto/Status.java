package kg.devcats.internlabs.clientgateway.dto;

public enum Status {
    PAYMENT_COMPLETED("payment.completed"),
    REQUISITE_FOUND("requisite.found"),
    STATUS_FOUND("status.found"),
    ROLLBACK_COMPLETED("rollback.completed"),
    PAYMENT_EXISTS("payment.exists"),
    ACCOUNT_IS_BLOCKED,
    ACCOUNT_NOT_FOUND,
    SERVICE_NOT_FOUND,
    SERVICE_LIMIT,
    AMOUNT_NOT_CORRECT,
    DUPLICATE_PAYMENT,
    TOO_BIG_AMOUNT,
    TOO_SMALL_AMOUNT,
    CLIENT_NOT_FOUND,
    REQUISITE_NOT_FOUND,
    STATUS_NOT_FOUND,
    IS_CANCELLED,
    AMOUNT_IS_WRONG,
    REQUISITE_IS_NULL,
    SERVICE_IS_NULL,
    AMOUNT_IS_NULL,
    REQUEST_IS_NULL,
    PAYMENT_NOT_FOUND,
    ROLLBACK_DENIED,
    PAYMENT_STATUS_NOT_SUCCESS,
    ROLLBACK_TIME_LIMIT,
    ROLLBACK_AMOUNT_LIMIT,
    ROLLBACK_NOT_ALLOWED,
    NOT_ENOUGH_MONEY,
    NOT_ENOUGH_ACCOUNT_BALANCE,
    PAYMENT_NOT_CANCELLABLE,
    INVALID_DATE_RANGE,
    NO_PAYMENTS_CLIENT,
    PAYMENTS_NOT_FOUND,
    PAYMENTS_FOUND("payments.found"),
    NO_PAYMENTS_BY_CLIENT,
    OTHER;


    private String message;

    Status(String message) {
        this.message = message;
    }

    Status() {

    }

    public String getMessage() {
        return message;
    }
}
