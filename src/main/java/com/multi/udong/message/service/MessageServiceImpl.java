package com.multi.udong.message.service;

import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dao.MessageMapper;
import com.multi.udong.message.model.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Message service.
 *
 * @author 김재식
 * @since 2024 -08-06
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageMapper messageMapper;

    /**
     * Select received messages list.
     *
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -08-06
     */
    @Override
    public List<MessageDTO> getReceivedMessages(PageDTO pageDTO) {

        return messageMapper.getReceivedMessages(pageDTO);
    }

    /**
     * Get sent messages list.
     *
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -08-07
     */
    @Override
    public List<MessageDTO> getSentMessages(PageDTO pageDTO) {

        return messageMapper.getSentMessages(pageDTO);
    }

    /**
     * Get nickname by member no string.
     *
     * @param receiverNo the receiver no
     * @return the string
     * @since 2024 -08-07
     */
    @Override
    public String getNicknameByMemberNo(int receiverNo) {

        return messageMapper.getNicknameByMemberNo(receiverNo);
    }

    /**
     * Send message.
     *
     * @param messageDTO the message dto
     * @since 2024 -08-07
     */
    @Override
    public void sendMessage(MessageDTO messageDTO) {

        messageMapper.sendMessage(messageDTO);
    }

    @Override
    public boolean deleteMessages(List<Integer> messageNos) {

        return messageMapper.deleteMessages(messageNos) > 0;
    }
}
