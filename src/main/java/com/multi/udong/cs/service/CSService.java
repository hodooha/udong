package com.multi.udong.cs.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSAnswerDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.member.model.dto.PageDTO;

import java.util.List;
import java.util.Map;

/**
 * The interface Cs service.
 *
 * @author 김재식
 * @since 2024 -07-31
 */
public interface CSService {
    List<List<String>> selectQue(PageDTO pageDTO);

    void insertQueForm(CSQuestionDTO csQuestionDTO, List<AttachmentDTO> attachmentDTO);

    List<TypeDTO> getAllTypes();

    Map<String, Object> getQueDetail(int csNo);

    AttachmentDTO getAttachment(int fileNo);

    void insertAnswerQue(CSAnswerDTO csAnswerDTO);
}
