package kg.devcats.internlabs.core.dto.response;

public class GetFromToResult {
    private Status status;
    private DetailError detailError;
    private String detailMessageError;
    private String name;
    private boolean requisitesFound;

    public GetFromToResult(Status success, String payment_name) {
    }
    public GetFromToResult(DetailError detailError) {
        this.status = Status.ERROR;
        this.detailError = detailError;
    }

    public GetFromToResult(DetailError detailError, String detailMessageError, boolean requisitesFound) {
        this.status = Status.ERROR;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.requisitesFound = requisitesFound;
    }

    public GetFromToResult(Status status, String name, boolean requisitesFound) {
        this.status = status;
        this.name = name;
        this.requisitesFound = requisitesFound;
    }

    public GetFromToResult(Status status, DetailError detailError, String detailMessageError, String name) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.name = name;
    }

    public GetFromToResult(Status status, DetailError detailError, String detailMessageError, String name, boolean requisitesFound) {
        this.status = status;
        this.detailError = detailError;
        this.detailMessageError = detailMessageError;
        this.name = name;
        this.requisitesFound = requisitesFound;
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

    public boolean isRequisitesFound() {
        return requisitesFound;
    }

    public void setRequisitesFound(boolean requisitesFound) {
        this.requisitesFound = requisitesFound;
    }

    public enum Status{
        SUCCESS, ERROR
    }

    public enum DetailError{
        IP_ADDRESS_FORBIDDEN,
        PAYMENTS_NOT_FOUND("payments.not.found"),
        INVALID_DATE_RANGE("invalid.date.range"),
        NO_PAYMENTS_BY_CLIENT("no.payments.client");

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
