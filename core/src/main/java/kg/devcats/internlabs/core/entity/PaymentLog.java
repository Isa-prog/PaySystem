package kg.devcats.internlabs.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_logs")
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "action cannot be null")
    @Column(name = "action")
    private String action;

    @Column(name = "request_parameters")
    private String requestParameters;

    @Column(name = "response")
    private String response;

    @Column(name = "details")
    private String details;
    @NotNull(message = "datetime cannot be null")
    @Column(name = "datetime")
    private LocalDateTime datetime;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public PaymentLog() {
    }

    public PaymentLog(String action, String requestParameters, String response, String details, LocalDateTime datetime, Payment payment) {
        this.action = action;
        this.requestParameters = requestParameters;
        this.response = response;
        this.details = details;
        this.datetime = datetime;
        this.payment = payment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(String requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PaymentLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", requestParameters='" + requestParameters + '\'' +
                ", response='" + response + '\'' +
                ", details='" + details + '\'' +
                ", datetime=" + datetime +
                ", payment=" + payment +
                '}';
    }
}
