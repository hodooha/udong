package com.multi.udong.club.model.dto;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubDTO {

    private int clubNo;
    private CategoryDTO category;
    private LocationDTO location;
    private String clubName;
    private String introduction;
    private MasterDTO master;
    private int currentPersonnel;
    private int maxPersonnel;
    private String chatroomCode;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private AttachmentDTO attachment;

}
