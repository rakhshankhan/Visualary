package com.rakhshankhan.visualary.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatDTO {

    private String latestReply;
    private List<ChatPromptDTO> chatHistoryDTO;

}
