package com.rakhshankhan.visualary.service;

import com.cohere.api.Cohere;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.types.ChatMessage;
import com.cohere.api.types.ChatMessageRole;
import com.cohere.api.types.NonStreamedChatResponse;
import com.rakhshankhan.visualary.model.dto.ChatDTO;
import com.rakhshankhan.visualary.model.dto.ChatPromptDTO;
import com.rakhshankhan.visualary.model.entity.ChatPrompt;
import com.rakhshankhan.visualary.model.entity.ChatPromptRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableAsync
public class CohereService {

    private final Cohere cohere;

    private final ChatPromptRepository chatPromptRepository;

    public CohereService(@Value("${cohere.api.key}") String cohereApiKey, ChatPromptRepository chatPromptRepository) {
        this.cohere = Cohere.builder()
                .token(cohereApiKey)
                .clientName("snippet")
                .build();
        this.chatPromptRepository = chatPromptRepository;
    }

    public ChatDTO generateText(String prompt) {
        String processedPrompt = processPrompt(prompt);

        List<ChatPrompt> chatPrompts = chatPromptRepository.findAllByOrderByTimestampAsc();

        List<ChatMessage> chatHistory = new ArrayList<>();

        if (chatPrompts != null && !chatPrompts.isEmpty()) {
            chatHistory = chatPrompts.stream()
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
        chatPromptRepository.save(new ChatPrompt("user", processedPrompt, LocalDateTime.now()));

        String chatBotReply = response.getText();

        if (chatBotReply != null && !chatBotReply.isEmpty()) {
            chatPromptRepository.save(new ChatPrompt("bot", chatBotReply, LocalDateTime.now()));
        }

        // Retrieve chat history again to fix initial prompt and reply being added to history
        chatPrompts = chatPromptRepository.findAllByOrderByTimestampAsc();

        List<ChatPromptDTO> chatHistoryDTO = new ArrayList<>();

        if (chatPrompts != null && !chatPrompts.isEmpty()) {
            chatHistoryDTO = chatPrompts.stream()
                    .map(chatPrt -> new ChatPromptDTO(chatPrt.getRole(),
                            chatPrt.getRole().equals("user") ? prompt : chatPrt.getContent(),
                            chatPrt.getTimestamp()))
                    .toList();
        }

        return new ChatDTO(chatBotReply, chatHistoryDTO);
    }

    public void clearChatHistory() {
        chatPromptRepository.deleteAll();
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
