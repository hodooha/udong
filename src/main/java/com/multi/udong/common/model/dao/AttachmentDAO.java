package com.multi.udong.common.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AttachmentDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AttachmentDTO findAttachmentByMemberNo(Long memberNo) {
        String sql = "SELECT * FROM attachments WHERE member_no = ?";  // SQL 쿼리 작성
        return jdbcTemplate.queryForObject(sql, new Object[]{memberNo}, new RowMapper<AttachmentDTO>() {
            @Override
            public AttachmentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                AttachmentDTO attachment = new AttachmentDTO();
                attachment.setFileNo(rs.getInt("file_no"));
                attachment.setTypeCode(rs.getString("type_code"));
                attachment.setTargetNo(rs.getInt("member_no"));
                attachment.setOriginalName(rs.getString("original_name"));
                attachment.setSavedName(rs.getString("saved_name"));
                attachment.setFileUrl(rs.getString("file_url"));
                return attachment;
            }
        });
    }
    public void insertAttachment(AttachmentDTO attachmentDTO) {
        String sql = "INSERT INTO ATTACHMENT (type_code, target_no, original_name, saved_name) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                attachmentDTO.getTypeCode(),
                attachmentDTO.getTargetNo(),
                attachmentDTO.getOriginalName(),
                attachmentDTO.getSavedName());
    }
}