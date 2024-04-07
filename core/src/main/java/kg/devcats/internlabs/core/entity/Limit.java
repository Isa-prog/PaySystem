package kg.devcats.internlabs.core.entity;

import org.apache.logging.log4j.message.Message;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "limits")
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="period_id", nullable=false)
    private LimitPeriod period;

    @NotNull(message = "amount cannot be null")
    @Column(name = "amount", nullable = false)
    private Long amount;

    public Limit() {
    }

    public Limit(LimitPeriod period, Long amount) {
        this.period = period;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LimitPeriod getPeriod() {
        return period;
    }

    public void setPeriod(LimitPeriod period) {
        this.period = period;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Limit{" +
                "id=" + id +
                ", period='" + period + '\'' +
                ", amount=" + amount +
                '}';
    }
}
