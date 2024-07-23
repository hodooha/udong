package com.multi.udong.security;

import com.multi.udong.member.model.dto.MemberDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * @author : 재식
 * @since : 24. 7. 22.
 */
@Getter
@RequiredArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {
    private final MemberDTO memberDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(memberDTO.getAuthority()));
    }

    @Override
    public String getPassword() {
        return memberDTO.getMemberPw();
    }

    @Override
    public String getUsername() {
        return memberDTO.getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return memberDTO.getIsBlacked() != 'Y';
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return memberDTO.getIsBlacked() != 'Y';
    }
}