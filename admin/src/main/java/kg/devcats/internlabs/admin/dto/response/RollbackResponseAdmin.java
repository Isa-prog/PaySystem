package kg.devcats.internlabs.admin.dto.response;


import kg.devcats.internlabs.admin.dto.Status;

public class RollbackResponseAdmin {
    private Status status;
    private String message;
    private String state;
    public RollbackResponseAdmin(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public RollbackResponseAdmin(Status status, String message, String state) {
        this.status = status;
        this.message = message;
        this.state = state;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "RollbackResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
