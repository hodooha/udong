package com.multi.udong.share.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private String itemCatName;
    private String title;
    private String content;
    private int ownerNo;
    private String ownerImg;
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

    private int rqstNo;
    private String rqstNickname;
    private String mRqstNickname;

    public void maskNickname() {

        if (this.rqstNickname == null || this.rqstNickname.isEmpty() || this.rqstNickname.trim().isEmpty()) {
            this.mRqstNickname = this.rqstNickname;
        } else {
            int len = this.rqstNickname.length();
            if (len == 2) {
                this.mRqstNickname = this.rqstNickname.charAt(0) + "*";
            } else if (len >= 3) {
                this.mRqstNickname = this.rqstNickname.charAt(0) + "*".repeat(len - 2) + this.rqstNickname.substring(len - 1);
            }
        }
    }

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

    public void convertLocaldatetimeToTime() {

        LocalDateTime now = LocalDateTime.now();

        long diffTime = this.modifiedAt.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String displayDate = null;
        if (diffTime < SEC) {
            this.displayDate = diffTime + "초전";
            return;
        }
        diffTime = diffTime / SEC;
        if (diffTime < MIN) {
            this.displayDate = diffTime + "분 전";
            return;
        }
        diffTime = diffTime / MIN;
        if (diffTime < HOUR) {
            this.displayDate = diffTime + "시간 전";
            return;
        }
        diffTime = diffTime / HOUR;
        if (diffTime < DAY) {
            this.displayDate = diffTime + "일 전";
            return;
        }
        diffTime = diffTime / DAY;
        if (diffTime < MONTH) {
            this.displayDate = diffTime + "개월 전";
            return;
        }

        diffTime = diffTime / MONTH;
        this.displayDate = diffTime + "년 전";

    }
}

