package kg.devcats.internlabs.clientgateway.dto.response;

import kg.devcats.internlabs.clientgateway.dto.Status;

public class PaymentFromToResponse {
    private Status status;
    private String message;
    private String payment;
    private boolean paymentsFound;


    public PaymentFromToResponse(Status status, String message, String payment, boolean paymentsFound) {
        this.status = status;
        this.message = message;
        this.payment = payment;
        this.paymentsFound = paymentsFound;
    }

    public PaymentFromToResponse(Status status, String message, boolean paymentsFound) {
        this.status = status;
        this.message = message;
        this.paymentsFound = paymentsFound;
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

    public String getPayments() {
        return payment;
    }

    public void setPayments(String payment) {
        this.payment = payment;
    }
    public boolean isPaymentsFound() {
        return paymentsFound;
    }


    @Override
    public String toString() {
        return "PaymentFromToResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", payment='" + payment + '\'' +
                '}';
    }
}
