package com.multi.udong.share.service;

import com.multi.udong.share.model.dao.ShareDAO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShareServiceImpl implements ShareService{

    private final SqlSessionTemplate sqlSession;
    private final ShareDAO shareDAO;

    @Override
    public List<ShaCatDTO> getShaCat() throws Exception {
        List<ShaCatDTO> list = shareDAO.getShaCat(sqlSession);

        return list;
    }

    @Override
    public int insertItem(ShaItemDTO itemDTO) throws Exception {
        int result = shareDAO.insertItem(sqlSession, itemDTO);

        return result;
    }


}
