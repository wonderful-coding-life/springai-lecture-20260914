package com.example.demo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping("/chats")
    public String postChats(@RequestBody String message) {
        return chatClient.prompt()
                .user(message)
                .call().content();
    }
}
