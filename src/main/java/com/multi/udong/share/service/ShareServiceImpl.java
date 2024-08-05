package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dao.ShareDAO;
import com.multi.udong.share.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ShareServiceImpl implements ShareService {

    private final SqlSessionTemplate sqlSession;
    private final ShareDAO shareDAO;
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;


    /**
     * 물건 상세 조회 시 보여지는 날짜 설정 메소드
     *
     * @param localDateTime the local date time
     * @return the string
     * @since 2024 -08-01
     */
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {

        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        String displayDate = null;
        if (diffTime < SEC){
            return diffTime + "초전";
        }
        diffTime = diffTime / SEC;
        if (diffTime < MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / MIN;
        if (diffTime < HOUR) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / HOUR;
        if (diffTime < DAY) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / DAY;
        if (diffTime < MONTH) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / MONTH;
        return diffTime + "년 전";

    };

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
     * @throws Exception the exception
     * @since 2024 -07-22
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertItem(ShaItemDTO itemDTO, List<AttachmentDTO> imgList) throws Exception {
        int itemNo = shareDAO.insertItem(sqlSession, itemDTO);
        if (itemNo < 1) {
            throw new Exception("물건 정보 등록을 실패했습니다.");
        }
        if (!imgList.isEmpty()) {
            for (AttachmentDTO img : imgList) {
                img.setTargetNo(itemNo);
            }
            if (shareDAO.insertImg(sqlSession, imgList) < 1) {
                throw new Exception("물건 첨부 사진 등록을 실패했습니다.");
            }
            ;
        }
    }

    /**
     * 물건 조회수 변경 -> 물건 상세 정보 조회 메서드 호출
     *
     * @param itemDTO the item dto
     * @throws Exception the exception
     */

    @Override
    public void plusViewCnt(ShaItemDTO itemDTO) throws Exception {

        if (shareDAO.plusViewCnt(sqlSession, itemDTO.getItemNo()) < 1) {
            throw new Exception("조회수 변경을 실패했습니다.");
        }
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
    public ShaItemDTO getItemDetail(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        ShaItemDTO item = shareDAO.getItemDetail(sqlSession, itemDTO);
        item.setDisplayDate(convertLocaldatetimeToTime(item.getModifiedAt()));
        List<AttachmentDTO> imgList = shareDAO.getItemImgs(sqlSession, item);
        item.setImgList(imgList);

        // 로그인한 유저의 해당 물건 찜 여부 확인
        ShaLikeDTO shaLikeDTO = new ShaLikeDTO();
        shaLikeDTO.setMemberNo(c.getMemberDTO().getMemberNo());
        shaLikeDTO.setItemNo(itemDTO.getItemNo());
        item.setLiked(shareDAO.getShaLike(sqlSession, shaLikeDTO) != null);

        return item;
    }


    /**
     * 물건 검색 및 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the sha item result dto
     * @throws Exception the exception
     * @since 2024 -07-24
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaItemResultDTO searchItems(ShaCriteriaDTO criteriaDTO) throws Exception {
        ShaItemResultDTO result = new ShaItemResultDTO();
        List<ShaItemDTO> itemList = shareDAO.searchItems(sqlSession, criteriaDTO);
        for(ShaItemDTO i : itemList){
            i.setDisplayDate(convertLocaldatetimeToTime(i.getModifiedAt()));
        }
        result.setItemList(itemList);
        result.setTotalCounts(getItemCounts(criteriaDTO));

        return result;
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

        return shareDAO.getItemCounts(sqlSession, criteriaDTO);
    }

    /**
     * 대여 및 나눔 신청
     *
     * @param reqDTO the req dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertRequest(ShaReqDTO reqDTO) throws Exception {

        // 동일 물건에 대한 대여 및 나눔 요청 유무 확인(status:"신청완료"만!)
        reqDTO.setStatusCode("RQD");
        if (findRequest(reqDTO) != null) {
            throw new Exception("이미 신청하셨습니다.");
        }

        if (shareDAO.insertRequest(sqlSession, reqDTO) < 1) {
            throw new Exception("신청을 실패했습니다.");
        }
        ;

        if (shareDAO.plusReqCnt(sqlSession, reqDTO.getReqItem()) < 1) {
            throw new Exception("신청자수 변경을 실패했습니다.");
        }

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

        return shareDAO.findRequest(sqlSession, reqDTO);
    }

    /**
     * 물건 수정 (파일 포함)
     *
     * @param itemDTO    the item dto
     * @param newImgList the new img list
     * @param delImgList the del img list
     * @throws Exception the exception
     * @since 2024 -07-30
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateItem(ShaItemDTO itemDTO, List<AttachmentDTO> newImgList, List<AttachmentDTO> delImgList) throws Exception {

        // 물건 정보 수정
        if (shareDAO.updateItem(sqlSession, itemDTO) < 1) {

            throw new Exception("물건 정보 수정을 실패했습니다.");
        }
        ;

        // 기존 물건 사진 중에서 유저가 삭제한 사진 삭제
        if (!delImgList.isEmpty()) {
            if (shareDAO.deleteImgList(sqlSession, delImgList) < 1) {

                throw new Exception("기존 사진 삭제를 실패했습니다.");
            }
            ;
        }

        // 유저가 등록한 새로운 사진 저장
        if (!newImgList.isEmpty()) {

            if (shareDAO.insertImg(sqlSession, newImgList) < 1) {
                throw new Exception("새로운 사진 등록을 실패했습니다.");
            }
        }

    }


    /**
     * 물건 삭제 (파일 포함)
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<AttachmentDTO> deleteItem(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        // 삭제할 물건 정보 가져오기
        itemDTO = getItemDetail(itemDTO, c);

        // 삭제할 사진 목록 가져오기 (삭제 성공 시 로컬폴더에서도 삭제하기 위해)
        List<AttachmentDTO> delImgs = itemDTO.getImgList();

        // 삭제할 첨부 사진 dto 설정
        AttachmentDTO imgDTO = new AttachmentDTO();
        imgDTO.setTargetNo(itemDTO.getItemNo());
        imgDTO.setTypeCode(itemDTO.getItemGroup());

        // 삭제하려는 유저와 물건 소유자가 같은지 확인
        if (itemDTO.getOwnerNo() != c.getMemberDTO().getMemberNo()) {
            throw new Exception("삭제 권한이 없습니다.");
        }

        // 현재 대여중인 물건인지 내역 확인
        if (itemDTO.getStatusCode().equals("RNT")) {
            throw new Exception("현재 대여중인 물건입니다. '반납완료' 처리 후 삭제가 가능합니다.");
        }

        // 물건 정보 삭제 (sha_items 테이블)
        if (shareDAO.deleteItem(sqlSession, itemDTO) < 1) {
            throw new Exception("물건 삭제를 실패했습니다.");
        }
        ;

        // 물건 첨부사진 삭제 (attachment 테이블)
        if (shareDAO.deleteImgByTarget(sqlSession, imgDTO) < 0) {
            throw new Exception("물건의 첨부사진 삭제에 실패했습니다.");
        }
        ;

        return delImgs;
    }

    /**
     * 물건 상태 업데이트
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateItStat(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        // 변경하려는 물건 정보 가져오기
        ShaItemDTO target = getItemDetail(itemDTO, c);

        // 변경하려는 유저와 물건 소유자가 같은지 확인
        if (target.getOwnerNo() != c.getMemberDTO().getMemberNo()) {
            throw new Exception("변경 권한이 없습니다.");
        }

        // 물건의 상태가 '대여중'이면 예외 던지기
        if (target.getStatusCode().equals("RNT")) {
            throw new Exception("현재 대여중인 물건입니다. '반납완료' 처리 후 일시중단이 가능합니다.");
        }

        // 물건 상태 '대여가능'일 경우 일시중단, '대여불가'일 경우 중단해제
        String status = target.getStatusCode().equals("AVL") ? "UNAV" : "AVL";
        itemDTO.setStatusCode(status);

        // 물건 상태 변경
        if (shareDAO.updateItStat(sqlSession, itemDTO) < 1) {
            throw new Exception("물건 상태 변경을 실패했습니다.");
        }
        ;
    }

    /**
     * 찜 등록 및 삭제 (+물건의 찜횟수 변경)
     *
     * @param likeDTO the like dto
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShaLike(ShaLikeDTO likeDTO) throws Exception {

        // 유저의 기존 찜 내역에 해당 물건이 없다면 추가
        if (shareDAO.getShaLike(sqlSession, likeDTO) == null) {
            if (shareDAO.insertShaLike(sqlSession, likeDTO) < 1) {
                throw new Exception("찜 등록을 실패했습니다.");
            }
            ;
            if (shareDAO.plusLikeCnt(sqlSession, likeDTO.getItemNo()) < 1) {
                throw new Exception("찜 횟수 변경을 실패했습니다.");
            }
        } else {  // 유저의 기존 찜 내역에 해당 물건이 있다면 삭제
            if (shareDAO.deleteShaLike(sqlSession, likeDTO) < 1) {
                throw new Exception("찜 삭제를 실패했습니다.");
            }
            ;
            if (shareDAO.minusLikeCnt(sqlSession, likeDTO.getItemNo()) < 1) {
                throw new Exception("찜 횟수 변경을 실패했습니다.");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaDreamResultDTO getLendList(ShaDreamCriteriaDTO criteriaDTO) throws Exception {

        // 결과값 초기 설정
        ShaDreamResultDTO result = new ShaDreamResultDTO();

        // 유저의 물건 리스트, 전체 개수 가져오기
        List<ShaItemDTO> lendList = shareDAO.getLendList(sqlSession, criteriaDTO);
        for(ShaItemDTO i : lendList){
            i.setDisplayDate(convertLocaldatetimeToTime(i.getModifiedAt()));
        }
        result.setLendList(lendList);
        result.setTotalCounts(shareDAO.getLendCounts(sqlSession, criteriaDTO));

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShaReqDTO> getRequesters(ShaReqDTO reqDTO) throws Exception {

        // 해당 물건의 거래 희망자 목록 조회
        List<ShaReqDTO> requesters = shareDAO.getRequesters(sqlSession, reqDTO);


        return requesters;
    }


}
