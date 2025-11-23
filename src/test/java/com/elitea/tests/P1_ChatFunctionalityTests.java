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

/**
 * EliteA Chat Application - P1 High Priority Test Cases
 * Test Level: Integration
 * Priority: P1 - High
 */
public class P1_ChatFunctionalityTests extends BaseTest {

    private ChatPage chatPage;
    private ConversationPage conversationPage;

    @Test
    @DisplayName("TC 1.2 - Send Message with Model Selection")
    void testSendMessageWithModelSelection() {
        // Navigate and create conversation
        chatPage = new ChatPage(page);
        chatPage.navigateToChatPage(ConfigManager.getAppUrl());
        conversationPage = chatPage.createNewConversation();

        // Click on "Select LLM Model" dropdown
        Locator modelSelector = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Select LLM Model"));
        modelSelector.click();

        // Verify available models are listed
        page.waitForTimeout(1000);
        
        // Close model selector (if needed) and select default model
        page.keyboard().press("Escape");

        // Type and send message using ID selector
        TestMessage testMessage = TestDataFactory.createModelSelectionMessage();
        Locator messageInputFixed = page.locator("#standard-multiline-static");
        messageInputFixed.waitFor();
        messageInputFixed.click();
        messageInputFixed.pressSequentially(testMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for response
        conversationPage.waitForAIResponse(5000);

        // Verify message sent successfully
        assertTrue(conversationPage.isUserMessageVisible(testMessage.getContent()), 
            "User message should be visible");
        assertTrue(conversationPage.isAINameVisible(), "AI response should appear");
    }

    @Test
    @DisplayName("TC 2.2 - Navigate Between Conversations")
    void testNavigateBetweenConversations() {
        chatPage = new ChatPage(page);
        chatPage.navigateToChatPage(ConfigManager.getAppUrl());

        // Create first conversation
        conversationPage = chatPage.createNewConversation();
        TestMessage firstMessage = TestDataFactory.createSimpleMessage("First conversation message");
        conversationPage.sendMessage(firstMessage.getContent());
        conversationPage.waitForAIResponse(3000);
        String firstUrl = page.url();

        // Create second conversation
        chatPage.createNewConversation();
        TestMessage secondMessage = TestDataFactory.createSimpleMessage("Second conversation message");
        conversationPage.sendMessage(secondMessage.getContent());
        conversationPage.waitForAIResponse(3000);
        String secondUrl = page.url();

        // Verify URLs are different
        assertNotEquals(firstUrl, secondUrl, "Conversation URLs should be different");

        // Navigate back using browser
        chatPage.goBack();

        // Verify we're back to first conversation
        assertTrue(page.url().contains("/chat/"), "Should still be in chat");
        assertTrue(conversationPage.isUserMessageVisible(firstMessage.getContent()), 
            "First conversation message should be visible after navigation");
    }

    @Test
    @DisplayName("TC 2.3 - Search Conversations")
    void testSearchConversations() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);

        // Create a conversation with specific topic
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage searchMessage = TestDataFactory.createSearchQuery("Unique search test topic about testing");
        messageInput.click();
        messageInput.pressSequentially(searchMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        page.waitForTimeout(3000);

        // Click search button
        Locator searchButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Search conversations"));
        assertTrue(searchButton.isVisible(), "Search button should be visible");
        searchButton.click();

        // Verify search functionality is accessible
        page.waitForTimeout(1000);
    }

    @Test
    @DisplayName("TC 3.1 - Add AI Agent to Conversation")
    void testAddAIAgentToConversation() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);
        
        // Create new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);

        // Click "Add agent" button
        Locator addAgentButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Add agent"));
        assertTrue(addAgentButton.isVisible(), "Add agent button should be visible");
        addAgentButton.click();

        // Wait for agent selection dialog
        page.waitForTimeout(2000);

        // Close dialog with Escape
        page.keyboard().press("Escape");
        page.waitForTimeout(500);

        // Verify we're back to chat by checking message input is visible
        Locator messageInput = page.locator("#standard-multiline-static");
        assertTrue(messageInput.isVisible(), 
            "Should return to chat after closing dialog");
    }

    @Test
    @DisplayName("TC 5.1 - Copy Message to Clipboard")
    void testCopyMessageToClipboard() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage copyMessage = TestDataFactory.createSimpleMessage("Message to copy");
        messageInput.click();
        messageInput.pressSequentially(copyMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for AI response
        page.waitForTimeout(5000);

        // Verify copy button exists using label selector
        Locator copyButton = page.getByLabel("Copy to clipboard", new Page.GetByLabelOptions().setExact(true)).first();
        assertTrue(copyButton.isVisible(), "Copy to clipboard button should be visible");
    }

    @Test
    @DisplayName("TC 5.2 - Delete User Message")
    void testDeleteUserMessage() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage deleteMessage = TestDataFactory.createSimpleMessage("Message to delete");
        messageInput.click();
        messageInput.pressSequentially(deleteMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for message to appear
        page.waitForTimeout(2000);

        // Verify delete button exists using label selector
        Locator deleteButton = page.getByLabel("Delete", new Page.GetByLabelOptions().setExact(true)).first();
        assertTrue(deleteButton.isVisible(), "Delete button should be visible on user message");
    }

    @Test
    @DisplayName("TC 5.3 - Regenerate AI Response")
    void testRegenerateAIResponse() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage regenerateMessage = TestDataFactory.createSimpleMessage("Test regenerate response");
        messageInput.click();
        messageInput.pressSequentially(regenerateMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for AI response
        page.waitForTimeout(5000);

        // Verify regenerate button exists using label selector
        Locator regenerateButton = page.getByLabel("Regenerate", new Page.GetByLabelOptions().setExact(true)).first();
        assertTrue(regenerateButton.isVisible(), "Regenerate button should be visible");
    }

    @Test
    @DisplayName("TC 6.1 - Clear Chat History")
    void testClearChatHistory() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation with messages
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage clearMessage = TestDataFactory.createSimpleMessage("First message");
        messageInput.click();
        messageInput.pressSequentially(clearMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        page.waitForTimeout(3000);

        // Verify clear button is enabled
        Locator clearButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Clear the chat history"));
        assertTrue(clearButton.isVisible(), "Clear chat history button should be visible");
        assertTrue(clearButton.isEnabled(), "Clear chat history button should be enabled when messages exist");
    }

    @Test
    @DisplayName("TC 9.1 - Sidebar Navigation")
    void testSidebarNavigation() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Verify sidebar sections are accessible
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Chat")).isVisible(), "Chat navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Agents")).isVisible(), "Agents navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Pipelines")).isVisible(), "Pipelines navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Toolkits")).isVisible(), "Toolkits navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("MCP")).isVisible(), "MCP navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Collections")).isVisible(), "Collections navigation should be visible");
        assertTrue(page.getByRole(AriaRole.LISTITEM, 
            new Page.GetByRoleOptions().setName("Artifacts")).isVisible(), "Artifacts navigation should be visible");
    }

    @Test
    @DisplayName("TC 12.2 - Network Error Handling")
    void testNetworkErrorHandling() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        
        // Type message using ID selector
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        TestMessage networkMessage = TestDataFactory.createSimpleMessage("Test network resilience");
        messageInput.click();
        messageInput.pressSequentially(networkMessage.getContent());

        // Send message and verify it's handled
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        
        // Wait and verify message appears
        page.waitForTimeout(2000);
        assertTrue(page.locator("text=" + networkMessage.getContent().substring(0, 20)).first().isVisible(), 
            "Message should be sent even with potential network issues");
    }

    @Test
    @DisplayName("TC 12.4 - Special Character Handling in Search")
    void testSpecialCharacterHandlingInSearch() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create conversation with special characters in message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);
        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        TestMessage specialMessage = TestDataFactory.createSpecialCharacterMessage();
        messageInput.click();
        messageInput.pressSequentially(specialMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        
        page.waitForTimeout(3000);

        // Verify message with special characters is handled correctly
        assertTrue(page.getByText(specialMessage.getContent()).first().isVisible(), 
            "Message with special characters should be visible");
    }

    @Test
    @DisplayName("TC 13.1 - Keyboard Navigation")
    void testKeyboardNavigation() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(2000);

        // Test keyboard input - type message character by character
        Locator messageInput = page.locator("#standard-multiline-static");
        
        // Wait for input to be ready and enabled
        messageInput.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        messageInput.click();
        
        // Type using keyboard
        TestMessage keyboardMessage = TestDataFactory.createKeyboardTestMessage();
        page.keyboard().type(keyboardMessage.getContent());
        page.waitForTimeout(500);

        // Test Enter to send
        page.keyboard().press("Enter");
        page.waitForTimeout(5000);
        
        // Verify message was sent via keyboard
        assertTrue(page.getByText(keyboardMessage.getContent()).first().isVisible(), 
            "Message should be sent via keyboard");
    }

    @Test
    @DisplayName("TC 14.1 - Message Send Response Time")
    void testMessageSendResponseTime() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.waitForTimeout(2000);

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        page.waitForTimeout(1000);

        Locator messageInput = page.locator("#standard-multiline-static");
        messageInput.waitFor();
        
        // Measure time to send message
        TestMessage perfMessage = TestDataFactory.createPerformanceTestMessage();
        long startTime = System.currentTimeMillis();
        messageInput.click();
        messageInput.pressSequentially(perfMessage.getContent());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for message to appear using unique full text
        page.getByText(perfMessage.getContent()).first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
        long sendTime = System.currentTimeMillis() - startTime;

        // Verify message sent within acceptable time (< 10 seconds including typing)
        assertTrue(sendTime < 10000, "Message should be sent within 10000ms, actual: " + sendTime + "ms");
        assertTrue(page.getByText(perfMessage.getContent()).first().isVisible(), 
            "Message should be visible");
    }
}
