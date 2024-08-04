package com.multi.udong.cs.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSAnswerDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.member.model.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * The interface Cs mapper.
 *
 * @author 김재식
 * @since 2024 -08-01
 */
@Mapper
public interface CSMapper {
    List<LinkedHashMap<String, Object>> selectQue(PageDTO pageDTO);

    List<LinkedHashMap<String, Object>> selectAllQue(PageDTO pageDTO);

    void insertQueForm(CSQuestionDTO csQuestionDTO);

    void insertFile(AttachmentDTO attachmentDTO);

    List<TypeDTO> getAllTypes();

    CSQuestionDTO getQueDetail(int csNo);

    List<AttachmentDTO> getQueAttachment(int csNo);

    List<CSAnswerDTO> getQueAnswer(int csNo);

    AttachmentDTO getAttachment(int fileNo);

    void insertAnswerQue(CSAnswerDTO csAnswerDTO);

    String getAnswerCreatedAt(CSAnswerDTO csAnswerDTO);

    void updateQueIsAnswered(Integer csNo);
}
