package com.multi.udong.cs.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.TypeDTO;
import com.multi.udong.cs.model.dao.CSMapper;
import com.multi.udong.cs.model.dto.CSQuestionDTO;
import com.multi.udong.member.model.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The type Cs service.
 *
 * @author 김재식
 * @since 2024 -07-31
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CSServiceImpl implements CSService{

    private final CSMapper csMapper;

    /**
     * Select que list.
     *
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -08-01
     */
    @Override
    public List<List<String>> selectQue(PageDTO pageDTO) {

        List<LinkedHashMap<String, Object>> map = new ArrayList<>();

        map = csMapper.selectQue(pageDTO);

        List<List<String>> result = new ArrayList<>();
        for (LinkedHashMap<String, Object> row : map) {
            List<String> list = new ArrayList<>();
            for (Object value : row.values()) {
                list.add(value != null ? value.toString() : "");
            }
            result.add(list);
        }

        return result;
    }

    /**
     * Insert que form.
     *
     * @param csQuestionDTO the cs question dto
     * @param attachmentDTO the attachment dto
     * @since 2024 -08-01
     */
    @Override
    public void insertQueForm(CSQuestionDTO csQuestionDTO, AttachmentDTO attachmentDTO) {

        if (attachmentDTO != null && !attachmentDTO.getSavedName().isEmpty()) {
            csMapper.insertFile(attachmentDTO);
        }

        csMapper.insertQueForm(csQuestionDTO);
    }

    /**
     * Get all types list.
     *
     * @return list
     * @since 2024 -08-01
     */
    @Override
    public List<TypeDTO> getAllTypes() {
        return csMapper.getAllTypes();
    }
}
