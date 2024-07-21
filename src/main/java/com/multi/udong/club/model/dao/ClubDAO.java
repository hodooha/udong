package com.multi.udong.club.model.dao;

import com.multi.udong.club.model.dto.CategoryDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClubDAO {

    public List<CategoryDTO> selectCategoryList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("clubMapper.selectCategoryList");

    }
}
