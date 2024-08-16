package com.multi.udong.news.service;

import com.multi.udong.news.model.dto.CategoryDTO;
import com.multi.udong.news.model.dto.FilterDTO;
import com.multi.udong.news.model.dto.NewsDTO;

import java.util.List;

public interface NewsService {

    List<NewsDTO> selectNewsList(FilterDTO filterDTO) throws Exception;

    int selectNewsCount(FilterDTO filterDTO) throws Exception;

    List<CategoryDTO> selectCategoryList() throws Exception;

    int insertNews(NewsDTO newsDTO) throws Exception;
}
