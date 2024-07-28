package com.multi.udong.club.service;

import com.multi.udong.club.model.dao.ClubDAO;
import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;
import com.multi.udong.club.model.dto.FilterDTO;
import com.multi.udong.club.model.dto.RequestDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 모임 service
 *
 * @author 강성현
 * @since 2024 -07-25
 */
@Transactional(rollbackFor = {Exception.class})
@Service
public class ClubServiceImpl implements ClubService {

    @Autowired
    private SqlSessionTemplate sqlSession;

    private final ClubDAO clubDAO;

    /**
     * 모임 service 생성자
     *
     * @param clubDAO the club dao
     */
    public ClubServiceImpl(ClubDAO clubDAO) {
        this.clubDAO = clubDAO;
    }


    /**
     * 모임의 주제(카테고리) 리스트 조회
     *
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public List<CategoryDTO> selectCategoryList() throws Exception {

        return clubDAO.selectCategoryList(sqlSession);

    }


    /**
     * 모임 생성
     *
     * @param clubDTO the club dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public int insertClub(ClubDTO clubDTO) throws Exception {

        int result = 0;

        // 모임 데이터 insert
        int clubResult = clubDAO.insertClub(sqlSession, clubDTO);

        // insert한 모임no를 targetNo에 set한 후 모임 이미지 insert
        int clubNo = clubDTO.getClubNo();
        AttachmentDTO attachmentDTO = clubDTO.getAttachment().get(0);
        attachmentDTO.setTargetNo(clubNo);
        int attachmentResult = clubDAO.insertClubImg(sqlSession, attachmentDTO);

        if(clubResult <= 0 || attachmentResult <= 0) {

            throw new Exception("모임 생성에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

        } else {

            // clubNo를 붙인 채팅방 code를 만들어 다시 clubDTO에 set
            String chatroomCode = "chatroom" + clubNo;
            clubDTO.setChatroomCode(chatroomCode);

            // 비어있던 chatroom 컬럼에 code를 update
            int chatroomResult = clubDAO.updateChatroomCode(sqlSession, clubDTO);

            // 모임 멤버 테이블에 모임 생성자를 insert
            int masterResult = clubDAO.insertMaster(sqlSession, clubDTO);

            if(chatroomResult <= 0 || masterResult <= 0) {

                throw new Exception("모임 생성 후 챗룸 및 모임장 insert에 실패했습니다. 트랜잭션이 롤백을 실행합니다.");

            } else {

                result = 1;

            }

        }

        return result;

    }


    /**
     * 모임 리스트 조회
     *
     * @param filterDTO the filter dto
     * @return the list
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public List<ClubDTO> selectClubList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubList(sqlSession, filterDTO);

    }


    /**
     * 모임의 개수 조회
     *
     * @param filterDTO the filter dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public int selectClubCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubCount(sqlSession, filterDTO);

    }


    /**
     * 모임 홈 조회
     *
     * @param requestDTO the request dto
     * @return the club dto
     * @throws Exception the exception
     * @since 2024 -07-25
     */
    @Override
    public ClubDTO selectClubHome(RequestDTO requestDTO) throws Exception {

        return clubDAO.selectClubHome(sqlSession, requestDTO);

    }


    /**
     * 모임 가입 신청
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public int requestJoinClub(RequestDTO requestDTO) throws Exception {

        return clubDAO.requestJoinClub(sqlSession, requestDTO);

    }


    /**
     * 모임 가입 상태 확인
     *
     * @param requestDTO the request dto
     * @return the string
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public String checkJoinStatus(RequestDTO requestDTO) throws Exception {

        return clubDAO.checkJoinStatus(sqlSession, requestDTO);

    }


    /**
     * 모임 인원 현황 확인
     *
     * @param clubNo the club no
     * @return the club dto
     * @throws Exception the exception
     * @since 2024 -07-27
     */
    @Override
    public ClubDTO checkPersonnel(int clubNo) throws Exception {

        return clubDAO.checkPersonnel(sqlSession, clubNo);

    }


    /**
     * 모임장 확인
     *
     * @param clubNo the club no
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int checkClubMaster(int clubNo) throws Exception {

        return clubDAO.checkClubMaster(sqlSession, clubNo);

    }


    /**
     * 모임 가입 신청 취소
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int cancelJoinRequest(RequestDTO requestDTO) throws Exception {

        return clubDAO.cancelJoinRequest(sqlSession, requestDTO);

    }


    /**
     * 모임 탈퇴
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int leaveClub(RequestDTO requestDTO) throws Exception {

        return clubDAO.leaveClub(sqlSession, requestDTO);

    }


    /**
     * 모임 해체
     *
     * @param requestDTO the request dto
     * @return the int
     * @throws Exception the exception
     * @since 2024 -07-28
     */
    @Override
    public int deleteClub(RequestDTO requestDTO) throws Exception {

        return clubDAO.deleteClub(sqlSession, requestDTO);

    }

}
