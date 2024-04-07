package kg.devcats.internlabs.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "services")
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be blank")
    @Column(name = "name")
    private String name;

    @Positive(message = "Min value must be positive")
    @Column(name = "min")
    private BigDecimal min;

    @Positive(message = "Max value must be positive")
    @Column(name = "max")
    private BigDecimal max;

    @NotNull(message = "Cancellation status cannot be null")
    @Column(name = "is_cancelable")
    private boolean isCancelable;

    @Column(name = "logo")
    private String logo;

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "limit_id")
//    private Limit limit;

    public Services() {
    }

    public Services(String name, BigDecimal min, BigDecimal max, boolean isCancelable, String logo) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.isCancelable = isCancelable;
        this.logo = logo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public boolean getIsCancelable() {
        return isCancelable;
    }

    public void setIsCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", isCancelable=" + isCancelable +
                ", logo=" + logo +
                '}';
    }
}

