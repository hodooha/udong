package com.multi.udong.admin.model.dao;

import com.multi.udong.admin.model.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeDAO {
    // 공지사항 삽입
    void insertNotice(NoticeDTO notice);

    // 모든 공지사항 조회
    List<NoticeDTO> findAllNotices();

    // 특정 공지사항 조회
    NoticeDTO findNoticeById(@Param("noticeNo") int noticeNo);

    // 공지사항 업데이트
    void updateNotice(NoticeDTO notice);

    // 공지사항 삭제
    void deleteNotice(@Param("noticeNo") int noticeNo);
}