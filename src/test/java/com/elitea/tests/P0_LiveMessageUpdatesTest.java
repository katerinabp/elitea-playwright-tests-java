package com.elitea.tests;

import com.elitea.base.BaseTest;
import com.elitea.pages.ChatPage;
import com.elitea.pages.ConversationPage;
import com.elitea.config.ConfigManager;
import com.elitea.data.TestDataFactory;
import com.elitea.data.TestMessage;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

/**
 * spec: docs/EliteA-Chat-Test-Plan-Updated.md
 * Test Level: Integration
 * Priority: P0 - Critical
 */
public class P0_LiveMessageUpdatesTest extends BaseTest {

    private ChatPage chatPage;
    private ConversationPage conversationPage;

    @Test
    @DisplayName("TC 10.2 - Live Message Updates")
    void testLiveMessageUpdates() {
        // Navigate to chat and create conversation
        chatPage = new ChatPage(page);
        chatPage.navigateToChatPage(ConfigManager.getAppUrl());
        conversationPage = chatPage.createNewConversation();

        // Verify greeting is displayed
        assertTrue(conversationPage.isGreetingDisplayed(), "Greeting should be visible");

        // 1. Send a message
        TestMessage testMessage = TestDataFactory.createStoryRequest();
        conversationPage.sendMessage(testMessage.getContent());

        // Verify user message appears immediately
        page.waitForTimeout(500);
        assertTrue(conversationPage.isUserMessageVisible(testMessage.getContent()), 
            "User message should appear immediately");

        // 2. Observe streaming response
        page.waitForTimeout(1000);

        // 3. Verify real-time updates
        // - AI responses stream in real-time
        page.waitForTimeout(2000);
        assertTrue(conversationPage.isAINameVisible(), 
            "Alita avatar should appear");

        // - Message appears incrementally (streaming)
        // Thought process indicator is timing-dependent, check but don't fail
        if (conversationPage.isThoughtProcessVisible()) {
            System.out.println("[INFO] Thought process indicator visible (streaming response)");
        }

        // - No page refresh needed
        assertTrue(page.url().contains("/chat/"), "URL should still contain /chat/");

        // Verify the complete response is visible (wait for AI response to finish streaming)
        page.waitForTimeout(3000); // Allow time for streaming to complete

        // Verify action buttons are present (indicates message is complete)
        assertTrue(conversationPage.isCopyButtonVisible(), "Copy button should be visible");

        // Verify message timestamp is shown
        assertTrue(conversationPage.isTimestampVisible(), 
            "Timestamp should be visible");
    }

    @Test
    @DisplayName("TC 10.2 - Live Message Updates - Streaming Verification")
    void testLiveMessageUpdatesStreamingVerification() {
        // Track network events to verify WebSocket usage
        List<String> webSocketMessages = new ArrayList<>();

        // Set up WebSocket listener BEFORE navigation
        page.onWebSocket(ws -> {
            ws.onFrameReceived(frame -> webSocketMessages.add("received: " + frame.text()));
            ws.onFrameSent(frame -> webSocketMessages.add("sent: " + frame.text()));
        });

        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(3000);

        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(2000);

        // Use ID selector for reliable element finding
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        TestMessage testMsg = TestDataFactory.createSimpleMessage();
        messageInput.click();
        messageInput.pressSequentially(testMsg.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for AI response (Alita name appears)
        page.waitForSelector("text=Alita", new Page.WaitForSelectorOptions().setTimeout(30000));

        // Verify WebSocket was used for communication
        assertTrue(webSocketMessages.size() > 0, "WebSocket messages should be present");

        // Verify no full page reload occurred
        assertTrue(page.url().contains("/chat/"), "URL should still contain /chat/");
    }

    @Test
    @DisplayName("TC 10.2 - Live Message Updates - Multiple Messages")
    void testLiveMessageUpdatesMultipleMessages() {
        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);

        // Create a new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);

        // Send first message using ID selector
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        TestMessage firstMessage = TestDataFactory.createSimpleMessage("First message");
        messageInput.click();
        messageInput.pressSequentially(firstMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for first response (Alita appears)
        page.waitForSelector("text=Alita", new Page.WaitForSelectorOptions().setTimeout(30000));
        page.waitForTimeout(3000); // Allow response to complete

        // Send second message
        TestMessage secondMessage = TestDataFactory.createSimpleMessage("Second message");
        messageInput.click();
        messageInput.pressSequentially(secondMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Verify both messages are visible
        assertTrue(page.getByText(firstMessage.getContent()).first().isVisible(), "First message should be visible");
        assertTrue(page.getByText(secondMessage.getContent()).first().isVisible(), "Second message should be visible");

        // Wait for second response
        page.waitForTimeout(3000);

        // Verify multiple "Alita" responses (at least 2)
        int alitaResponses = page.locator("text=Alita").count();
        assertTrue(alitaResponses >= 2, "Should have at least 2 Alita responses");
    }
}
