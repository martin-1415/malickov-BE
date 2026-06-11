package cz.malickov.backend.model;

import cz.malickov.backend.entity.User;
import cz.malickov.backend.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

// Used in spring security
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "ROLE_" + user.getRoleName().name();
        return Collections.singleton(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isActive();
    }

    public Role getRole() {
        return user.getRoleName();
    }

    public UUID getUserUuid() {
        return user.getUserUuid();
    }
}
