package com.multi.udong.notification.model.dao;

import com.multi.udong.notification.model.dto.NotiDTO;
import com.multi.udong.notification.model.dto.NotiSetDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * The interface Noti mapper.
 *
 * @author 김재식
 * @since 2024 -08-13
 */
@Mapper
public interface NotiMapper {

    void insertNoti(List<NotiDTO> notiDTO);

    List<NotiDTO> getNoti(Integer receiverNo);

    int markAsRead(@Param("receiverNo") Integer receiverNo, @Param("notiNo") Integer notiNo);

    int markAllAsRead(Integer receiverNo);

    int deleteAllReadNoti(Integer receiverNo);

    int getUnreadNotiCount(int receiverNo);

    List<NotiSetDTO> getNotiSetByMemberNo(int memberNo);

    Integer getNotiNoByReceiverNo(NotiDTO notiDTO);
}
