package com.multi.udong.share.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ShareDAO {


    public List<ShaCatDTO> getShaCat(SqlSessionTemplate sqlSession) throws Exception{

        return (ArrayList) sqlSession.selectList("ShareMapper.getShaCat");
    }

    public int insertItem(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{
        sqlSession.insert("ShareMapper.insertItem", itemDTO);
        return itemDTO.getItemNo();
    }

    public int insertImg(SqlSessionTemplate sqlSession, List<AttachmentDTO> imgList) {


        return sqlSession.insert("ShareMapper.insertImg", imgList);
    }

    public List<ShaItemDTO> rentItemList(SqlSessionTemplate sqlSession, int locCode) {
        return (ArrayList) sqlSession.selectList("ShareMapper.rentItemList", locCode);
    }

    public List<ShaItemDTO> giveItemList(SqlSessionTemplate sqlSession, int locCode) {
        return (ArrayList) sqlSession.selectList("ShareMapper.giveItemList", locCode);
    }
}
