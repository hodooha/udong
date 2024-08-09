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
    List<MessageDTO> getReceivedMessages(PageDTO pageDTO);

    List<MessageDTO> getSentMessages(PageDTO pageDTO);

    String getNicknameByMemberNo(int receiverNo);

    void sendMessage(MessageDTO messageDTO);

    int deleteMessages(List<Integer> messageNos);

    MessageDTO getMessageDetail(Integer messageNo);

    void updateMessageIsRead(Integer messageNo);
}
