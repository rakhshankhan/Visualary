package com.rakhshankhan.visualary.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "chats", uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "owner"}))
@Getter
@Setter
@NoArgsConstructor
public class Chat implements Serializable {

    @Serial
    private static final long serialVersionUID = 798341619724346934L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "chat_id", nullable = false)
    private Integer chatId;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

}
