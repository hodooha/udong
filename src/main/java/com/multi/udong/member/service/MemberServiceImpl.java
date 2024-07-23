package com.multi.udong.member.service;

import com.multi.udong.member.model.dao.MemberDAO;
import com.multi.udong.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberDAO memberDAO;

    @Override
    public void signup(MemberDTO m) throws Exception {
        String encPw = bCryptPasswordEncoder.encode(m.getMemberPw());
        m.setMemberPw(encPw);
        try {
            int result = memberDAO.signup(m);
            if (result != 1) {
                throw new Exception("회원가입에 실패하였습니다");
            }
        } catch (Exception e) {
            System.out.println("회원가입 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean isIdDuplicate(String memberId) {
        return memberDAO.findMemberById(memberId) != null;
    }
}
