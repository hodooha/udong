package com.multi.udong.cs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Cs answer dto.
 *
 * @author 김재식
 * @since 2024 -08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSAnswerDTO {
    private Integer answerNo;
    private Integer csNo;
    private Integer answererNo;
    private String content;
    private String createdAt;

    private String nickname;
}
