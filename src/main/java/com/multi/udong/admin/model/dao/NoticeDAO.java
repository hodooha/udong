package com.multi.udong.admin.model.dao;

import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeDAO {
    // 공지사항 삽입
    int insertNotice(NoticeDTO noticeDTO);

    // 모든 공지사항 조회
    List<NoticeDTO> findAllNotices();

    // 특정 공지사항 조회
    NoticeDTO findNoticeById(@Param("noticeNo") int noticeNo);

    // 공지사항 삭제
    void deleteNotice(@Param("noticeNo") int noticeNo);

    // 공지사항과 첨부파일 조회
    NoticeDTO findNoticeWithAttachments(@Param("noticeNo") int noticeNo);

    // 공지사항 업데이트
    void updateNotice(@Param("notice") NoticeDTO notice);

    // 공지사항 관련 첨부파일 삭제
    void deleteAttachmentsByNoticeNo(@Param("noticeNo") int noticeNo);

    // 첨부파일 삽입
    void insertAttachment(AttachmentDTO attachment);

    // 공지사항 ID로 첨부파일 조회
    NoticeDTO getNoticeById(@Param("noticeNo") int noticeNo);

    // 첨부파일 조회
    List<AttachmentDTO> getAttachmentsByNoticeNo(@Param("noticeNo") int noticeNo);
}