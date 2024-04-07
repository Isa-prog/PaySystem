package kg.devcats.internlabs.core.exception;

public class UpdateBalanceException extends Exception {
    public UpdateBalanceException() {
        super();
    }

    public UpdateBalanceException(String message) {
        super(message);
    }

    public UpdateBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateBalanceException(Throwable cause) {
        super(cause);
    }
}
