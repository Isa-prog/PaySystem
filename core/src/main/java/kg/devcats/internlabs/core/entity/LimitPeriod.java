package kg.devcats.internlabs.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "limit_periods")
public class LimitPeriod {
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    public LimitPeriod() {
    }

    public LimitPeriod(String name) {
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
        return "LimitPeriod{" +
                "name='" + name + '\'' +
                '}';
    }
}
