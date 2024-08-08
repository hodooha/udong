package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.NoticeDAO;
import com.multi.udong.admin.model.dto.NoticeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;

    @Override
    public void save(NoticeDTO notice) {
        noticeDAO.insertNotice(notice);
    }

    @Override
    public List<NoticeDTO> findAll() {
        return noticeDAO.findAllNotices();
    }

    @Override
    public NoticeDTO findById(int noticeNo) {
        return noticeDAO.findNoticeById(noticeNo);
    }

    @Override
    public void update(NoticeDTO notice) {
        noticeDAO.updateNotice(notice);
    }

    @Override
    public void delete(int noticeNo) {
        noticeDAO.deleteNotice(noticeNo);
    }
}