package com.rakhshankhan.visualary.service;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.ChatMessageRole;
import com.cohere.api.types.NonStreamedChatResponse;
import com.rakhshankhan.visualary.model.dto.ChatDTO;
import com.rakhshankhan.visualary.model.dto.ChatInfoDTO;
import com.rakhshankhan.visualary.model.dto.ChatMessageDTO;
import com.rakhshankhan.visualary.model.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@EnableAsync
public class CohereService {

    private final Cohere cohere;

    private final ChatMessageRepository chatMessageRepository;

    private final ChatRepository chatRepository;

    private final UserRepository userRepository;

    public CohereService(
            @Value("${cohere.api.key}") String cohereApiKey,
            ChatMessageRepository chatMessageRepository,
            ChatRepository chatRepository,
            UserRepository userRepository) {
        this.cohere = Cohere.builder()
                .token(cohereApiKey)
                .clientName("snippet")
                .build();
        this.chatMessageRepository = chatMessageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public ChatDTO generateText(String prompt, Integer userId, Integer chatId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Chat chat = chatRepository.findByChatIdAndOwner(chatId, user.get());

        if (chat == null) {
            chat = new Chat();
            chat.setOwner(user.get());
            chat.setChatId(chatId);
            chatRepository.save(chat);
        }

        String processedPrompt = processPrompt(prompt);

        List<Message> messages = chatMessageRepository.findAllByChatIdOrderByTimestampAsc(chat.getId());

        List<ChatMessage> chatHistory = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            chatHistory = messages.stream()
                    .map(chatPrt -> ChatMessage.builder()
                            .role(chatPrt.getRole().equals("user") ? ChatMessageRole.USER : ChatMessageRole.CHATBOT)
                            .message(chatPrt.getContent())
                            .build())
                    .toList();
        }

        ChatRequest request = ChatRequest.builder()
                .message(processedPrompt)
                .model("command-r-plus")
                .chatHistory(chatHistory)
                .build();

        NonStreamedChatResponse response = cohere.chat(request);

        // Add user prompt and bot response to db
        chatMessageRepository.save(new Message("user", processedPrompt, prompt, LocalDateTime.now(), chat));

        String chatBotReply = response.getText();

        if (chatBotReply != null && !chatBotReply.isEmpty()) {
            chatMessageRepository.save(new Message("bot", chatBotReply, null, LocalDateTime.now(), chat));
        }

        // Retrieve chat history again to fix initial prompt and reply being added to history
        messages = chatMessageRepository.findAllByChatIdOrderByTimestampAsc(chat.getId());

        List<ChatMessageDTO> chatHistoryDTO = new ArrayList<>();

        if (messages != null && !messages.isEmpty()) {
            chatHistoryDTO = messages.stream()
                    .map(chatPrt -> new ChatMessageDTO(
                            chatPrt.getRole(),
                            chatPrt.getRole().equals("user") ? chatPrt.getUnprocessedContent() : chatPrt.getContent(),
                            chatPrt.getTimestamp()))
                    .toList();
        }

        return new ChatDTO(userId, chatBotReply, chatHistoryDTO);
    }

    public List<ChatInfoDTO> getAllUserChats(Integer userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Chat> chats = chatRepository.findByOwner(user.get());

        // Mapping Chat to ChatDTO
        return chats.stream().map(chat -> {
            List<Message> messages = chatMessageRepository.findAllByChatIdOrderByTimestampAsc(chat.getId());
            List<ChatMessageDTO> chatHistory = new ArrayList<>();

            if (messages != null && !messages.isEmpty()) {
                chatHistory = messages.stream()
                        .map(message -> new ChatMessageDTO(
                                message.getRole(),
                                message.getRole().equals("user") ? message.getUnprocessedContent() : message.getContent(),
                                message.getTimestamp()))
                        .toList();
            }

            return new ChatInfoDTO(userId, chat.getChatId(), chatHistory);
        }).toList();
    }

    public void deleteUserChatById(Integer userId, Integer chatId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Chat chat = chatRepository.findByChatIdAndOwner(chatId, user.get());

        if (chat != null) {
            chatRepository.delete(chat);
        } else {
            throw new RuntimeException("Chat does not exist.");
        }
    }

    public void deleteAllUserChats(Integer userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Chat> chats = chatRepository.findByOwner(user.get());

        if (chats != null) {
            chatRepository.deleteByOwner(user.get());
        } else {
            throw new RuntimeException("Chats do not exist.");
        }
    }

    public String processPrompt(String ogPrompt) {
        String modifyPrompt =
                "Strictly generate react JSX on the following requirements:\n" + ogPrompt.trim()
                        + ".\n\nStrictly adhere to the following guidelines:\n"
                        + "strictly ensure inline TailwindCSS is used for styling and absolutely no vanilla CSS,\n"
                        + "make sure to focus on the structure and minimal styling usage (only necessary),\n"
                        + "there is one parent element with everything else inside it,\n"
                        + "only use these tags where applicable: <p>, <h1> to <h6>, <div>, <span>, <header>, <li>, <ul>, <ol>\n"
                        + "strictly do not use these tags: <body>, <head>, <a>, <Link>, <script>, and NO react components,\n"
                        + "do NOT comment anything,\n"
                        + "ensure there is logic being done outside or inside the tags,\n"
                        + "ensure that the padding, margins, and any other sizing is done correctly.\n";

        System.out.println(modifyPrompt);

        return modifyPrompt;
    }
}