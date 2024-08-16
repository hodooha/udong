package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;

import java.util.List;

public interface NoticeService {
    void save(NoticeDTO notice);
    List<NoticeDTO> findAll();
    NoticeDTO findById(int noticeNo);
    void delete(int noticeNo);
    NoticeDTO getNoticeWithAttachments(int noticeNo);
    void updateNotice(NoticeDTO notice, AttachmentDTO attachmentDTO);
    int saveNoticeWithAttachment(NoticeDTO noticeDTO, AttachmentDTO attachmentDTO);
    NoticeDTO getActivePopupNoticeWithAttachment();
}