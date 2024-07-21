package com.multi.udong.club.service;

import com.multi.udong.club.model.dao.ClubDAO;
import com.multi.udong.club.model.dto.CategoryDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = {Exception.class})
@Service
public class ClubServiceImpl implements ClubService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final ClubDAO clubDAO;

    public ClubServiceImpl(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }

    @Override
    public List<CategoryDTO> selectCategoryList() throws Exception {

        return clubDAO.selectCategoryList(sqlSession);

    }

}
