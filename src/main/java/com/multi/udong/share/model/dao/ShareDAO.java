package com.multi.udong.share.model.dao;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dto.*;
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

        return sqlSession.insert("ShareMapper.insertRequest", reqDTO);
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

    /**
     * 물건 정보 수정 (sha_items 테이블)
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    public int updateItem(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{

        return sqlSession.update("ShareMapper.updateItem", itemDTO);
    }

    /**
     * 첨부파일 no로 물건 첨부 사진 목록 삭제 (attachment 테이블)
     *
     * @param sqlSession the sql session
     * @param delImgList the del img list
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    public int deleteImgList(SqlSessionTemplate sqlSession, List<AttachmentDTO> delImgList) throws Exception{

        return sqlSession.delete("ShareMapper.deleteImgList", delImgList);
    }

    /**
     * 물건 삭제 (sha_items 테이블)
     *
     * @param sqlSession the sql session
     * @param target     the target
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    public int deleteItem(SqlSessionTemplate sqlSession, ShaItemDTO target) throws Exception {

        return sqlSession.delete("ShareMapper.deleteItem", target);
    }

    /**
     * target 정보로 물건 첨부 사진 삭제 (attachment 테이블)
     *
     * @param sqlSession the sql session
     * @param target     the target
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    public int deleteImgByTarget(SqlSessionTemplate sqlSession, AttachmentDTO target) throws Exception {

        return sqlSession.delete("ShareMapper.deleteImgByTarget", target);
    }

    /**
     * 물건 상태 업데이트
     *
     * @param sqlSession the sql session
     * @param itemDTO    the item dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    public int updateItStat(SqlSessionTemplate sqlSession, ShaItemDTO itemDTO) throws Exception{
        return sqlSession.update("ShareMapper.updateItStat", itemDTO);

    }

    public int plusViewCnt(SqlSessionTemplate sqlSession, int itemNo) throws Exception{
        return sqlSession.update("ShareMapper.plusViewCnt", itemNo);
    }

    public ShaLikeDTO getShaLike(SqlSessionTemplate sqlSession, ShaLikeDTO likeDTO) throws Exception{
        return sqlSession.selectOne("ShareMapper.getShaLike", likeDTO);
    }

    public int insertShaLike(SqlSessionTemplate sqlSession, ShaLikeDTO likeDTO) throws Exception{
        return sqlSession.insert("ShareMapper.insertShaLike", likeDTO);
    }

    public int deleteShaLike(SqlSessionTemplate sqlSession, ShaLikeDTO likeDTO) throws Exception{
        return sqlSession.delete("ShareMapper.deleteShaLike", likeDTO) ;
    }

    public int plusLikeCnt(SqlSessionTemplate sqlSession, int itemNo) throws Exception {
        return sqlSession.update("ShareMapper.plusLikeCnt", itemNo);
    }

    public int minusLikeCnt(SqlSessionTemplate sqlSession, int itemNo) throws Exception {
        return sqlSession.update("ShareMapper.minusLikeCnt", itemNo);
    }

    public int plusReqCnt(SqlSessionTemplate sqlSession, int itemNo) throws Exception{
        return sqlSession.update("ShareMapper.plusReqCnt", itemNo);
    }
}
