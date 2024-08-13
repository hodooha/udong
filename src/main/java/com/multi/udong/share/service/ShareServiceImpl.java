package com.multi.udong.share.service;

import com.multi.udong.common.model.dto.AttachmentDTO;
import com.multi.udong.login.service.CustomUserDetails;
import com.multi.udong.share.model.dao.ShareDAO;
import com.multi.udong.share.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


/**
 * The type Share service.
 *
 * @author 하지은
 * @since 2024 -08-11
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
     * @param c       the c
     * @return the item detail with view cnt
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaItemDTO getItemDetailWithViewCnt(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        // db에서 물건 상세정보 가져오기
        ShaItemDTO result = getItemDetail(itemDTO, c);
        if (result.getDeletedAt() != null) {
            throw new Exception("삭제된 물건입니다.");
        }

        if (shareDAO.plusViewCnt(sqlSession, itemDTO.getItemNo()) < 1) {
            throw new Exception("조회수 변경을 실패했습니다.");
        }
        ;

        return result;
    }


    /**
     * 물건 상세 정보 조회 (물건 정보 & 사진목록)
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @return the item detail
     * @throws Exception the exception
     * @since 2024 -07-23
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaItemDTO getItemDetail(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        ShaItemDTO item = shareDAO.getItemDetail(sqlSession, itemDTO);
        if(item == null){
            throw new Exception("존재하지 않는 물건입니다.");
        }
        item.convertLocaldatetimeToTime();
        item.maskNickname();
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
        for (ShaItemDTO i : itemList) {
            i.convertLocaldatetimeToTime();
            i.maskNickname();
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

        // 요청 db에 저장
        if (shareDAO.insertRequest(sqlSession, reqDTO) < 1) {
            throw new Exception("신청을 실패했습니다.");
        }
        ;

        // 물건 신청자수 증가
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
    public void deleteItem(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        // 삭제할 물건 정보 가져오기
        itemDTO = getItemDetail(itemDTO, c);

//        // 삭제할 사진 목록 가져오기 (삭제 성공 시 로컬폴더에서도 삭제하기 위해)
//        List<AttachmentDTO> delImgs = itemDTO.getImgList();
//
//        // 삭제할 첨부 사진 dto 설정
//        AttachmentDTO imgDTO = new AttachmentDTO();
//        imgDTO.setTargetNo(itemDTO.getItemNo());
//        imgDTO.setTypeCode(itemDTO.getItemGroup());

        // 삭제하려는 유저와 물건 소유자가 같은지 확인
        if (itemDTO.getOwnerNo() != c.getMemberDTO().getMemberNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            throw new Exception("삭제 권한이 없습니다.");
        }

        // 현재 대여중인 물건인지 내역 확인
        if (itemDTO.getStatusCode().equals("RNT")) {
            throw new Exception("현재 대여중인 물건입니다. '반납완료' 처리 후 삭제가 가능합니다.");
        }

        // 물건 정보 수정 (sha_items 테이블)
        if (shareDAO.deleteItem(sqlSession, itemDTO) < 1) {
            throw new Exception("물건 삭제를 실패했습니다.");
        }
        ;

//        // 물건 첨부사진 삭제 (attachment 테이블)
//        if (shareDAO.deleteImgByTarget(sqlSession, imgDTO) < 0) {
//            throw new Exception("물건의 첨부사진 삭제에 실패했습니다.");
//        }
        ;

    }

    /**
     * 물건 상태 업데이트
     *
     * @param itemDTO the item dto
     * @param c       the c
     * @return
     * @throws Exception the exception
     * @since 2024 -07-31
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateItStat(ShaItemDTO itemDTO, CustomUserDetails c) throws Exception {

        // 변경하려는 물건 정보 가져오기
        ShaItemDTO target = getItemDetail(itemDTO, c);

        // 변경하려는 유저와 물건 소유자가 같은지 확인
        if (target.getOwnerNo() != c.getMemberDTO().getMemberNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
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
     * @param c
     * @return
     * @throws Exception the exception
     * @since 2024 -08-01
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaItemDTO updateShaLike(ShaLikeDTO likeDTO, CustomUserDetails c) throws Exception {

        // 유저의 기존 찜 내역에 해당 물건이 없다면 추가
        if (shareDAO.getShaLike(sqlSession, likeDTO) == null) {
            if (shareDAO.insertShaLike(sqlSession, likeDTO) < 1) {
                throw new Exception("찜 등록을 실패했습니다.");
            }
            ;
            //물건 찜 횟수 변경 (+1)
            if (shareDAO.plusLikeCnt(sqlSession, likeDTO.getItemNo()) < 1) {
                throw new Exception("찜 횟수 변경을 실패했습니다.");
            }
        } else {  // 유저의 기존 찜 내역에 해당 물건이 있다면 삭제
            if (shareDAO.deleteShaLike(sqlSession, likeDTO) < 1) {
                throw new Exception("찜 삭제를 실패했습니다.");
            }
            ;
            //물건 찜 횟수 변경 (1-)
            if (shareDAO.minusLikeCnt(sqlSession, likeDTO.getItemNo()) < 1) {
                throw new Exception("찜 횟수 변경을 실패했습니다.");
            }
        }

        ShaItemDTO item = new ShaItemDTO();
        item.setItemNo(likeDTO.getItemNo());
        item = getItemDetail(item, c);

        return item;
    }

    /**
     * 빌려드림 목록 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the lend list
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaDreamResultDTO getLendList(ShaDreamCriteriaDTO criteriaDTO) throws Exception {

        // 결과값 초기 설정
        ShaDreamResultDTO result = new ShaDreamResultDTO();

        // 유저의 물건 리스트, 전체 개수 가져오기
        List<ShaItemDTO> lendList = shareDAO.getLendList(sqlSession, criteriaDTO);
        for (ShaItemDTO i : lendList) {
            i.convertLocaldatetimeToTime();
        }
        result.setLendList(lendList);
        result.setTotalCounts(shareDAO.getLendCounts(sqlSession, criteriaDTO));

        return result;
    }

    /**
     * 해당 물건의 신청자 목록 조회
     *
     * @param reqDTO the req dto
     * @return the requesters
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ShaReqDTO> getRequesters(ShaReqDTO reqDTO) throws Exception {

        // 해당 물건의 거래 희망자 목록 조회
        List<ShaReqDTO> requesters = shareDAO.getRequesters(sqlSession, reqDTO);


        return requesters;
    }


    /**
     * 대여인의 차용인 선택 및 대여 확정처리
     *
     * @param reqDTO the req dto
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approveReq(ShaReqDTO reqDTO) throws Exception {

        if (!reqDTO.getReturnDate().isAfter(LocalDate.now())) {
            throw new Exception("반납예정일은 오늘 날짜부터 선택 가능합니다.");
        }

        if (shareDAO.updateReqStat(sqlSession, reqDTO) < 1) {
            throw new Exception("대여 확정를 실패했습니다.");
        }
        ;

        ShaItemDTO target = new ShaItemDTO();
        target.setItemNo(reqDTO.getReqItem());
        target.setStatusCode("RNT");
        if (shareDAO.updateItStat(sqlSession, target) < 1) {
            throw new Exception("물건 상태 변경을 실패했습니다.");
        }
        ;

        // 물건 신청자수 변경 (-1)
        if (shareDAO.minusReqCnt(sqlSession, reqDTO.getReqItem()) < 1) {
            throw new Exception("신청자수 변경을 실패했습니다.");
        }
        ;


    }

    /**
     * 대여인의 차용인 평가 및 반납완료 처리
     *
     * @param evalDTO the eval dto
     * @param c       the c
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void evalWithReturnReq(ShaEvalDTO evalDTO, CustomUserDetails c) throws Exception {

        // 로그인한 유저가 물건의 주인과 동일인인지 확인
        if (c.getMemberDTO().getMemberNo() != evalDTO.getEvrNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            throw new Exception("권한이 없습니다.");
        }

        // 물건 제공자의 차용인 평가 등록
        if (shareDAO.insertEval(sqlSession, evalDTO) < 1) {
            throw new Exception("평가 등록을 실패했습니다.");
        }
        ;

        // 등록된 평가 토대로 차용인 점수 & 레벨 변경
        if (shareDAO.updateScore(sqlSession, evalDTO) < 1) {
            throw new Exception("차용인 점수 업데이트를 실패했습니다.");
        }
        ;
        System.out.println("=== 최종 점수 === ");
        System.out.println(evalDTO);
        if (shareDAO.updateLevel(sqlSession, evalDTO) < 1) {
            throw new Exception("차용인 레벨 업데이트를 실패했습니다.");
        }
        ;

        // 대여인 점수 & 레벨 변경
        ShaEvalDTO lenderEval = new ShaEvalDTO();
        lenderEval.setEveNo(evalDTO.getEvrNo());
        lenderEval.setRating(5);
        if (shareDAO.updateScore(sqlSession, lenderEval) < 1) {
            throw new Exception("대여인 점수 업데이트를 실패했습니다.");
        }
        ;
        System.out.println("=== 최종 점수 === ");
        System.out.println(lenderEval);
        if (shareDAO.updateLevel(sqlSession, lenderEval) < 1) {
            throw new Exception("대여인 레벨 업데이트를 실패했습니다.");
        }
        ;

        // 반납완료 처리
        ShaReqDTO reqDTO = new ShaReqDTO();
        reqDTO.setReqNo(evalDTO.getReqNo());
        reqDTO.setStatusCode("RTR");
        if (shareDAO.updateReqStat(sqlSession, reqDTO) < 1) {
            throw new Exception("반납완료 처리를 실패했습니다.");
        }
        ;

        // 물건 상태 변경
        ShaItemDTO itemDTO = new ShaItemDTO();
        itemDTO.setItemNo(evalDTO.getReqItem());
        itemDTO.setStatusCode("AVL");
        if (shareDAO.updateItStat(sqlSession, itemDTO) < 1) {
            throw new Exception("물건 상태 변경을 실패했습니다.");
        }
        ;

        // 물건 거래횟수 변경 (+1)
        if (shareDAO.plusDealCnt(sqlSession, evalDTO.getReqItem()) < 1) {
            throw new Exception("거래횟수 변경을 실패했습니다.");
        }
        ;

    }


    /**
     * 요청드림 조회
     *
     * @param criteriaDTO the criteria dto
     * @return the borrow list
     * @throws Exception the exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShaDreamResultDTO getBorrowList(ShaDreamCriteriaDTO criteriaDTO) throws Exception {

        // 결과값 초기 설정
        ShaDreamResultDTO result = new ShaDreamResultDTO();

        // 유저가 신청한 전체 요청 가져오기
        List<ShaReqDTO> borrowList = shareDAO.getReqList(sqlSession, criteriaDTO);

        System.out.println("리스트" + borrowList);
        for (ShaReqDTO r : borrowList) {
            r.getItemDTO().convertLocaldatetimeToTime();
        }

        result.setBorrowList(borrowList);
        result.setTotalCounts(shareDAO.getReqCounts(sqlSession, criteriaDTO));

        return result;
    }

    /**
     * 대여 및 나눔 신청 취소
     *
     * @param shaReqDTO the sha req dto
     * @param c         the c
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteReq(ShaReqDTO shaReqDTO, CustomUserDetails c) throws Exception {

        // 요청 정보 db에서 가져오기
        ShaReqDTO target = shareDAO.getReqByReqNo(sqlSession, shaReqDTO.getReqNo());

        // 요청자와 로그인한 유저가 같은지 확인
        if (target.getRqstNo() != c.getMemberDTO().getMemberNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            throw new Exception("권한이 없습니다.");
        }

        // 요청 상태가 '신청완료'인지 확인, 아니면 취소 불가
        if (!target.getStatusCode().equals("RQD")) {
            throw new Exception("'신청완료' 상태 외에는 신청취소가 불가합니다.");
        }


        // 요청 내역에서 삭제
        if (shareDAO.deleteReq(sqlSession, target) < 1) {
            throw new Exception("대여 및 나눔 신청 삭제를 실패했습니다.");
        }

        // 물건 신청자수 변경 (-1)
        if (shareDAO.minusReqCnt(sqlSession, target.getReqItem()) < 1) {
            throw new Exception("신청자수 변경을 실패했습니다.");
        }
        ;

    }

    /**
     * 차용인의 대여인 평가 및 req 상태 변경
     *
     * @param evalDTO the eval dto
     * @param c       the c
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void evalWithEndReq(ShaEvalDTO evalDTO, CustomUserDetails c) throws Exception {

        // 로그인한 유저가 차용인과 동일인인지 확인
        if (c.getMemberDTO().getMemberNo() != evalDTO.getEvrNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            throw new Exception("권한이 없습니다.");
        }

        // 차용인의 대여인 평가 등록
        if (shareDAO.insertEval(sqlSession, evalDTO) < 1) {
            throw new Exception("평가 등록을 실패했습니다.");
        }
        ;

        // 등록된 평가 토대로 대여인 점수 & 레벨 변경
        if (shareDAO.updateScore(sqlSession, evalDTO) < 1) {
            throw new Exception("대여인 점수 업데이트를 실패했습니다.");
        }
        ;
        System.out.println("=== 최종 점수 === ");
        System.out.println(evalDTO);
        if (shareDAO.updateLevel(sqlSession, evalDTO) < 1) {
            throw new Exception("대여인 레벨 업데이트를 실패했습니다.");
        }
        ;

        // 차용인 점수 & 레벨 변경
        ShaEvalDTO lenderEval = new ShaEvalDTO();
        lenderEval.setEveNo(evalDTO.getEvrNo());
        lenderEval.setRating(1);
        if (shareDAO.updateScore(sqlSession, lenderEval) < 1) {
            throw new Exception("차용인 점수 업데이트를 실패했습니다.");
        }
        ;
        System.out.println("=== 최종 점수 === ");
        System.out.println(lenderEval);
        if (shareDAO.updateLevel(sqlSession, lenderEval) < 1) {
            throw new Exception("차용인 레벨 업데이트를 실패했습니다.");
        }
        ;

        // 평가완료 처리
        ShaReqDTO reqDTO = new ShaReqDTO();
        reqDTO.setReqNo(evalDTO.getReqNo());
        reqDTO.setStatusCode("REV");
        if (shareDAO.updateReqStat(sqlSession, reqDTO) < 1) {
            throw new Exception("평가완료 처리를 실패했습니다.");
        }
        ;


    }

    /**
     * 신고 등록
     *
     * @param reportDTO the report dto
     * @param c         the c
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Override
    public void insertReport(ShaReportDTO reportDTO, CustomUserDetails c) throws Exception {

        // 신고자와 로그인한 유저가 다를 경우 예외 던지기
        if (reportDTO.getReporterMember() != c.getMemberDTO().getMemberNo() && !c.getMemberDTO().getAuthority().equals("ROLE_ADMIN")) {
            throw new Exception("권한이 없습니다.");
        }

        if (shareDAO.insertReport(sqlSession, reportDTO) < 1) {
            throw new Exception("신고 접수를 실패했습니다.");
        }

    }

    /**
     * 추천 물건 조회
     *
     * @param c the c
     * @return the list
     * @throws Exception the exception
     * @since 2024 -08-11
     */
    @Override
    public List<ShaItemDTO> recommendItem(CustomUserDetails c) throws Exception {

        List<ShaItemDTO> itemList = null;
        List<ShaCatDTO> catsInMemReq = shareDAO.getCatsInMemReq(sqlSession, c.getMemberDTO().getMemberNo());

        System.out.println("===== 유저가 대여 및 나눔 요청한 물건 카테고리 =====");
        System.out.println(catsInMemReq);

        ShaCriteriaDTO criteriaDTO = new ShaCriteriaDTO();
        criteriaDTO.setLocCode(c.getMemberDTO().getMemAddressDTO().getLocationCode());
        criteriaDTO.setGroup("rent");
        criteriaDTO.setStart(1);
        criteriaDTO.setEnd(12);

        if (catsInMemReq.isEmpty()) {
            itemList = shareDAO.getHotItems(sqlSession, criteriaDTO);
            for(ShaItemDTO i : itemList){
                i.convertLocaldatetimeToTime();
                i.maskNickname();
            }
            System.out.println("===== 요청했던 물건이 없을때의 추천리스트!! =====");
            System.out.println(itemList);
        } else {
            criteriaDTO.setCatList(catsInMemReq);
            criteriaDTO.setMemberNo(c.getMemberDTO().getMemberNo());
            itemList = shareDAO.getHotItems(sqlSession, criteriaDTO);
            if (itemList.size() < 12) {
                int i = 12 - itemList.size();
                criteriaDTO.setEnd(i);
                criteriaDTO.setCatList(null);
                List<ShaItemDTO> fillItems = shareDAO.getHotItems(sqlSession, criteriaDTO);
                itemList.addAll(fillItems);
            }
            for(ShaItemDTO i : itemList){
                i.convertLocaldatetimeToTime();
                i.maskNickname();
            }
            System.out.println("===== 요청했던 물건이 있을때 추천리스트!! =====");
            System.out.println(itemList);
        }

        return itemList;
    }


    /**
     * 나눔 물건 중 오늘자 마감 조회, 있으면 래플 실행
     *
     * @throws Exception the exception
     * @since 2024 -08-08
     */
// 매일 오후 12시에 나눔 품목들의 마감일 확인
    @Scheduled(cron = "0 27 14 * * ?")
    public void raffleGiveItem() throws Exception {

        // 오늘 날짜인 나눔 물건 조회
        List<ShaItemDTO> itemList = shareDAO.getRaffleItem(sqlSession);

        System.out.println("=====추첨해야할 나눔 물건 목록=====");
        System.out.println(itemList);

        // 오늘 날짜인 나눔 물건의 당첨자 추첨
        if (itemList != null) {
            letsRaffle(itemList);
        }

    }

    // 나눔 물건 당첨자 선정 및 req, item 상태 변경
    @Transactional(rollbackFor = Exception.class)
    private void letsRaffle(List<ShaItemDTO> itemList) throws Exception {

        for (ShaItemDTO i : itemList) {
            // 해당 물건을 나눔 요청한 요청자 목록 조회
            ShaReqDTO reqDTO = new ShaReqDTO();
            reqDTO.setReqItem(i.getItemNo());
            reqDTO.setStatusCode("RQD");
            List<ShaReqDTO> reqList = shareDAO.getRequesters(sqlSession, reqDTO);

            if (reqList.isEmpty()) {
                if (shareDAO.postponeExpiry(sqlSession, i) < 1) {
                    throw new Exception("마감일 연기를 실패했습니다.");
                }

                System.out.println("===== 나눔일 연기된 물건 =====");
                System.out.println(i);
            } else {

                System.out.println("===== 나눔 물건의 요청자 목록 =====");
                System.out.println(reqList);

                // 당첨자 추첨
                int luckyNum = (int) (Math.random() * reqList.size());
                ShaReqDTO winner = reqList.get(luckyNum);

                System.out.println("===== 럭키넘버!!! =====");
                System.out.println(luckyNum);
                System.out.println("===== 당첨자!!! =====");
                System.out.println(winner);

                // 당첨자 req 상태 '당첨'으로 변경, 당첨자 외 다른 req 상태 '낙첨'으로 변경
                if (shareDAO.updateReqAfterRaffle(sqlSession, winner) < 1) {
                    throw new Exception("요청 상태 변경을 실패했습니다.");
                }

                // 물건 상태 변경
                i.setStatusCode("GVD");
                if (shareDAO.updateItStat(sqlSession, i) < 1) {
                    throw new Exception("물건 상태 변경을 실패했습니다.");
                }

                // 물건 제공자 점수 및 레벨업
                ShaEvalDTO giverEval = new ShaEvalDTO();
                giverEval.setEveNo(i.getOwnerNo());
                giverEval.setRating(5);
                if (shareDAO.updateScore(sqlSession, giverEval) < 1) {
                    throw new Exception("나눔 제공인 점수 업데이트를 실패했습니다.");
                }
                ;
                System.out.println("=== 최종 점수 === ");
                System.out.println(giverEval);
                if (shareDAO.updateLevel(sqlSession, giverEval) < 1) {
                    throw new Exception("나눔 제공인 레벨 업데이트를 실패했습니다.");
                }
                ;
            }
        }

    }





}
