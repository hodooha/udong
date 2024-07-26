package com.multi.udong.club.model.dao;

import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;
import com.multi.udong.club.model.dto.FilterDTO;
import com.multi.udong.club.model.dto.RequestDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 모임 DAO
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Repository
public class ClubDAO {

    /**
     * 모임의 주제(카테고리) 리스트 조회
     *
     * @param sqlSession the sql session
     * @return the list
     * @since 2024 -07-25
     */
    public List<CategoryDTO> selectCategoryList(SqlSessionTemplate sqlSession) {

        return sqlSession.selectList("clubMapper.selectCategoryList");

    }


    /**
     * 모임 생성
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertClub(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertClub", clubDTO);

    }


    /**
     * 모임 대표 이미지 insert
     *
     * @param sqlSession    the sql session
     * @param attachmentDTO the attachment dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertClubImg(SqlSessionTemplate sqlSession, AttachmentDTO attachmentDTO) {

        return sqlSession.insert("clubMapper.insertClubImg", attachmentDTO);

    }


    /**
     * 모임 생성 직후 채팅방 코드 업데이트
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int updateChatroomCode(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.update("clubMapper.updateChatroomCode", clubDTO);

    }


    /**
     * 모임 생성 직후 모임장을 모임의 멤버로 insert
     *
     * @param sqlSession the sql session
     * @param clubDTO    the club dto
     * @return the int
     * @since 2024 -07-25
     */
    public int insertMaster(SqlSessionTemplate sqlSession, ClubDTO clubDTO) {

        return sqlSession.insert("clubMapper.insertMaster", clubDTO);

    }


    /**
     * 모임 리스트 조회
     *
     * @param sqlSession the sql session
     * @param filterDTO  the filter dto
     * @return the list
     * @since 2024 -07-25
     */
    public List<ClubDTO> selectClubList(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectList("clubMapper.selectClubList", filterDTO);

    }


    /**
     * 모임의 개수 조회
     *
     * @param sqlSession the sql session
     * @param filterDTO  the filter dto
     * @return the int
     * @since 2024 -07-25
     */
    public int selectClubCount(SqlSessionTemplate sqlSession, FilterDTO filterDTO) {

        return sqlSession.selectOne("clubMapper.selectClubCount", filterDTO);

    }


    /**
     * 모임 홈 조회
     *
     * @param sqlSession the sql session
     * @param requestDTO the request dto
     * @return the club dto
     * @since 2024 -07-25
     */
    public ClubDTO selectClubHome(SqlSessionTemplate sqlSession, RequestDTO requestDTO) {

        return sqlSession.selectOne("clubMapper.selectClubHome", requestDTO);

    }
}
