package com.multi.udong.club.model.dao;

import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;
import com.multi.udong.club.model.dto.FilterDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClubDAO {

    public List<CategoryDTO> selectCategoryList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("clubMapper.selectCategoryList");

    }

    public int insertClub(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertClub", clubDTO);

    }

    public int insertClubImg(SqlSessionTemplate sqlSession, AttachmentDTO attachmentDTO) {

        return sqlSession.insert("clubMapper.insertClubImg", attachmentDTO);

    }

    public int updateChatroomCode(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.update("clubMapper.updateChatroomCode", clubDTO);

    }

    public int insertMaster(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertMaster", clubDTO);

    }

    public List<ClubDTO> selectClubList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectClubList", filterDTO);

    }

    public int selectClubCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectClubCount", filterDTO);

    }
}
