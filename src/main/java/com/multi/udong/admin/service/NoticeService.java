package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dto.NoticeDTO;

import java.util.List;

public interface NoticeService {
    void save(NoticeDTO notice);
    List<NoticeDTO> findAll();
    NoticeDTO findById(int noticeNo);
    void update(NoticeDTO notice);
    void delete(int noticeNo);
}