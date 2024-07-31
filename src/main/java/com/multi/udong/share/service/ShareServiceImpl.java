package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.share.model.dao.ShareDAO;
import com.multi.udong.share.model.dto.ShaCatDTO;
import com.multi.udong.share.model.dto.ShaCriteriaDTO;
import com.multi.udong.share.model.dto.ShaItemDTO;
import com.multi.udong.share.model.dto.ShaReqDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 대여 및 나눔 Service
 *
 * @author 하지은
 * @since 2024 -07-21
 */
@RequiredArgsConstructor
@Service
public class ShareServiceImpl implements ShareService {

    private final SqlSessionTemplate sqlSession;
    private final ShareDAO shareDAO;

    /**
     * 물건 카테고리 조회
     *
     * @return the sha cat
     * @throws Exception the exception
     * @since 2024 -07-21
     */
    @Override
    public List<ShaCatDTO> getShaCat() throws Exception {
        List<ShaCatDTO> list = shareDAO.getShaCat(sqlSession);

        return list;
    }

    /**
     * 물건 등록 (물건 정보 및 사진)
     *
     * @param itemDTO the item dto
     * @param imgList the img list
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-22
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception {
        int itemNo = shareDAO.insertItem(sqlSession, itemDTO);
        int result = itemNo > 0 ? 1 : 0;
        if (!imgList.isEmpty()) {
            for (AttachmentDTO img : imgList) {
                img.setTargetNo(itemNo);
            }
            int imgResult = shareDAO.insertImg(sqlSession, imgList);
            if (imgResult < 1) {
                result = 0;

            }
        }
        return result;
    }

    /**
     * 물건 상세 정보 조회 (물건 정보 & 사진목록)
     *
     * @param itemDTO the item dto
     * @return the item detail
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaItemDTO getItemDetail(ShaItemDTO itemDTO) throws Exception {

        ShaItemDTO item = shareDAO.getItemDetail(sqlSession, itemDTO);
        List<AttachmentDTO> imgList = shareDAO.getItemImgs(sqlSession, item);
        item.setImgList(imgList);

        return item;
    }


    /**
     * 물건 검색
     *
     * @param criteriaDTO the criteria dto
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    @Override
    public List<ShaItemDTO> searchItems(ShaCriteriaDTO criteriaDTO) throws Exception {

        List<ShaItemDTO> itemList = shareDAO.searchItems(sqlSession, criteriaDTO);


        return itemList;
    }


    /**
     * 물건 총 개수 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the item counts
     * @throws Exception the exception
     */
    @Override
    public int getItemCounts(ShaCriteriaDTO criteriaDTO) throws Exception {

        int totalCount = shareDAO.getItemCounts(sqlSession, criteriaDTO);

        return totalCount;
    }

    /**
     * 대여 및 나눔 신청
     *
     * @param reqDTO the req dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int insertRequest(ShaReqDTO reqDTO) throws Exception {

        int result = shareDAO.insertRequest(sqlSession, reqDTO);

        return result;
    }


    /**
     * 기존 대여 및 나눔 신청 내역 조회
     *
     * @param reqDTO the req dto
     * @return the sha req dto
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public ShaReqDTO findRequest(ShaReqDTO reqDTO) throws Exception {

        ShaReqDTO result = shareDAO.findRequest(sqlSession, reqDTO);

        return result;
    }


    /**
     * 물건 수정 (파일 포함)
     *
     * @param itemDTO    the item dto
     * @param newImgList the new img list
     * @param delImgList the del img list
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateItem(ShaItemDTO itemDTO, List<AttachmentDTO> newImgList, List<AttachmentDTO> delImgList) throws Exception {

        // 물건 정보 수정
        int result = shareDAO.updateItem(sqlSession, itemDTO);

        // 기존 물건 사진 중에서 유저가 삭제한 사진 삭제
        if(!delImgList.isEmpty()){
            int deleteResult = shareDAO.deleteImgList(sqlSession, delImgList);
            if (deleteResult < 1) {
                result = 0;
            }
        }

        // 유저가 등록한 새로운 사진 저장
        if (!newImgList.isEmpty()) {
            int imgResult = shareDAO.insertImg(sqlSession, newImgList);
            if (imgResult < 1) {
                result = 0;
            }
        }

        return result;
    }

    /**
     * 물건 삭제
     *
     * @param target the target
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteItem(ShaItemDTO target) throws Exception {
        int result = 1;

        // 물건 정보 삭제 (sha_items 테이블)
        if(shareDAO.deleteItem(sqlSession, target) < 0) {
            result = 0;
        };

        // 물건 첨부사진 삭제 (attachment 테이블)
        AttachmentDTO delImg = new AttachmentDTO();
        delImg.setTargetNo(target.getItemNo());
        delImg.setTypeCode(target.getItemGroup());
        if(shareDAO.deleteImgByTarget(sqlSession, delImg) < 0){
            result = 0;
        };

        return result;
    }


}
