package com.multi.udong.cs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Cs question dto.
 *
 * @author 김재식
 * @since 2024 -08-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSQuestionDTO {
    private Integer csNo;
    private Integer writerNo;
    private String csType;
    private String csName;
    private String title;
    private String content;
    private String createdAt;
    private Character isAnswered;
}
