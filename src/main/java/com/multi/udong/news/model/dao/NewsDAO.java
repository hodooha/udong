package com.multi.udong.news.model.dao;

import com.multi.udong.club.model.dto.ReportDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.news.model.dto.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsDAO {


    public String checkIsNewsDeleted(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.selectOne("newsMapper.checkIsNewsDeleted", newsNo);

    }

    public String checkIsAdDeleted(SqlSessionTemplate sqlSession, int adNo) {

        return sqlSession.selectOne("newsMapper.checkIsAdDeleted", adNo);

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

    public List<LocationDTO> selectLocationList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("newsMapper.selectLocationList");

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

    public int updateNews(SqlSessionTemplate sqlSession, NewsDTO newsDTO) {

        return sqlSession.update("newsMapper.updateNews", newsDTO);

    }

    public AttachmentDTO selectAttachment(SqlSessionTemplate sqlSession, int fileNo) {

        return sqlSession.selectOne("newsMapper.selectAttachment", fileNo);

    }

    public int updateAttachment(SqlSessionTemplate sqlSession, AttachmentDTO newImg) {

        return sqlSession.update("newsMapper.updateAttachment", newImg);

    }

    public int deleteAttachment(SqlSessionTemplate sqlSession, AttachmentDTO deletedImg) {

        return sqlSession.delete("newsMapper.deleteAttachment", deletedImg);

    }

    public int insertAttachment(SqlSessionTemplate sqlSession, AttachmentDTO newImg) {

        return sqlSession.insert("newsMapper.insertAttachment", newImg);

    }

    public int deleteNews(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.update("newsMapper.deleteNews", newsNo);

    }

    public List<NewsDTO> selectHotNewsList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("newsMapper.selectHotNewsList", filterDTO);

    }


    public int insertAd(SqlSessionTemplate sqlSession, NewsDTO newsDTO) {

        return sqlSession.insert("newsMapper.insertAd", newsDTO);

    }

    public List<NewsDTO> selectAdList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("newsMapper.selectAdList", filterDTO);

    }


    public int addAdViews(SqlSessionTemplate sqlSession, int adNo) {

        return sqlSession.update("newsMapper.addAdViews", adNo);

    }

    public NewsDTO selectAdDetail(SqlSessionTemplate sqlSession, int adNo) {

        return sqlSession.selectOne("newsMapper.selectAdDetail", adNo);

    }

    public int deleteAd(SqlSessionTemplate sqlSession, int adNo) {

        return sqlSession.update("newsMapper.deleteAd", adNo);

    }

    public Integer selectNewsWriterNo(SqlSessionTemplate sqlSession, int newsNo) {

        return sqlSession.selectOne("newsMapper.selectNewsWriterNo", newsNo);
    }
}
