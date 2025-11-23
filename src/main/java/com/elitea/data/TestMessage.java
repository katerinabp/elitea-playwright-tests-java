package com.elitea.data;

/**
 * Test data model for chat messages
 * Provides builder pattern for flexible message creation
 */
public class TestMessage {
    private final String content;
    private final MessageType type;
    private final String context;
    private final boolean expectResponse;
    private final int expectedResponseTime;

    private TestMessage(Builder builder) {
        this.content = builder.content;
        this.type = builder.type;
        this.context = builder.context;
        this.expectResponse = builder.expectResponse;
        this.expectedResponseTime = builder.expectedResponseTime;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public String getContext() {
        return context;
    }

    public boolean shouldExpectResponse() {
        return expectResponse;
    }

    public int getExpectedResponseTime() {
        return expectedResponseTime;
    }

    /**
     * Message types for different test scenarios
     */
    public enum MessageType {
        SIMPLE_QUESTION("Simple question"),
        COMPLEX_QUERY("Complex query"),
        COMMAND("Command"),
        STORY_REQUEST("Story request"),
        CODE_REQUEST("Code request"),
        SEARCH_QUERY("Search query"),
        SPECIAL_CHARACTERS("Special characters"),
        PERFORMANCE_TEST("Performance test"),
        LONG_TEXT("Long text");

        private final String description;

        MessageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Builder for creating TestMessage instances
     */
    public static class Builder {
        private String content;
        private MessageType type = MessageType.SIMPLE_QUESTION;
        private String context = "";
        private boolean expectResponse = true;
        private int expectedResponseTime = 5000;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder type(MessageType type) {
            this.type = type;
            return this;
        }

        public Builder context(String context) {
            this.context = context;
            return this;
        }

        public Builder expectResponse(boolean expectResponse) {
            this.expectResponse = expectResponse;
            return this;
        }

        public Builder expectedResponseTime(int milliseconds) {
            this.expectedResponseTime = milliseconds;
            return this;
        }

        public TestMessage build() {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalStateException("Message content cannot be null or empty");
            }
            return new TestMessage(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TestMessage{" +
                "content='" + content + '\'' +
                ", type=" + type +
                ", expectResponse=" + expectResponse +
                '}';
    }
}
