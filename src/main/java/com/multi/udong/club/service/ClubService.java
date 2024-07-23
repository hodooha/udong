package com.multi.udong.club.service;

import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;

import java.util.List;

public interface ClubService {

    List<CategoryDTO> selectCategoryList() throws Exception;

    int insertClub(ClubDTO clubDTO) throws Exception;
}
