package com.elitea.tests;

import com.elitea.base.BaseTest;
import com.elitea.pages.ChatPage;
import com.elitea.pages.ConversationPage;
import com.elitea.config.ConfigManager;
import com.elitea.data.TestDataFactory;
import com.elitea.data.TestMessage;
import com.elitea.utils.WaitUtils;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * spec: docs/EliteA-Chat-Test-Plan-Updated.md
 * Test Level: Integration
 * Priority: P0 - Critical
 * 
 * TC 2.1 - Create New Conversation
 * Verified flow using Playwright MCP tools
 */
public class P0_CreateNewConversationTest extends BaseTest {
    private ChatPage chatPage;
    private ConversationPage conversationPage;

    @Test
    @DisplayName("TC 2.1 - Create New Conversation")
    void testCreateNewConversation() {
        // Step 1: Navigate to chat page
        chatPage = new ChatPage(page);
        chatPage.navigateToChatPage(ConfigManager.getAppUrl());
        
        // Get initial URL to compare later
        String initialUrl = page.url();

        // Step 2: Click "Create Conversation" button in sidebar
        conversationPage = chatPage.createNewConversation();
        
        // Step 3: Verify new conversation is created with default greeting
        assertTrue(conversationPage.isGreetingDisplayed(), 
            "Greeting 'Hello, Katerina!' should be visible");
        assertTrue(conversationPage.isWelcomeMessageDisplayed(), 
            "Welcome message 'What can I do for you today?' should be visible");

        // Step 4: Verify "Naming" status initially shown
        assertTrue(conversationPage.isNamingStatusVisible(), 
            "Naming status should be visible initially");

        // Step 5: Verify URL changes to new conversation
        String newUrl = page.url();
        assertNotEquals(initialUrl, newUrl, "URL should change after creating conversation");
        assertTrue(newUrl.contains("/chat"), "URL should contain /chat");

        // Step 6: Type a message (using pressSequentially via typeMessage)
        TestMessage testMessage = TestDataFactory.createSimpleMessage();
        conversationPage.typeMessage(testMessage.getContent());

        // Step 7: Verify send button becomes enabled after typing
        assertTrue(conversationPage.isSendButtonEnabled(), 
            "Send button should be enabled after typing message");

        // Step 8: Click send button
        conversationPage.clickSendButton();

        // Step 9: Verify user message appears in chat history
        WaitUtils.waitForTextToAppear(page, testMessage.getContent());
        assertTrue(conversationPage.isMessageInHistory(testMessage.getContent()), 
            "User message should appear in chat history");
        
        // Step 10: Verify message timestamp
        assertTrue(conversationPage.hasMessageWithTimestamp("less than a minute ago"), 
            "Message should have 'less than a minute ago' timestamp");

        // Step 11: Wait for AI response
        WaitUtils.waitForTextToAppear(page, "Alita");
        
        // Step 12: Verify AI response appears (thought section is optional, depends on model/timing)
        if (conversationPage.isThoughtSectionVisible()) {
            System.out.println("[INFO] Thought section is visible");
        } else {
            System.out.println("[INFO] Thought section not visible (may be fast response or model-dependent)");
        }
        assertTrue(conversationPage.hasAIResponse(), 
            "AI response text should be visible");

        // Step 13: Verify message action buttons are present
        assertTrue(conversationPage.isCopyButtonVisible(), 
            "Copy to clipboard button should be visible");
        assertTrue(conversationPage.isRegenerateButtonVisible(), 
            "Regenerate button should be visible");
        assertTrue(conversationPage.isDeleteButtonVisible(), 
            "Delete button should be visible");

        // Step 14: Verify conversation is auto-named
        WaitUtils.waitUntil(() -> !page.url().equals(newUrl), 5000,
            "URL should update with conversation name");
        String finalUrl = page.url();
        assertTrue(finalUrl.contains("name="), 
            "URL should contain conversation name parameter");

        // Step 15: Verify conversation appears in "Today" section
        assertTrue(conversationPage.isTodaySectionVisible(), 
            "Today section should be visible in conversations list");
        
        // Step 16: Verify we can create another conversation
        chatPage.createNewConversation();
        assertTrue(conversationPage.isGreetingDisplayed(), 
            "New conversation should show greeting message");

        // Step 17: Verify previous conversation remains in history
        assertTrue(conversationPage.isTodaySectionVisible(), 
            "Today section should still show previous conversations");
    }
}
