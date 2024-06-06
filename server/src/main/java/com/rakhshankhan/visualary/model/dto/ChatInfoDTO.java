package com.rakhshankhan.visualary.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatInfoDTO {

    private Integer userId;
    private Integer chatId;
    private List<ChatMessageDTO> chatHistory;

}
