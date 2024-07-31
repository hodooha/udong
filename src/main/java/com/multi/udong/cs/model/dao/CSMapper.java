package com.multi.udong.cs.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.member.model.dto.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;

@Mapper
public interface CSMapper {
    List<LinkedHashMap<String, Object>> selectQue(PageDTO pageDTO);

    void insertFile(AttachmentDTO attachmentDTO);

    void insertQueForm(CSQuestionDTO csQuestionDTO);

    List<TypeDTO> getAllTypes();
}
