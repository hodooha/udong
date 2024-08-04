package com.multi.udong.share.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 물건 DTO
 *
 * @author 하지은
 * @since 2024 -07-21
 */
@Data
public class ShaItemDTO {
    private int itemNo;
    private String itemGroup;
    private String itemCatCode;
    private String title;
    private String content;
    private int ownerNo;
    private String nickName;
    private int level;
    private Long locCode;
    private String locName;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String displayDate;
    private LocalDate expiryDate;
    private LocalDate returnDate;
    private String statusCode;
    private String statusName;
    private int dealCnt;
    private int likeCnt;
    private int viewCnt;
    private int reqCnt;
    private LocalDateTime deletedAt;
    private String img;
    private List<AttachmentDTO> imgList;
    private List<String> delFilesNo;
    private List<String> delFilesName;
    private boolean liked;

}

