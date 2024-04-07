package kg.devcats.internlabs.clientgateway.dto.response;

import kg.devcats.internlabs.clientgateway.dto.Status;

public class RequisiteResponse {
    private Status status;
    private String message;
    private String name;
    private boolean requisiteFound;

    public RequisiteResponse(Status status, String message, String name, boolean requisiteFound) {
        this.status = status;
        this.message = message;
        this.name = name;
        this.requisiteFound = requisiteFound;
    }

    public RequisiteResponse(Status status, String message, boolean requisiteFound) {
        this.status = status;
        this.message = message;
        this.requisiteFound = requisiteFound;

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

    @Override
    public String toString() {
        return "RequisiteResponse{" +
                "\"status\": \"" + status + "\"," +
                "\"message\": \"" + message + "\"," +
                "\"name\": \"" + name + "\"," +
                "\"requisiteFound\": " + requisiteFound +
                "}";
    }

}
