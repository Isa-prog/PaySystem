package kg.devcats.internlabs.admin.dto;

public enum Status {
    ROLLBACK_COMPLETED("rollback.completed"),
    PAYMENT_NOT_FOUND,
    PAYMENT_STATUS_NOT_SUCCESS,
    ROLLBACK_AMOUNT_LIMIT,
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
