package com.example.demo.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class KnowledgeSearchTool {
    @Autowired
    private VectorStore vectorStore;

    @Tool(description = """
            반품 가능 조건, 반품 기간, 배송비, 환불 절차 등
            반품 FAQ와 반품 정책 매뉴얼에서 정보를 검색합니다.
            반품 관련 질문에는 반드시 호출합니다.
            """)
    public String searchReturnKnowledgeBase(@ToolParam(description = "반품 문서 검색에 적합하게 재작성된 질문") String query) {
        log.info("재작성된 질문: {}", query);

        var documents = vectorStore.similaritySearch(SearchRequest.builder()
                        .query(query)
                        .similarityThreshold(0.7)
                        .topK(3)
                .build());

        if (documents.isEmpty()) {
            return "관련 매뉴얼 및 FAQ에서 내용을 찾지 못 하였습니다.";
        }

        return documents.stream().map(Document::getText).collect(Collectors.joining("\n\n--------\n\n"));
    }
}
