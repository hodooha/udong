package com.multi.udong.member.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dao.MemberMapper;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.member.model.dto.MemberDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.login.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final MemberMapper memberMapper;
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

            int result = memberMapper.signup(memberDTO);
            int result2 = memberMapper.insertProfileImg();

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
        return memberMapper.findMemberById(memberId) != null;
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
        return memberMapper.findMemberByNickname(nickname) != null;
    }

    /**
     * Select all act list.
     *
     * @param table   the table
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -07-30
     */
    @Override
    public List<List<String>> selectAllAct(String table, PageDTO pageDTO) {

        List<LinkedHashMap<String, Object>> map = new ArrayList<>();

        map = switch (table) {
            case "newsBoard" -> memberMapper.selectNewsBoard(pageDTO);
            case "newsLike" -> memberMapper.selectNewsLike(pageDTO);
            case "newsReply" -> memberMapper.selectNewsReply(pageDTO);
            case "club" -> memberMapper.selectClub(pageDTO);
            case "clubLog" -> memberMapper.selectClubLog(pageDTO);
            case "clubSchedule" -> memberMapper.selectClubSchedule(pageDTO);
            case "shareLike" -> memberMapper.selectShareLike(pageDTO);
            case "saleBoard" -> memberMapper.selectSaleBoard(pageDTO);
            default -> map;
        };

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
     * Select all dash board map.
     *
     * @param memberNo the member no
     * @return the map
     * @since 2024 -07-30
     */
    @Override
    public Map<String, Object> selectAllDashBoard(int memberNo) {

        Map<String, Object> result = new HashMap<>();

        result.put("newsData", memberMapper.getNewsData(memberNo));
        result.put("lendData", memberMapper.getLendData(memberNo));
        result.put("rentData", memberMapper.getRentData(memberNo));
        result.put("giveData", memberMapper.getGiveData(memberNo));
        result.put("clubData", memberMapper.getClubData(memberNo));
        result.put("scheduleData", memberMapper.getScheduleData(memberNo));

        return result;
    }

    /**
     * Delete member.
     *
     * @param memberNo the member no
     * @return boolean string
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    @Override
    public String deleteMember(int memberNo) {

        Map<String, Object> checkResult = memberMapper.checkMember(memberNo);

        if ((Long) checkResult.get("renting_count") != 0) {
            return "isRenting";
        }

        if ((Long) checkResult.get("giving_count") != 0) {
            return "isGiving";
        }

        if ((Long) checkResult.get("master_count") != 0) {
            return "isMaster";
        }

        if (memberMapper.deleteMember(memberNo) == 1) {
            if (memberMapper.checkMemBus(memberNo) != null) {
                memberMapper.deleteMemBus(memberNo);
            }
            return "able";
        } else {
            return "disable";
        }
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
            int result = memberMapper.signup(memberDTO);
            int result2 = memberMapper.insertBusReg(memBusDTO);
            int result3 = memberMapper.insertAttachment(attachmentDTO);

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
        Long locationCode = memberMapper.findCodeByAddress(memAddressDTO);

        if (locationCode != null) {
            memAddressDTO.setLocationCode(locationCode);
            int result = memberMapper.insertAddress(memAddressDTO);

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
        Long locationCode = memberMapper.findCodeByAddress(memAddressDTO);

        if (locationCode != null) {
            memAddressDTO.setLocationCode(locationCode);
            int result = memberMapper.updateAddress(memAddressDTO);

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
            memberMapper.updateProfileImg(attachmentDTO);
            memberMapper.updateMember(attachmentDTO);
        }

        if (memberDTO.getNickname() != null && !memberDTO.getNickname().isEmpty()) {
            memberMapper.updateNickname(memberDTO);
        }

        if (memberDTO.getEmail() != null && !memberDTO.getEmail().isEmpty()) {
            memberMapper.updateEmail(memberDTO);
        }

        if (memberDTO.getPhone() != null && !memberDTO.getPhone().isEmpty()) {
            memberMapper.updatePhone(memberDTO);
        }

        if (memberDTO.getMemberPw() != null && !memberDTO.getMemberPw().isEmpty()) {
            memberMapper.updateMemberPw(memberDTO);
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
