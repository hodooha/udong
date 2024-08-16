package com.multi.udong.news.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.news.model.dto.CategoryDTO;
import com.multi.udong.news.model.dto.FilterDTO;
import com.multi.udong.news.model.dto.NewsDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsDAO {

    public List<NewsDTO> selectNewsList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("newsMapper.selectNewsList", filterDTO);

    }

    public int selectNewsCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("newsMapper.selectNewsCount", filterDTO);

    }

    public List<CategoryDTO> selectCategoryList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("newsMapper.selectCategoryList");

    }

    public int insertNews(SqlSessionTemplate sqlSession, NewsDTO newsDTO) {

        return sqlSession.insert("newsMapper.insertNews", newsDTO);

    }

    public int insertNewsImg(SqlSessionTemplate sqlSession, AttachmentDTO attachment) {

        return sqlSession.insert("newsMapper.insertNewsImg", attachment);

    }
}
