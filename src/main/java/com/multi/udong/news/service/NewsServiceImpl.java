package com.multi.udong.news.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.news.model.dao.NewsDAO;
import com.multi.udong.news.model.dto.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = {Exception.class})
public class NewsServiceImpl implements NewsService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final NewsDAO newsDAO;

    public NewsServiceImpl(NewsDAO newsDAO) {
        this.newsDAO = newsDAO;
    }



    @Override
    public String checkIsNewsDeleted(int newsNo) throws Exception {

        return newsDAO.checkIsNewsDeleted(sqlSession, newsNo);

    }

    @Override
    public int checkReplyWriter(int replyNo) throws Exception {

        return newsDAO.checkReplyWriter(sqlSession, replyNo);

    }

    @Override
    public List<NewsDTO> selectNewsList(FilterDTO filterDTO) throws Exception {

        return newsDAO.selectNewsList(sqlSession, filterDTO);

    }

    @Override
    public int selectNewsCount(FilterDTO filterDTO) throws Exception {

        return newsDAO.selectNewsCount(sqlSession, filterDTO);

    }

    @Override
    public List<CategoryDTO> selectCategoryList() throws Exception {

        return newsDAO.selectCategoryList(sqlSession);

    }

    @Override
    public int insertNews(NewsDTO newsDTO) throws Exception {

        int result = 0;

        int newsResult = newsDAO.insertNews(sqlSession, newsDTO);

        if(newsResult == 1) {

            result = 1;

            int attachmentResult = 0;

            List<AttachmentDTO> attachmentList = newsDTO.getAttachments();

            if(attachmentList != null) {

                int newsNo = newsDTO.getNewsNo();

                for(AttachmentDTO attachment : attachmentList) {

                    attachment.setTargetNo(newsNo);
                    attachmentResult = newsDAO.insertNewsImg(sqlSession, attachment);

                }

                if(attachmentResult == attachmentList.size()) {

                    result = 2;

                }
                else {

                    throw new Exception("소식 이미지 업로드에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

                }

            }

        }
        else {

            throw new Exception("소식 작성에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

        }

        return result;

    }

    @Override
    public int addNewsViews(int newsNo) throws Exception {

        return newsDAO.addNewsViews(sqlSession, newsNo);

    }

    @Override
    public NewsDTO selectNewsDetail(RequestDTO requestDTO) throws Exception {

        return newsDAO.selectNewsDetail(sqlSession, requestDTO);

    }

    @Override
    public int insertNewsLike(LikeDTO likeDTO) throws Exception {

        return newsDAO.insertNewsLike(sqlSession, likeDTO);

    }

    @Override
    public int deleteNewsLike(LikeDTO likeDTO) throws Exception {

        return newsDAO.deleteNewsLike(sqlSession, likeDTO);

    }

    @Override
    public int insertReply(ReplyDTO replyDTO) throws Exception {

        return newsDAO.insertReply(sqlSession, replyDTO);

    }

    @Override
    public int updateReply(ReplyDTO replyDTO) throws Exception {

        return newsDAO.updateReply(sqlSession, replyDTO);

    }

    @Override
    public int deleteReply(ReplyDTO replyDTO) throws Exception {

        return newsDAO.deleteReply(sqlSession, replyDTO);

    }

    @Override
    public int insertReplyLike(LikeDTO likeDTO) throws Exception {

        return newsDAO.insertReplyLike(sqlSession, likeDTO);

    }

    @Override
    public int deleteReplyLike(LikeDTO likeDTO) throws Exception {

        return newsDAO.deleteReplyLike(sqlSession, likeDTO);

    }


}
