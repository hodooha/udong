package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dao.ShareDAO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShareServiceImpl implements ShareService {

    private final SqlSessionTemplate sqlSession;
    private final ShareDAO shareDAO;

    @Override
    public List<ShaCatDTO> getShaCat() throws Exception {
        List<ShaCatDTO> list = shareDAO.getShaCat(sqlSession);

        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception {
        int itemNo = shareDAO.insertItem(sqlSession, itemDTO);
        int result = itemNo > 0 ? 1 : 0;
        if (!imgList.isEmpty()) {
            for (AttachmentDTO img : imgList) {
                img.setTargetNo(itemNo);
            }
            int imgResult = shareDAO.insertImg(sqlSession, imgList);
            if (imgResult < 1) {
                result = 0;

            }
        }
        return result;
    }

    @Override
    public List<ShaItemDTO> rentItemList(int locCode) throws Exception {

        List<ShaItemDTO> itemList = shareDAO.rentItemList(sqlSession, locCode);

        return itemList;
    }

    @Override
    public List<ShaItemDTO> giveItemList(int locCode) throws Exception {

        List<ShaItemDTO> itemList = shareDAO.giveItemList(sqlSession, locCode);

        return itemList;
    }
}
