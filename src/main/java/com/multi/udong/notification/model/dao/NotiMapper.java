package com.multi.udong.notification.model.dao;

import com.multi.udong.notification.model.dto.NotiDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotiMapper {

    void insertNoti(NotiDTO notiDTO);

    List<NotiDTO> getUnreadNoti(Integer receiverNo);

    void markAsRead(Integer notiNo);
}
