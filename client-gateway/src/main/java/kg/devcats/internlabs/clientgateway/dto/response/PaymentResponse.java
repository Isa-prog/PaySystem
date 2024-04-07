package kg.devcats.internlabs.clientgateway.dto.response;

import kg.devcats.internlabs.clientgateway.dto.PaymentData;
import kg.devcats.internlabs.clientgateway.dto.Status;

public class PaymentResponse {
    private Status status;
    private String message;
    private PaymentData paymentInfo;

    public PaymentResponse(Status status, String message, PaymentData paymentInfo) {
        this.status = status;
        this.message = message;
        this.paymentInfo = paymentInfo;
    }

    public PaymentResponse(Status status, String message) {
        this.status = status;
        this.message = message;
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

    public PaymentData getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentData paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", paymentInfo=" + paymentInfo +
                '}';
    }
}
