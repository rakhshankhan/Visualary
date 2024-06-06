package com.rakhshankhan.visualary.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatMessageDTO {

    private String role;
    private String content;
    private LocalDateTime timestamp;

}
