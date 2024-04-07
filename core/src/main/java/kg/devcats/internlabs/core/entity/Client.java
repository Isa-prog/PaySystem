package kg.devcats.internlabs.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true)
    @NotNull(message = "Login must not be null")
    private String login;

    @Column(name = "full_name")
    @NotNull(message = "Full name must not be null")
    private String fullName;

    @Column(name = "password")
    @NotNull(message = "Password must not be null")
    private String password;

    @Column(name = "balance")
    @NotNull(message = "Balance must not be null")
    private BigDecimal balance;

    @Column(name = "ip_address")
    @NotNull(message = "IP Address must not be null")
    private String ipAddresses;

    @ManyToOne
    @JoinColumn(name = "limit_id")
    private Limit limit;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull(message = "Role must not be null")
    private Role role;

    public Client() {
    }

    public Client(String login, String fullName, String password, BigDecimal balance, String ipAddresses, Limit limit, Role role) {
        this.login = login;
        this.fullName = fullName;
        this.password = password;
        this.balance = balance;
        this.ipAddresses = ipAddresses;
        this.limit = limit;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(String ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", ipAddresses='" + ipAddresses + '\'' +
                ", limit=" + limit +
                ", role=" + role +
                '}';
    }
}

