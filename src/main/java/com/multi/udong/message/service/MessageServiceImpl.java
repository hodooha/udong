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
    public List<MessageDTO> selectReceivedMessages(PageDTO pageDTO) {

        return messageMapper.selectReceivedMessages(pageDTO);
    }
}
