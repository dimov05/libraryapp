package bg.libapp.libraryapp.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

public class AppUserDetails implements UserDetails, Serializable {
    private String username;
    private String password;
    private boolean isActive;
    private Collection<GrantedAuthority> authorities;

    public AppUserDetails() {
    }

    public AppUserDetails(String username, String password, boolean isActive, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public AppUserDetails setUsername(String username) {
        this.username = username;
        return this;
    }

    public AppUserDetails setPassword(String password) {
        this.password = password;
        return this;
    }

    public AppUserDetails setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
        return isActive;
    }
}
