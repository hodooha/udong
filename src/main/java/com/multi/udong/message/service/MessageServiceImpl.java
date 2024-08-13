package com.multi.udong.message.service;

import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dao.MessageMapper;
import com.multi.udong.message.model.dto.MessageBlockDTO;
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
     * @return boolean boolean
     * @since 2024 -08-07
     */
    @Override
    public boolean sendMessage(MessageDTO messageDTO) {

        Integer result = messageMapper.getMemberNoByNickname(messageDTO.getReceiverNickname());

        if (result != null) {
            messageDTO.setReceiverNo(result);

            boolean blocked = messageMapper.getIsBlocked(messageDTO);

            if (!blocked) {
                messageMapper.sendMessage(messageDTO);
            }

            return true;

        } else {
            return false;
        }
    }

    /**
     * Delete messages boolean.
     *
     * @param messageNos the message nos
     * @return the boolean
     * @since 2024 -08-08
     */
    @Override
    public boolean deleteMessages(List<Integer> messageNos) {

        return messageMapper.deleteMessages(messageNos) > 0;
    }


    /**
     * Block messages boolean.
     *
     * @param blockerNo  the blocker no
     * @param blockedNos the blocked nos
     * @return the boolean
     * @since 2024 -08-10
     */
    @Override
    public Boolean blockMessages(int blockerNo, List<Integer> blockedNos) {

        int result = 0;

        for (int blockedNo : blockedNos) {
            if (messageMapper.blockMessages(blockerNo, blockedNo) > 0) {
                result++;
            }
        }

        return result > 0;
    }

    /**
     * Unblock messages boolean.
     *
     * @param blockerNo  the blocker no
     * @param blockedNos the blocked nos
     * @return the boolean
     * @since 2024 -08-10
     */
    @Override
    public boolean unblockMessages(int blockerNo, List<Integer> blockedNos) {

        int result = 0;

        for (int blockedNo : blockedNos) {
            if (messageMapper.unblockMessages(blockerNo, blockedNo) > 0) {
                result++;
            }
        }

        return result > 0;
    }

    /**
     * Get message detail message dto.
     *
     * @param messageNo the message no
     * @return the message dto
     * @since 2024 -08-08
     */
    @Override
    public MessageDTO getReceivedMessageDetail(Integer messageNo) {

        messageMapper.updateMessageIsRead(messageNo);

        return messageMapper.getMessageDetail(messageNo);
    }

    /**
     * Get sent message detail message dto.
     *
     * @param messageNo the message no
     * @return the message dto
     * @since 2024 -08-10
     */
    @Override
    public MessageDTO getSentMessageDetail(Integer messageNo) {

        return messageMapper.getMessageDetail(messageNo);
    }

    /**
     * Is blocked boolean.
     *
     * @param messageNo the message no
     * @return the boolean
     * @since 2024 -08-10
     */
    @Override
    public boolean getIsBlocked(MessageDTO messageNo) {
        return messageMapper.getIsBlocked(messageNo);
    }

    @Override
    public List<MessageBlockDTO> getBlockList(PageDTO pageDTO) {
        return messageMapper.getBlockList(pageDTO);
    }

    /**
     * Is admin int.
     *
     * @param blockedNo the blocker no
     * @return the int
     * @since 2024 -08-10
     */
    @Override
    public String isAdmin(int blockedNo) {
        return messageMapper.isAdmin(blockedNo);
    }
}
