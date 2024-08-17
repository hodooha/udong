package com.multi.udong.news.model.dao;

import com.multi.udong.club.model.dto.ReportDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.news.model.dto.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsDAO {


    public String checkIsNewsDeleted(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.selectOne("newsMapper.checkIsNewsDeleted", newsNo);

    }

    public int checkNewsWriter(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.selectOne("newsMapper.checkNewsWriter", newsNo);

    }

    public int checkReplyWriter(SqlSessionTemplate sqlSession, int replyNo) {

        return sqlSession.selectOne("newsMapper.checkReplyWriter", replyNo);

    }

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

    public int addNewsViews(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.update("newsMapper.addNewsViews", newsNo);

    }

    public NewsDTO selectNewsDetail(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("newsMapper.selectNewsDetail", requestDTO);

    }

    public int insertNewsLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.insert("newsMapper.insertNewsLike", likeDTO);

    }

    public int deleteNewsLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.delete("newsMapper.deleteNewsLike", likeDTO);

    }

    public int insertReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.insert("newsMapper.insertReply", replyDTO);

    }

    public int updateReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.update("newsMapper.updateReply", replyDTO);

    }

    public int deleteReply(SqlSessionTemplate sqlSession, ReplyDTO replyDTO) {

        return sqlSession.delete("newsMapper.deleteReply", replyDTO);

    }

    public int insertReplyLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.insert("newsMapper.insertReplyLike", likeDTO);

    }

    public int deleteReplyLike(SqlSessionTemplate sqlSession, LikeDTO likeDTO) {

        return sqlSession.delete("newsMapper.deleteReplyLike", likeDTO);

    }


    public int reportNews(SqlSessionTemplate sqlSession, ReportDTO reportDTO) {

        return sqlSession.insert("newsMapper.report", reportDTO);

    }
}
