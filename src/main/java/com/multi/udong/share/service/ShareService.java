package com.multi.udong.share.service;

import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;

import java.util.List;

public interface ShareService {
    List<ShaCatDTO> getShaCat() throws Exception;

    int insertItem(ShaItemDTO itemDTO) throws Exception;
}
