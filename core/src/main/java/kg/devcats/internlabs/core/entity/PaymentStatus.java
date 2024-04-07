package kg.devcats.internlabs.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_statuses")
public class PaymentStatus {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    public PaymentStatus() {
    }

    public PaymentStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PaymentStatus{" +
                "name='" + name + '\'' +
                '}';
    }
}
