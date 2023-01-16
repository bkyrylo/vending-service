package com.mvpmatch.vending.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name="user", schema = "public")
public class User implements UserDetails {

    private static final String rolePrefix = "ROLE_";

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String password;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)    //
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", nullable=false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable=false))
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy="seller", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Deposit> deposits = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addProduct(Product product) {
        this.products.add(product);
        product.setSeller(this);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        product.setSeller(null);
    }

    public void addDeposit(Deposit deposit) {
        this.deposits.add(deposit);
        deposit.setUser(this);
    }

    public void removeAllDeposits() {
        this.deposits.forEach(deposit -> deposit.setUser(null));
        this.deposits.clear();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles != null ? roles.stream()
                .map(r -> rolePrefix + r.getRoleName())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()) : Collections.emptyList();
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
}