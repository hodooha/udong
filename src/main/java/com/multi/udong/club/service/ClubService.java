package com.multi.udong.club.service;

import com.multi.udong.club.model.dto.CategoryDTO;

import java.util.List;

public interface ClubService {

    List<CategoryDTO> selectCategoryList() throws Exception;

}
