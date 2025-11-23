package com.elitea.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Test data model for conversations
 * Represents a conversation with metadata and messages
 */
public class TestConversation {
    private final String name;
    private final String model;
    private final List<TestMessage> messages;
    private final boolean withAgent;
    private final String agentName;
    private final String expectedUrl;

    private TestConversation(Builder builder) {
        this.name = builder.name;
        this.model = builder.model;
        this.messages = builder.messages;
        this.withAgent = builder.withAgent;
        this.agentName = builder.agentName;
        this.expectedUrl = builder.expectedUrl;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public List<TestMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public boolean hasAgent() {
        return withAgent;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getExpectedUrl() {
        return expectedUrl;
    }

    /**
     * Builder for creating TestConversation instances
     */
    public static class Builder {
        private String name = "Test Conversation";
        private String model = "Default";
        private List<TestMessage> messages = new ArrayList<>();
        private boolean withAgent = false;
        private String agentName = "";
        private String expectedUrl = "";

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder addMessage(TestMessage message) {
            this.messages.add(message);
            return this;
        }

        public Builder addMessage(String content) {
            this.messages.add(TestMessage.builder()
                    .content(content)
                    .build());
            return this;
        }

        public Builder withAgent(String agentName) {
            this.withAgent = true;
            this.agentName = agentName;
            return this;
        }

        public Builder expectedUrl(String url) {
            this.expectedUrl = url;
            return this;
        }

        public TestConversation build() {
            return new TestConversation(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "TestConversation{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", messages=" + messages.size() +
                ", withAgent=" + withAgent +
                '}';
    }
}
