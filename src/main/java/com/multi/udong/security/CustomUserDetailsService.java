package com.multi.udong.security;


import com.multi.udong.member.model.dao.MemberDAO;
import com.multi.udong.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberDAO memberDAO;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        MemberDTO memberDTO = memberDAO.findMemberById(memberId);

        if (memberDTO == null) {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다");
        }

        return new CustomUserDetails(memberDTO);
    }
}
