package kg.devcats.internlabs.clientgateway.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.devcats.internlabs.core.entity.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ClientUserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String fullName;

    private String login;

    @JsonIgnore
    private String password;

    private BigDecimal balance;

    private Limit limit;

    private Collection<? extends GrantedAuthority> authorities;

    public ClientUserDetailsImpl(Long id, String fullName, String login, String password, Limit limit, BigDecimal balance,
                                 Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.login = login;
        this.password = password;
        this.balance = balance;
        this.limit = limit;
        this.authorities = authorities;
    }

    public static ClientUserDetailsImpl build(Client client) {
        List<GrantedAuthority> authorities = client.getRole().getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());

        return new ClientUserDetailsImpl(client.getId(),
                client.getFullName(),
                client.getLogin(),
                client.getPassword(),
                client.getLimit(),
                client.getBalance(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLogin() {
        return login;
    }

    public Limit getLimit() {
        return limit;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClientUserDetailsImpl client = (ClientUserDetailsImpl) o;
        return Objects.equals(id, client.id);
    }
}
