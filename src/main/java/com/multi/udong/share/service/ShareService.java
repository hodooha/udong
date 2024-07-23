package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;

import java.util.List;

public interface ShareService {
    List<ShaCatDTO> getShaCat() throws Exception;

    int insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception;

    List<ShaItemDTO> rentItemList(int locCode) throws Exception;

    List<ShaItemDTO> giveItemList(int locCode) throws Exception;
}
