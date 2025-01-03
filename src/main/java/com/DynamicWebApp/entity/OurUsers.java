package com.DynamicWebApp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Entity
@Table(name = "ourusers")
@Data
public class OurUsers implements UserDetails {
    private static final long serialVersionUID = 2746166987048338933L;

    @SequenceGenerator(
            name = "users_sequence",
            sequenceName = "users_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "users_sequence"
    )
    private Integer id;
    private String email;
    private String name;
    private String password;
    private String city;
    private LocalDateTime createDate; // اضافه کردن فیلد CREATE_DATE

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @ElementCollection
    @CollectionTable(
            name = "user_roles_create_date",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "create_date")
    private List<LocalDateTime> rolesCreateDate; // اضافه کردن فیلدهای CREATE_DATE برای هر نقش

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCodeEng()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
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
