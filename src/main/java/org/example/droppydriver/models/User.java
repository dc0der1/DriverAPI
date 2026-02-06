package org.example.droppydriver.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private final UUID id = UUID.randomUUID();

    private String username;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;
    private int age;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String username, String password, String email, int age, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.createdAt = new Date();
        this.role = role;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<Folder> folders = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name().toUpperCase()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
