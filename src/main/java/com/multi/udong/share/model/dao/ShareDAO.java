package com.multi.udong.share.model.dao;

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

        return sqlSession.insert("ShareMapper.insertItem", itemDTO);
    }
}
