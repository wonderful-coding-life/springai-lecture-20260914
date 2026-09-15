package com.example.demo.controller;

import com.example.demo.tool.ProductOrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ProductOrderTool productOrderTool;

    @PostMapping("/chats")
    public String postChats(@RequestBody String message,
                            @RequestParam("conversationId") String conversationId,
                            @RequestParam("username") String username) {
        return chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .system("마크다운 형식을 사용하지 말고 순수한 텍스트 형식으로 답 해 주세요.")
                .user(message)
                .tools(productOrderTool)
                .toolContext(Map.of("username", username))
                .call().content();
    }
}
