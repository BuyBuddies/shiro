package com.buybuddies.shiro.authorization.security;

import com.buybuddies.shiro.data.user.Role;
import com.buybuddies.shiro.data.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


/**
 * Custom UserDetails implementation for Spring Security
 * Stores only essential user info in the security context
 * Never store the full entity to avoid serialization and lazy-loading issues
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String firebaseUid;
    private final String email;
    private final String name;
    private final Role role;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.firebaseUid = user.getFirebaseUid();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return null; // Firebase handles passwords
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