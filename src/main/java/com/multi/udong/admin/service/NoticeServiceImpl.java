package com.multi.udong.admin.service;

import com.multi.udong.admin.model.dao.NoticeDAO;
import com.multi.udong.admin.model.dto.NoticeDTO;
import com.multi.udong.common.model.dao.AttachmentDAO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    @Transactional
    public int saveNoticeWithAttachment(NoticeDTO noticeDTO, AttachmentDTO attachmentDTO) {

        System.out.println("Saving notice: " + noticeDTO);
        int result = noticeDAO.insertNotice(noticeDTO);
        System.out.println("Insert result: " + result);

        if (result > 0) {
            int noticeNo = noticeDTO.getNoticeNo();  // 자동 생성된 키가 DTO에 설정되었는지 확인
            System.out.println("Generated noticeNo: " + noticeNo);

            if (attachmentDTO != null) {
                attachmentDTO.setTargetNo(noticeNo);
                try {
                    attachmentDAO.insertAttachment(attachmentDTO);
                    System.out.println("Attachment inserted successfully");
                } catch (Exception e) {
                    System.err.println("Error inserting attachment: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            return noticeNo;
        } else {
            throw new RuntimeException("Failed to insert notice");
        }
    }

    @Override
    public void save(NoticeDTO notice) {

    }

    @Override
    public List<NoticeDTO> findAll() {
        List<NoticeDTO> notices = noticeDAO.findAllNotices();
        for (NoticeDTO notice : notices) {
            if (notice.getPopup() != null) {
                notice.setPopupString(notice.getPopup() ? "Y" : "N");
            } else {
                notice.setPopupString("N");
            }

            // 디버깅 로그 추가
            System.out.println("Notice ID: " + notice.getNoticeNo() + ", Popup: " + notice.getPopup() + ", PopupString: " + notice.getPopupString());
        }
        return notices;
    }

    @Override
    public NoticeDTO findById(int noticeNo) {
        NoticeDTO notice = noticeDAO.findNoticeById(noticeNo);
        if (notice != null) {
            List<AttachmentDTO> attachments = noticeDAO.getAttachmentsByNoticeNo(noticeNo);
            if (!attachments.isEmpty()) {
                // 첫 번째 첨부 파일의 경로를 사용
                notice.setImagePath("/uploadFiles/" + attachments.get(0).getSavedName());
            }
        }
        return notice;
    }

    @Override
    @Transactional
    public void updateNotice(NoticeDTO notice, AttachmentDTO attachmentDTO) {
        if (attachmentDTO != null) {
            // 기존 첨부파일 삭제
            noticeDAO.deleteAttachmentsByNoticeNo(notice.getNoticeNo());

            // 새 첨부파일 추가
            attachmentDTO.setTypeCode("NOTI");
            attachmentDTO.setTargetNo(notice.getNoticeNo());
            noticeDAO.insertAttachment(attachmentDTO);

            // 공지사항의 이미지 경로 업데이트
            notice.setImagePath("/uploadFiles/" + attachmentDTO.getSavedName());
        } else if (notice.getImagePath() == null) {
            // 이미지를 삭제하는 경우
            noticeDAO.deleteAttachmentsByNoticeNo(notice.getNoticeNo());
        }

        // 공지사항 업데이트 (이미지 경로 포함)
        noticeDAO.updateNotice(notice);
    }

    @Override
    @Transactional
    public void delete(int noticeNo) {
        noticeDAO.deleteNotice(noticeNo);
    }

    @Override
    public NoticeDTO getNoticeWithAttachments(int noticeNo) {
        NoticeDTO notice = noticeDAO.getNoticeById(noticeNo);
        if (notice != null) {
            List<AttachmentDTO> attachments = noticeDAO.getAttachmentsByNoticeNo(noticeNo);
            notice.setAttachments(attachments);
        }
        return notice;
    }

    public String saveFile(MultipartFile file) {
        try {
            // 저장 경로 설정
            String path = Paths.get("src", "main", "resources", "static", "uploadFiles").toAbsolutePath().normalize().toString();
            String savePath = path + File.separator;

            // 디렉토리 생성
            File directory = new File(savePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일명 생성
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // 파일 저장
            Path targetLocation = Paths.get(savePath + newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다. 파일명: " + file.getOriginalFilename(), e);
        }
    }
}