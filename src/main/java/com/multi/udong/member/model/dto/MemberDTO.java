package com.multi.udong.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Integer memberNo;
    private String memberId;
    private String memberPw;
    private String nickname;
    private String phone;
    private String email;
    private String authority;
    private LocalDateTime signupAt;
    private LocalDateTime modifiedAt;
    private Integer score;
    private Integer level;
    private LocalDateTime lastLoginAt;
    private Integer reportedCnt;
    private Character isBlacked;
    private LocalDateTime blackedAt;
}
