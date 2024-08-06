package com.multi.udong.message.model.dao;

import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dto.MessageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * The interface Message mapper.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
@Mapper
public interface MessageMapper {
    List<MessageDTO> selectReceivedMessages(PageDTO pageDTO);
}
