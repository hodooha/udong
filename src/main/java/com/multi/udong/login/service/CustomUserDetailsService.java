package com.multi.udong.login.service;

import com.multi.udong.member.model.dao.MemberMapper;
import com.multi.udong.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The type Custom user details service.
 *
 * @author : 재식
 * @since : 24. 7. 22.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberMapper memberMapper;

    /**
     * Load user by username user details.
     *
     * @param memberId the member id
     * @return the user details
     * @throws UsernameNotFoundException the username not found exception
     * @since 2024 -07-22
     */
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        MemberDTO memberDTO = memberMapper.findMemberById(memberId);

        if (memberDTO == null || memberDTO.getIsDeleted() == 'Y') {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다");
        }

        return new CustomUserDetails(memberDTO);
    }
}
