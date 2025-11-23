package com.elitea.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory for generating unique test data
 * Ensures test data uniqueness to prevent conflicts and flaky tests
 * 
 * Usage:
 * <pre>
 *   TestMessage message = TestDataFactory.createSimpleMessage();
 *   TestMessage customMessage = TestDataFactory.messageBuilder()
 *       .withType(TestMessage.MessageType.COMPLEX_QUERY)
 *       .build();
 * </pre>
 */
public class TestDataFactory {

    private static final AtomicInteger messageCounter = new AtomicInteger(0);
    private static final AtomicInteger conversationCounter = new AtomicInteger(0);
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Generate unique timestamp for test data
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(timestampFormat);
    }

    /**
     * Generate unique ID for messages
     */
    private static int getNextMessageId() {
        return messageCounter.incrementAndGet();
    }

    /**
     * Generate unique ID for conversations
     */
    private static int getNextConversationId() {
        return conversationCounter.incrementAndGet();
    }

    // ==================== MESSAGE FACTORIES ====================

    /**
     * Create a simple question message with unique content
     */
    public static TestMessage createSimpleMessage() {
        return TestMessage.builder()
                .content("Hello, can you help me? [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SIMPLE_QUESTION)
                .expectResponse(true)
                .expectedResponseTime(5000)
                .build();
    }

    /**
     * Create a custom simple message with specified content
     */
    public static TestMessage createSimpleMessage(String baseContent) {
        return TestMessage.builder()
                .content(baseContent + " [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SIMPLE_QUESTION)
                .build();
    }

    /**
     * Create a story request message
     */
    public static TestMessage createStoryRequest() {
        return TestMessage.builder()
                .content("Tell me a short story about testing [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.STORY_REQUEST)
                .expectResponse(true)
                .expectedResponseTime(10000)
                .build();
    }

    /**
     * Create a complex query message
     */
    public static TestMessage createComplexQuery() {
        return TestMessage.builder()
                .content("Explain the benefits of test automation in software development [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.COMPLEX_QUERY)
                .expectResponse(true)
                .expectedResponseTime(15000)
                .build();
    }

    /**
     * Create a code request message
     */
    public static TestMessage createCodeRequest() {
        return TestMessage.builder()
                .content("Write a Java function to reverse a string [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.CODE_REQUEST)
                .expectResponse(true)
                .expectedResponseTime(12000)
                .build();
    }

    /**
     * Create a search query message
     */
    public static TestMessage createSearchQuery(String topic) {
        return TestMessage.builder()
                .content("Search: " + topic + " [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SEARCH_QUERY)
                .expectResponse(true)
                .build();
    }

    /**
     * Create a message with special characters
     */
    public static TestMessage createSpecialCharacterMessage() {
        return TestMessage.builder()
                .content("Test @#$%^&* special chars [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SPECIAL_CHARACTERS)
                .expectResponse(true)
                .build();
    }

    /**
     * Create a performance test message (short, quick response)
     */
    public static TestMessage createPerformanceTestMessage() {
        return TestMessage.builder()
                .content("Performance test message [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.PERFORMANCE_TEST)
                .expectResponse(true)
                .expectedResponseTime(3000)
                .build();
    }

    /**
     * Create a long text message
     */
    public static TestMessage createLongTextMessage() {
        StringBuilder longText = new StringBuilder("This is a long message for testing: ");
        for (int i = 0; i < 20; i++) {
            longText.append("Testing is important for software quality. ");
        }
        longText.append("[").append(getTimestamp()).append("_").append(getNextMessageId()).append("]");
        
        return TestMessage.builder()
                .content(longText.toString())
                .type(TestMessage.MessageType.LONG_TEXT)
                .expectResponse(true)
                .expectedResponseTime(20000)
                .build();
    }

    /**
     * Create a message for model selection testing
     */
    public static TestMessage createModelSelectionMessage() {
        return TestMessage.builder()
                .content("Test with model selection [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SIMPLE_QUESTION)
                .context("Model selection test")
                .build();
    }

    /**
     * Create message for keyboard input testing
     */
    public static TestMessage createKeyboardTestMessage() {
        return TestMessage.builder()
                .content("Keyboard test [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(TestMessage.MessageType.SIMPLE_QUESTION)
                .context("Keyboard navigation test")
                .build();
    }

    /**
     * Create message with specific content and type
     */
    public static TestMessage createMessage(String content, TestMessage.MessageType type) {
        return TestMessage.builder()
                .content(content + " [" + getTimestamp() + "_" + getNextMessageId() + "]")
                .type(type)
                .build();
    }

    // ==================== CONVERSATION FACTORIES ====================

    /**
     * Create a simple conversation with one message
     */
    public static TestConversation createSimpleConversation() {
        return TestConversation.builder()
                .name("Test Conversation " + getTimestamp() + "_" + getNextConversationId())
                .addMessage(createSimpleMessage())
                .build();
    }

    /**
     * Create a conversation with multiple messages
     */
    public static TestConversation createMultiMessageConversation() {
        return TestConversation.builder()
                .name("Multi-Message Test " + getTimestamp() + "_" + getNextConversationId())
                .addMessage(createSimpleMessage("First message"))
                .addMessage(createSimpleMessage("Second message"))
                .addMessage(createSimpleMessage("Third message"))
                .build();
    }

    /**
     * Create a conversation with specific model
     */
    public static TestConversation createConversationWithModel(String model) {
        return TestConversation.builder()
                .name("Model Test " + getTimestamp() + "_" + getNextConversationId())
                .model(model)
                .addMessage(createModelSelectionMessage())
                .build();
    }

    /**
     * Create a conversation with AI agent
     */
    public static TestConversation createConversationWithAgent(String agentName) {
        return TestConversation.builder()
                .name("Agent Test " + getTimestamp() + "_" + getNextConversationId())
                .withAgent(agentName)
                .addMessage(createSimpleMessage("Message for agent"))
                .build();
    }

    /**
     * Create a searchable conversation (for search tests)
     */
    public static TestConversation createSearchableConversation(String uniqueTopic) {
        return TestConversation.builder()
                .name("Search Test " + getTimestamp() + "_" + getNextConversationId())
                .addMessage(createSearchQuery(uniqueTopic))
                .build();
    }

    /**
     * Create a performance testing conversation
     */
    public static TestConversation createPerformanceConversation() {
        return TestConversation.builder()
                .name("Performance " + getTimestamp() + "_" + getNextConversationId())
                .addMessage(createPerformanceTestMessage())
                .build();
    }

    // ==================== BUILDER ACCESSORS ====================

    /**
     * Get a message builder for custom message creation
     */
    public static TestMessage.Builder messageBuilder() {
        return TestMessage.builder();
    }

    /**
     * Get a conversation builder for custom conversation creation
     */
    public static TestConversation.Builder conversationBuilder() {
        return TestConversation.builder();
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Reset counters (useful for test isolation in certain scenarios)
     * Generally not needed as timestamps provide uniqueness
     */
    public static void resetCounters() {
        messageCounter.set(0);
        conversationCounter.set(0);
    }

    /**
     * Get current message count (for debugging)
     */
    public static int getMessageCount() {
        return messageCounter.get();
    }

    /**
     * Get current conversation count (for debugging)
     */
    public static int getConversationCount() {
        return conversationCounter.get();
    }
}
