package kg.devcats.internlabs.core.dto.response;


public class GetRequisiteResult {
    private Status status;
    private DetailError detailError;
    private String detailMessageError;
    private String name;
    private boolean requisiteFound;


    public GetRequisiteResult(DetailError detailError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
    }

    public GetRequisiteResult(DetailError detailError, String detailMessageError, boolean requisiteFound) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.requisiteFound = requisiteFound;
    }

    public GetRequisiteResult(Status status, String name, boolean requisiteFound) {
        this.status = status;
        this.name = name;
        this.requisiteFound = requisiteFound;
    }

    public GetRequisiteResult(Status status, DetailError detailError, String detailMessageError, boolean requisiteFound) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.requisiteFound = requisiteFound;
    }

    public GetRequisiteResult(Status status, DetailError detailError, String detailMessageError, String name, boolean requisiteFound) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.name = name;
        this.requisiteFound = requisiteFound;
    }

    public GetRequisiteResult(Status success, String name, Boolean aBoolean) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequisiteFound() {
        return requisiteFound;
    }

    public void setRequisiteFound(boolean requisiteFound) {
        this.requisiteFound = requisiteFound;
    }

    public enum Status{
        SUCCESS, ERROR
    }

    public enum DetailError{
        IP_ADDRESS_FORBIDDEN,
        REQUISITE_NOT_FOUND("requisite.not.found"),
        SERVICE_NOT_FOUND("service.not.found"),
        AMOUNT_IS_WRONG("amount.not.found"),
        REQUISITE_IS_NULL("requisite.is.null"),
        SERVICE_IS_NULL("service.is.null"),
        ACCOUNT_IS_BLOCKED("account.is.blocked"),
        AMOUNT_IS_NULL("amount.is.null");
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
