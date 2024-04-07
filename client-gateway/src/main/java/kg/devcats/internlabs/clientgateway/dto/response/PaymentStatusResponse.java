package kg.devcats.internlabs.clientgateway.dto.response;

import kg.devcats.internlabs.clientgateway.dto.Status;

public class PaymentStatusResponse {
    private Status status;
    private String message;
    private String paymentStatus;
    public PaymentStatusResponse(Status status, String message, String paymentStatus) {
        this.status = status;
        this.message = message;
        this.paymentStatus = paymentStatus;
    }
    public PaymentStatusResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PaymentStatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

}