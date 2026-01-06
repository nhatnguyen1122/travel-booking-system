package edu.hust.travelbookingsystem.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import edu.hust.travelbookingsystem.service.TravelAssistant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Path;

@Configuration
@Slf4j
public class ChatbotConfig {

    @Value("${mistral.api.key:Naeedw2S5s81zplpEPY6TbVSDPandcCp}")
    private String mistralApiKey;

    @Value("${mistral.model:mistral-small-latest}")
    private String mistralModel;

    private final ResourceLoader resourceLoader;

    public ChatbotConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return MistralAiChatModel.builder()
                .apiKey(mistralApiKey)
                .modelName(mistralModel)
                .temperature(0.7)
                .maxTokens(1000)
                .build();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore) {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    @Bean
    public EmbeddingStoreContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.6)
                .build();
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor(EmbeddingStoreContentRetriever contentRetriever) {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    public TravelAssistant travelAssistant(
            ChatLanguageModel chatLanguageModel,
            RetrievalAugmentor retrievalAugmentor,
            EmbeddingStoreIngestor ingestor) {

        // Load and ingest knowledge base
        try {
            Resource resource = resourceLoader.getResource("classpath:knowledge/travel-booking-knowledge.txt");
            if (resource.exists()) {
                Path path = resource.getFile().toPath();
                Document document = FileSystemDocumentLoader.loadDocument(path);
                ingestor.ingest(document);
                log.info("Successfully loaded knowledge base from: {}", path);
            } else {
                log.warn("Knowledge base file not found, chatbot will work without RAG context");
            }
        } catch (IOException e) {
            log.error("Error loading knowledge base: {}", e.getMessage());
        }

        return AiServices.builder(TravelAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }
}
