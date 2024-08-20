package com.multi.udong.message.service;

import com.multi.udong.member.model.dto.PageDTO;
import com.multi.udong.message.model.dao.MessageMapper;
import com.multi.udong.message.model.dto.MessageBlockDTO;
import com.multi.udong.message.model.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;

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

                // 나에게 보내는 쪽지일경우 읽음상태 Y
                if (messageDTO.getSenderNo() == messageDTO.getReceiverNo()) {
                    messageDTO.setIsRead("Y");
                } else {
                    messageDTO.setIsRead("N");
                }

                messageMapper.sendMessage(messageDTO);

                // 나에게 보내는 쪽지가 아니면 알림 보냄
                if (messageDTO.getSenderNo() != messageDTO.getReceiverNo()){
                    MessageDTO insertedMessage = messageMapper.getInsertedMessage(messageDTO);
                    System.out.println("insertedMessage" + insertedMessage);
                    createMessageNoti(insertedMessage);
                }
            }

            return true;

        } else {
            return false;
        }
    }

    private void createMessageNoti(MessageDTO messageDTO) {
        simpMessagingTemplate.convertAndSend("/topic/message/" + messageDTO.getReceiverNo(), messageDTO);
    }

    /**
     * Delete messages boolean.
     *
     * @param messageNos the message nos
     * @return the boolean
     * @since 2024 -08-08
     */
    @Override
    public boolean deleteReceiveMessages(List<Integer> messageNos) {

        return messageMapper.deleteReceiveMessages(messageNos) > 0;
    }

    /**
     * Delete send messages boolean.
     *
     * @param messageNos the message nos
     * @return the boolean
     * @since 2024 -08-20
     */
    @Override
    public boolean deleteSendMessages(List<Integer> messageNos) {

        return messageMapper.deleteSendMessages(messageNos) > 0;
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

    /**
     * Get block list list.
     *
     * @param pageDTO the page dto
     * @return the list
     * @since 2024 -08-13
     */
    @Override
    public List<MessageBlockDTO> getBlockList(PageDTO pageDTO) {
        return messageMapper.getBlockList(pageDTO);
    }

    /**
     * Get message list.
     *
     * @param receiverNo the receiver no
     * @return the list
     * @since 2024 -08-13
     */
    @Override
    public List<MessageDTO> getMessage(int receiverNo) {
        return messageMapper.getMessage(receiverNo);
    }

    /**
     * Get unread message count int.
     *
     * @param receiverNo the receiver no
     * @return the int
     * @since 2024 -08-13
     */
    @Override
    public int getUnreadMessageCount(int receiverNo) {
        return messageMapper.getUnreadMessageCount(receiverNo);
    }

    /**
     * Mark all as read message boolean.
     *
     * @param receiverNo the receiver no
     * @return the boolean
     * @since 2024 -08-13
     */
    @Override
    public boolean markAllAsReadMessage(int receiverNo) {
        return messageMapper.markAllAsReadMessage(receiverNo) > 0;
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
