package com.multi.udong.share.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaCriteriaDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.model.dto.ShaReqDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * 대여 및 나눔 DAO
 *
 * @author 하지은
 * @since 2024 -07-23
 */
@Repository
public class ShareDAO {

    /**
     * 물건 카테고리 목록 조회
     *
     * @param sqlSession the sql session
     * @return the sha cat
     * @throws Exception the exception
     */
    public List<ShaCatDTO> getShaCat(SqlSessionTemplate sqlSession) throws Exception{

        return (ArrayList) sqlSession.selectList("ShareMapper.getShaCat");
    }

    /**
     * 물건 등록 (sha_items 테이블)
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    public int insertItem(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{
        sqlSession.insert("ShareMapper.insertItem", itemDTO);
        return itemDTO.getItemNo();
    }

    /**
     * 물건의 첨부파일(사진) 등록
     *
     * @param sqlSession the sql session
     * @param imgList    the img list
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    public int insertImg(SqlSessionTemplate sqlSession, List<AttachmentDTO> imgList) throws Exception{
        return sqlSession.insert("ShareMapper.insertImg", imgList);
    }

    /**
     * 물건 상세 정보 조회
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the item detail
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    public ShaItemDTO getItemDetail(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{

        return sqlSession.selectOne("ShareMapper.getItemDetail", itemDTO);

    }

    /**
     * 물건 첨부파일(사진) 목록 조회
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the item imgs
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    public List<AttachmentDTO> getItemImgs(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{

        return (ArrayList) sqlSession.selectList("ShareMapper.getItemImgs", itemDTO);
    }


    /**
     * 물건 검색
     *
     * @param sqlSession  the sql session
     * @param criteriaDTO the criteria dto
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    public List<ShaItemDTO> searchItems(SqlSessionTemplate sqlSession, ShaCriteriaDTO criteriaDTO) throws Exception{
        return (ArrayList) sqlSession.selectList("ShareMapper.searchItems", criteriaDTO);
    }


    /**
     * 물건 총 개수 조회
     *
     * @param sqlSession  the sql session
     * @param criteriaDTO the criteria dto
     * @return the item counts
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    public int getItemCounts(SqlSessionTemplate sqlSession, ShaCriteriaDTO criteriaDTO) throws Exception{
        return sqlSession.selectOne("ShareMapper.getItemCounts", criteriaDTO);

    }

    /**
     * 대여 및 나눔 신청
     *
     * @param sqlSession the sql session
     * @param reqDTO     the req dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    public int insertRequest(SqlSessionTemplate sqlSession, ShaReqDTO reqDTO) throws Exception{

        return sqlSession.insert("ShareMapper.shaRequest", reqDTO);
    }

    /**
     * 기존 대여 및 나눔 신청 내역 조회
     *
     * @param sqlSession the sql session
     * @param reqDTO     the req dto
     * @return the sha req dto
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    public ShaReqDTO findRequest(SqlSessionTemplate sqlSession, ShaReqDTO reqDTO) throws Exception{

        return sqlSession.selectOne("ShareMapper.findRequest", reqDTO);
    }
}
