package com.springTemplate.springTemplate.services.user;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private Integer idUser;
    private String mail;
    private String password;
    private String lastName;
    private String firstName;
    private Boolean firstConnection;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer idUser, String mail, String password, String lastName, String firstName,
            Boolean firstConnection, Collection<? extends GrantedAuthority> authorities) {
        this.idUser = idUser;
        this.mail = mail;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.firstConnection = firstConnection;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    /**
     * Username is mail
     */
    @Override
    public String getUsername() {
        return this.mail;
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

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public Boolean getFirstConnection() {
        return firstConnection;
    }

    public void setFirstConnection(Boolean firstConnection) {
        this.firstConnection = firstConnection;
    }
}
