package com.elitea.tests;

import com.elitea.base.BaseTest;
import com.elitea.pages.ChatPage;
import com.elitea.pages.ConversationPage;
import com.elitea.config.ConfigManager;
import com.elitea.data.TestDataFactory;
import com.elitea.data.TestMessage;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * spec: docs/EliteA-Chat-Test-Plan-Updated.md
 * Test Level: Integration
 * Priority: P0 - Critical
 */
public class P0_SendSimpleTextMessageTest extends BaseTest {

    private ChatPage chatPage;
    private ConversationPage conversationPage;

    @Test
    @DisplayName("TC 1.1 - Send Simple Text Message")
    void testSendSimpleTextMessage() {
        // 1. Navigate to chat page
        chatPage = new ChatPage(page);
        chatPage.navigateToChatPage(ConfigManager.getAppUrl());

        // 2. Create a new conversation
        conversationPage = chatPage.createNewConversation();

        // 3. Verify default greeting and welcome messages are displayed
        assertTrue(conversationPage.isGreetingDisplayed(), 
            "Greeting message should be visible");
        assertTrue(conversationPage.isWelcomeMessageDisplayed(), 
            "Welcome message should be visible");

        // 4. Type a message (typeMessage handles clicking the input)
        TestMessage testMessage = TestDataFactory.createSimpleMessage();
        conversationPage.typeMessage(testMessage.getContent());

        // 5. Verify send button becomes enabled
        assertTrue(conversationPage.isSendButtonEnabled(), 
            "Send button should be enabled after typing message");

        // 6. Click the send button
        conversationPage.clickSendButton();

        // Expected Results Verification:
        
        // - User message appears in chat history with user avatar and timestamp
        assertTrue(conversationPage.isUserMessageVisible(testMessage.getContent()), 
            "User message should be visible");
        assertTrue(conversationPage.isUserNameVisible(), 
            "User name should be visible");
        assertTrue(conversationPage.isTimestampVisible(), 
            "Timestamp should be visible");

        // - AI response appears below user message with "Alita" avatar
        assertTrue(conversationPage.isAINameVisible(), 
            "Alita avatar should be visible");

        // - Wait for AI response to complete
        conversationPage.waitForAIResponse(2000);

        // - Response may include "Thought for X sec" expandable section (timing-dependent)
        if (conversationPage.isThoughtProcessVisible()) {
            System.out.println("[INFO] Thought process section is visible");
        } else {
            System.out.println("[INFO] Thought process section not visible (fast response or model-dependent)");
        }

        // - Message has copy, regenerate, and delete action buttons
        assertTrue(conversationPage.isCopyButtonVisible(), 
            "Copy button should be visible");
        assertTrue(conversationPage.isRegenerateButtonVisible(), 
            "Regenerate button should be visible");
        assertTrue(conversationPage.isDeleteButtonVisible(), 
            "Delete button should be visible");

        // - URL updates to include conversation ID and name
        conversationPage.waitForAIResponse(2000);
        assertTrue(conversationPage.hasConversationIdInUrl(), 
            "URL should contain conversation ID and name");
    }
}
