package com.example.demo.controller;

import com.example.demo.tool.KnowledgeSearchTool;
import com.example.demo.tool.ProductOrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@RestController
public class ApiController {
    @Autowired
    private ChatClient chatClient;

//    @Autowired
//    private ProductOrderTool productOrderTool;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Autowired
    private KnowledgeSearchTool knowledgeSearchTool;

    @Autowired
    private JsonMapper jsonMapper;

    @PostMapping(value="/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> postChats(@RequestBody String message,
                                  @RequestParam("conversationId") String conversationId,
                                  @RequestParam("username") String username) {
        return chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .system("""
                        마크다운 형식을 사용하지 말고 순수한 텍스트 형식으로 답 해 주세요.
                        --------
                        사용자가 페이지 생성/수정/삭제를 요청하면 Notion MCP Tool을 사용하여 작업하세요.
                        필수 정보가 충분하면 추가 확인 질문 없이 바로 실행하세요.
                        새 페이지 생성 시 사용자가 위치를 지정하지 않으면 항상 다음 parent page_id 아래에 생성하세요.
                        parent page_id: 3dc826bfae2180988309eca7693c3a80
                        """)
                .user(message)
                .tools(toolCallbackProvider, knowledgeSearchTool)
                .toolContext(Map.of("username", username))
                .stream().content().map(jsonMapper::writeValueAsString); // --> Flux<String>
    }
}
