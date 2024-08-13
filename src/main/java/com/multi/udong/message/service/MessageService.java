package com.multi.udong.message.service;

import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dto.MessageBlockDTO;
import com.multi.udong.message.model.dto.MessageDTO;

import java.util.List;

/**
 * The interface Message service.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
public interface MessageService {
    List<MessageDTO> getReceivedMessages(PageDTO pageDTO);

    List<MessageDTO> getSentMessages(PageDTO pageDTO);

    String getNicknameByMemberNo(int receiverNo);

    boolean sendMessage(MessageDTO messageDTO);

    boolean deleteMessages(List<Integer> messageNos);

    Boolean blockMessages(int blockerNo, List<Integer> messageNos);

    boolean unblockMessages(int blockerNo, List<Integer> blockedNos);

    String isAdmin(int blockedNo);

    MessageDTO getReceivedMessageDetail(Integer messageNo);

    MessageDTO getSentMessageDetail(Integer messageNo);

    boolean getIsBlocked(MessageDTO messageNo);

    List<MessageBlockDTO> getBlockList(PageDTO pageDTO);
}
