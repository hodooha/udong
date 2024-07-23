package com.multi.udong.share.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
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
     * @since 2024 -07-23
     */
    public int insertImg(SqlSessionTemplate sqlSession, List<AttachmentDTO> imgList) {


        return sqlSession.insert("ShareMapper.insertImg", imgList);
    }

    /**
     * 대여 물건 목록 조회
     *
     * @param sqlSession the sql session
     * @param locCode    the loc code
     * @return the list
     * @since 2024 -07-23
     */
    public List<ShaItemDTO> rentItemList(SqlSessionTemplate sqlSession, int locCode) {
        return (ArrayList) sqlSession.selectList("ShareMapper.rentItemList", locCode);
    }

    /**
     * 나눔 물건 목록 조회
     *
     * @param sqlSession the sql session
     * @param locCode    the loc code
     * @return the list
     * @since 2024 -07-23
     */
    public List<ShaItemDTO> giveItemList(SqlSessionTemplate sqlSession, int locCode) {
        return (ArrayList) sqlSession.selectList("ShareMapper.giveItemList", locCode);
    }

    /**
     * 물건 상세 정보 조회
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the item detail
     * @since 2024 -07-23
     */
    public ShaItemDTO getItemDetail(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) {

        return sqlSession.selectOne("ShareMapper.getItemDetail", itemDTO);

    }

    /**
     * 물건 첨부파일(사진) 목록 조회
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the item imgs
     * @since 2024 -07-23
     */
    public List<AttachmentDTO> getItemImgs(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) {

        return (ArrayList) sqlSession.selectList("ShareMapper.getItemImgs", itemDTO);
    }
}
