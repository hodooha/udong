package com.multi.udong.login.service;

import com.multi.udong.member.model.dao.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Login service.
 *
 * @author 김재식
 * @since 2024 -08-02
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final MemberMapper memberMapper;

    /**
     * Update last login at.
     *
     * @param memberId the member id
     * @since 2024 -08-02
     */
    public void updateLastLoginAt(String memberId) {
        memberMapper.updateLastLoginAt(memberId);
    }
}
