package kg.devcats.internlabs.clientgateway.dto.response;

import kg.devcats.internlabs.clientgateway.dto.Status;

public class RollbackResponse{
    private Status status;
    private String message;
    private String state;
    public RollbackResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public RollbackResponse(Status status, String message, String state) {
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
