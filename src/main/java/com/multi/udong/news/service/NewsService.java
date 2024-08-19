package com.multi.udong.news.service;

import com.multi.udong.club.model.dto.ReportDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.common.model.dto.LocationDTO;
import com.multi.udong.news.model.dto.*;

import java.util.List;

public interface NewsService {

    String checkIsNewsDeleted(int newsNo) throws Exception;

    String checkIsAdDeleted(int adNo) throws Exception;

    int checkNewsWriter(int newsNo) throws Exception;

    int checkReplyWriter(int replyNo) throws Exception;

    List<NewsDTO> selectNewsList(FilterDTO filterDTO) throws Exception;

    int selectNewsCount(FilterDTO filterDTO) throws Exception;

    List<CategoryDTO> selectCategoryList() throws Exception;

    List<LocationDTO> selectLocationList() throws Exception;

    int insertNews(NewsDTO newsDTO) throws Exception;

    int addNewsViews(int newsNo) throws Exception;

    NewsDTO selectNewsDetail(RequestDTO requestDTO) throws Exception;

    int insertNewsLike(LikeDTO likeDTO) throws Exception;

    int deleteNewsLike(LikeDTO likeDTO) throws Exception;

    int insertReply(ReplyDTO replyDTO) throws Exception;

    int updateReply(ReplyDTO replyDTO) throws Exception;

    int deleteReply(ReplyDTO replyDTO) throws Exception;

    int insertReplyLike(LikeDTO likeDTO) throws Exception;

    int deleteReplyLike(LikeDTO likeDTO) throws Exception;

    int reportNews(ReportDTO reportDTO) throws Exception;

    int updateNews(NewsDTO newsDTO) throws Exception;

    AttachmentDTO selectAttachment(int fileNo) throws Exception;

    int updateAttachment(AttachmentDTO newImg) throws Exception;

    int deleteAttachment(AttachmentDTO deletedImg) throws Exception;

    int insertAttachment(AttachmentDTO newImg) throws Exception;

    int deleteNews(int newsNo) throws Exception;

    List<NewsDTO> selectHotNewsList(FilterDTO filterDTO) throws Exception;

    int insertAd(NewsDTO newsDTO) throws Exception;

    List<NewsDTO> selectAdList(FilterDTO filterDTO) throws Exception;

    int addAdViews(int adNo) throws Exception;

    NewsDTO selectAdDetail(int adNo) throws Exception;

    int deleteAd(int adNo) throws Exception;
}
