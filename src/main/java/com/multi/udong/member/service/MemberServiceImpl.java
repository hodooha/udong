package com.multi.udong.member.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dao.MemberDAO;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.security.CustomUserDetails;
import com.multi.udong.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The type Member service.
 *
 * @author : 재식
 * @since : 24. 7. 21.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberDAO memberDAO;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Signup.
     *
     * @param memberDTO the member dto
     * @throws Exception the exception
     * @since 2024 -07-21
     */
    @Override
    public void signup(MemberDTO memberDTO) throws Exception {

        String encPw = bCryptPasswordEncoder.encode(memberDTO.getMemberPw());
        memberDTO.setMemberPw(encPw);

        try {

            int result = memberDAO.signup(memberDTO);

            int memberNo = memberDAO.selectLastInsertId();
            AttachmentDTO attachmentDTO = new AttachmentDTO();
            attachmentDTO.setTargetNo(memberNo);
            attachmentDTO.setTypeCode("MEM");
            attachmentDTO.setOriginalName("defaultProfile.png");
            attachmentDTO.setSavedName("defaultProfile.png");

            int result2 = memberDAO.insertProfileImg(attachmentDTO);

            if (result != 1 && result2 != 1) {

                throw new Exception("회원가입에 실패하였습니다");

            }

        } catch (Exception e) {
            System.out.println("회원가입 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Is id duplicate boolean.
     *
     * @param memberId the member id
     * @return the boolean
     * @since 2024 -07-21
     */
    @Override
    public boolean isIdDuplicate(String memberId) {
        return memberDAO.findMemberById(memberId) != null;
    }

    /**
     * Is nickname duplicate boolean.
     *
     * @param nickname the nickname
     * @return the boolean
     * @since 2024 -07-27
     */
    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return memberDAO.findMemberByNickname(nickname) != null;
    }

    /**
     * Select all act list.
     *
     * @param table    the table
     * @param memberNo
     * @return the list
     * @since 2024 -07-30
     */
    @Override
    public List<List<String>> selectAllAct(String table, int memberNo) {

        List<LinkedHashMap<String, Object>> map = new ArrayList<>();

        switch (table) {
            case "newsBoard":
                map = memberDAO.selectNewsBoard(memberNo);
                break;
            case "newsLike":
                map = memberDAO.selectNewsLike(memberNo);
                break;
            case "newsReply":
                map = memberDAO.selectNewsReply(memberNo);
                break;
            case "club":
                map = memberDAO.selectClub(memberNo);
                break;
            case "clubLog":
                map = memberDAO.selectClubLog(memberNo);
                break;
            case "clubSchedule":
                map = memberDAO.selectClubSchedule(memberNo);
                break;
            case "shareLike":
                map = memberDAO.selectShareLike(memberNo);
                break;
            case "saleBoard":
                map = memberDAO.selectSaleBoard(memberNo);
                break;
        }

        List<List<String>> result = new ArrayList<>();
        for (LinkedHashMap<String, Object> row : map) {
            List<String> list = new ArrayList<>();
            for (Object value : row.values()) {
                list.add(value != null ? value.toString() : "");
            }
            result.add(list);
        }

        return result;
    }

    /**
     * Signup seller.
     *
     * @param memberDTO     the member dto
     * @param memBusDTO     the mem bus dto
     * @param attachmentDTO the attachment dto
     * @throws Exception the exception
     * @since 2024 -07-22
     */
    @Override
    public void signupSeller(MemberDTO memberDTO, MemBusDTO memBusDTO, AttachmentDTO attachmentDTO) throws Exception {
        String encPw = bCryptPasswordEncoder.encode(memberDTO.getMemberPw());
        memberDTO.setMemberPw(encPw);
        try {
            int result = memberDAO.signup(memberDTO);
            int memberNo = memberDAO.selectLastInsertId();

            memBusDTO.setMemberNo(memberNo);
            attachmentDTO.setTargetNo(memberNo);

            int result2 = memberDAO.insertBusReg(memBusDTO);
            int result3 = memberDAO.insertAttachment(attachmentDTO);

            if (result != 1 && result2 != 1 && result3 != 1) {
                throw new Exception("회원가입에 실패하였습니다");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Insert address.
     *
     * @param memAddressDTO the mem address dto
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public void insertAddress(MemAddressDTO memAddressDTO) throws Exception {
        Long locationCode = memberDAO.findCodeByAddress(memAddressDTO);

        if (locationCode != null) {
            memAddressDTO.setLocationCode(locationCode);
            int result = memberDAO.insertAddress(memAddressDTO);

            if (result != 1) {
                throw new Exception("주소 등록에 실패하였습니다.");
            }

        } else {
            throw new Exception("주소가 유효하지 않습니다.");
        }
    }

    /**
     * Update address.
     *
     * @param memAddressDTO the mem address dto
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public void updateAddress(MemAddressDTO memAddressDTO) throws Exception {
        Long locationCode = memberDAO.findCodeByAddress(memAddressDTO);

        if (locationCode != null) {
            memAddressDTO.setLocationCode(locationCode);
            int result = memberDAO.updateAddress(memAddressDTO);

            if (result != 1) {
                throw new Exception("주소 등록에 실패하였습니다.");
            }

        } else {
            throw new Exception("주소가 유효하지 않습니다.");
        }
    }

    /**
     * Update profile.
     *
     * @param memberDTO     the member dto
     * @param attachmentDTO the attachment dto
     * @since 2024 -07-26
     */
    @Override
    public void updateProfile(MemberDTO memberDTO, AttachmentDTO attachmentDTO) {

        if (attachmentDTO != null && !attachmentDTO.getSavedName().isEmpty()) {
            memberDAO.updateProfileImg(attachmentDTO);
            memberDAO.updateMember(attachmentDTO);
        }

        if (memberDTO.getNickname() != null && !memberDTO.getNickname().isEmpty()) {
            memberDAO.updateNickname(memberDTO);
        }

        if (memberDTO.getEmail() != null && !memberDTO.getEmail().isEmpty()) {
            memberDAO.updateEmail(memberDTO);
        }

        if (memberDTO.getPhone() != null && !memberDTO.getPhone().isEmpty()) {
            memberDAO.updatePhone(memberDTO);
        }

        if (memberDTO.getMemberPw() != null && !memberDTO.getMemberPw().isEmpty()) {
            memberDAO.updateMemberPw(memberDTO);
        }
    }

    /**
     * Update member session.
     *
     * @since 2024 -07-25
     */
    @Override
    public void updateMemberSession() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails c = (CustomUserDetails) authentication.getPrincipal();
        String memberId = c.getMemberDTO().getMemberId();

        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(memberId);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        customUserDetails.getMemberDTO().getMemberPw(),
                        customUserDetails.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
