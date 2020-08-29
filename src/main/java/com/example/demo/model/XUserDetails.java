package com.example.demo.model;

import com.example.demo.enums.Language;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XUserDetails implements UserDetails {

    private Long id;

    private String name;

    private String email;

    private String password;

    private String mobNumber;

    private Language language;

    private boolean status;

    private Collection<? extends GrantedAuthority> authorities;

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
        return this.email;
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

    public static XUserDetails build(XUser xUser){
        List<GrantedAuthority> authorities = xUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return XUserDetails.builder()
                .authorities(authorities)
                .id(xUser.getId())
                .name(xUser.getName())
                .email(xUser.getEmail())
                .password(xUser.getPassword())
                .mobNumber(xUser.getMobNumber())
                .language(xUser.getLanguage())
                .build();
    }
}
