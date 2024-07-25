package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Member dto.
 *
 * @author : 재식
 * @since : 24. 7. 21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer memberNo;
    private String memberId;
    private String memberPw;
    private String nickname;
    private String phone;
    private String email;
    private String authority;
    private String signupAt;
    private String modifiedAt;
    private Integer score;
    private Integer level;
    private String lastLoginAt;
    private Integer reportedCnt;
    private Character isBlacked;
    private String blackedAt;

    private MemAddressDTO memAddressDTO;
}
