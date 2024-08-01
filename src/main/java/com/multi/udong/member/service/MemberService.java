package com.multi.udong.member.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.member.model.dto.MemAddressDTO;
import com.multi.udong.member.model.dto.MemBusDTO;
import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.member.model.dto.MemberDTO;

import java.util.List;
import java.util.Map;

/**
 * The interface Member service.
 *
 * @author : 재식
 * @since : 24. 7. 21.
 */
public interface MemberService {

    /**
     * Signup.
     *
     * @param memberDTO the member dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    void signup(MemberDTO memberDTO) throws Exception;

    /**
     * Is id duplicate boolean.
     *
     * @param memberId the member id
     * @return the boolean
     * @since 2024 -08-01
     */
    boolean isIdDuplicate(String memberId);

    /**
     * Signup seller.
     *
     * @param memberDTO     the member dto
     * @param memBusDTO     the mem bus dto
     * @param attachmentDTO the attachment dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    void signupSeller(MemberDTO memberDTO, MemBusDTO memBusDTO, AttachmentDTO attachmentDTO) throws Exception;

    /**
     * Insert address.
     *
     * @param memAddressDTO the mem address dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    void insertAddress(MemAddressDTO memAddressDTO) throws Exception;

    /**
     * Update member session.
     *
     * @since 2024 -08-01
     */
    void updateMemberSession();

    /**
     * Update address.
     *
     * @param memAddressDTO the mem address dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    void updateAddress(MemAddressDTO memAddressDTO) throws Exception;

    /**
     * Update profile.
     *
     * @param memberDTO     the member dto
     * @param attachmentDTO the attachment dto
     * @since 2024 -08-01
     */
    void updateProfile(MemberDTO memberDTO, AttachmentDTO attachmentDTO);

    /**
     * Is nickname duplicate boolean.
     *
     * @param nickname the nickname
     * @return the boolean
     * @since 2024 -08-01
     */
    boolean isNicknameDuplicate(String nickname);

    /**
     * Select all act list.
     *
     * @param table   the table
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -08-01
     */
    List<List<String>> selectAllAct(String table, PageDTO pageDTO);

    /**
     * Select all dash board map.
     *
     * @param memberNo the member no
     * @return the map
     * @since 2024 -08-01
     */
    Map<String, Object> selectAllDashBoard(int memberNo);

    /**
     * Delete member string.
     *
     * @param memberNo the member no
     * @return the string
     * @since 2024 -08-01
     */
    String deleteMember(int memberNo);
}
