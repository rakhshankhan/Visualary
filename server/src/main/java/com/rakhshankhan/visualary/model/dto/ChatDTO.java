package com.rakhshankhan.visualary.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatDTO {

    private Integer userId;
    private String latestReply;
    private List<ChatMessageDTO> chatHistory;

}
