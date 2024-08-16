package com.multi.udong.news.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.news.model.dao.NewsDAO;
import com.multi.udong.news.model.dto.CategoryDTO;
import com.multi.udong.news.model.dto.FilterDTO;
import com.multi.udong.news.model.dto.NewsDTO;
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

}
