package com.rakhshankhan.visualary.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {

    @Serial private static final long serialVersionUID = 5342294607419435190L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role")
    private String role;

    @Column(name = "content")
    private String content;

    @Column(name = "unprocessed_content")
    private String unprocessedContent;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    public Message(String role, String content, String unprocessedContent, LocalDateTime timestamp, Chat chat) {
        this.role = role;
        this.content = content;
        this.unprocessedContent = unprocessedContent;
        this.timestamp = timestamp;
        this.chat = chat;
    }
}
