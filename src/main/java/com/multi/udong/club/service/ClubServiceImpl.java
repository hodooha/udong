package com.multi.udong.club.service;

import com.multi.udong.club.model.dao.ClubDAO;
import com.multi.udong.club.model.dto.CategoryDTO;
import com.multi.udong.club.model.dto.ClubDTO;
import com.multi.udong.club.model.dto.FilterDTO;
import com.multi.udong.common.model.dto.AttachmentDTO;
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

    @Override
    public int insertClub(ClubDTO clubDTO) throws Exception {

        int result = 0;

        // 모임 데이터 insert
        int clubResult = clubDAO.insertClub(sqlSession, clubDTO);

        // insert한 모임no를 targetNo에 set한 후 모임 이미지 insert
        int clubNo = clubDTO.getClubNo();
        AttachmentDTO attachmentDTO = clubDTO.getAttachment();
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

    @Override
    public List<ClubDTO> selectClubList(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubList(sqlSession, filterDTO);

    }

    @Override
    public int selectClubCount(FilterDTO filterDTO) throws Exception {

        return clubDAO.selectClubCount(sqlSession, filterDTO);

    }

}
