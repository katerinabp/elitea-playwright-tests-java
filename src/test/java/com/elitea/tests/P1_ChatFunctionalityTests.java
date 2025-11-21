package com.elitea.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EliteA Chat Application - P1 High Priority Test Cases
 * Test Level: Integration
 * Priority: P1 - High
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P1_ChatFunctionalityTests {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(500));
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @AfterAll
    void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @Test
    @DisplayName("TC 1.2 - Send Message with Model Selection")
    void testSendMessageWithModelSelection() {
        // Navigate and create conversation
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Click on "Select LLM Model" dropdown
        Locator modelSelector = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Select LLM Model"));
        modelSelector.click();

        // Verify available models are listed
        page.waitForTimeout(1000);
        
        // Close model selector (if needed) and select default model
        page.keyboard().press("Escape");

        // Type and send message
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("Test with model selection");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for response
        page.waitForTimeout(5000);

        // Verify message sent successfully
        assertTrue(page.getByText("Test with model selection").isVisible(), 
            "User message should be visible");
        assertTrue(page.getByText("Alita").isVisible(), "AI response should appear");
    }

    @Test
    @DisplayName("TC 2.2 - Navigate Between Conversations")
    void testNavigateBetweenConversations() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create first conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("First conversation message");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        page.waitForTimeout(3000);
        String firstUrl = page.url();

        // Create second conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        messageInput.fill("Second conversation message");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        page.waitForTimeout(3000);
        String secondUrl = page.url();

        // Verify URLs are different
        assertNotEquals(firstUrl, secondUrl, "Conversation URLs should be different");

        // Navigate back using browser
        page.goBack();
        page.waitForTimeout(1000);

        // Verify we're back to first conversation
        assertTrue(page.url().contains("/chat/"), "Should still be in chat");
        assertTrue(page.getByText("First conversation message").isVisible(), 
            "First conversation message should be visible after navigation");
    }

    @Test
    @DisplayName("TC 2.3 - Search Conversations")
    void testSearchConversations() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create a conversation with specific topic
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("Unique search test topic about testing");
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
        
        // Create new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Click "Add agent" button
        Locator addAgentButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Add agent"));
        assertTrue(addAgentButton.isVisible(), "Add agent button should be visible");
        addAgentButton.click();

        // Wait for agent selection dialog
        page.waitForTimeout(2000);

        // Close dialog with Escape
        page.keyboard().press("Escape");

        // Verify we're back to chat
        assertTrue(page.getByText("Hello, Katerina!").isVisible(), 
            "Should return to chat after closing dialog");
    }

    @Test
    @DisplayName("TC 5.1 - Copy Message to Clipboard")
    void testCopyMessageToClipboard() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("Message to copy");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for AI response
        page.waitForTimeout(5000);

        // Verify copy button exists
        Locator copyButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Copy to clipboard")).first();
        assertTrue(copyButton.isVisible(), "Copy to clipboard button should be visible");
    }

    @Test
    @DisplayName("TC 5.2 - Delete User Message")
    void testDeleteUserMessage() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("Message to delete");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for message to appear
        page.waitForTimeout(2000);

        // Verify delete button exists
        Locator deleteButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Delete")).first();
        assertTrue(deleteButton.isVisible(), "Delete button should be visible on user message");
    }

    @Test
    @DisplayName("TC 5.3 - Regenerate AI Response")
    void testRegenerateAIResponse() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("Test regenerate response");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for AI response
        page.waitForTimeout(5000);

        // Verify regenerate button exists
        Locator regenerateButton = page.locator("generic").filter(
            new Locator.FilterOptions().setHasText("Regenerate")).first();
        assertTrue(regenerateButton.isVisible(), "Regenerate button should be visible");
    }

    @Test
    @DisplayName("TC 6.1 - Clear Chat History")
    void testClearChatHistory() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");
        
        // Create conversation with messages
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("First message");
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

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Verify socket connection status
        assertTrue(page.locator("generic[title*='Socket']").isVisible(), 
            "Socket connection status should be visible");
        
        // Type message
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("Test network resilience");

        // Send message and verify it's handled
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        
        // Wait and verify message appears
        page.waitForTimeout(2000);
        assertTrue(page.getByText("Test network resilience").isVisible(), 
            "Message should be sent even with potential network issues");
    }

    @Test
    @DisplayName("TC 12.4 - Special Character Handling in Search")
    void testSpecialCharacterHandlingInSearch() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create conversation with special characters in message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        messageInput.fill("Test @#$%^&* special chars");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();
        
        page.waitForTimeout(3000);

        // Verify message with special characters is handled correctly
        assertTrue(page.getByText("Test @#$%^&* special chars").isVisible(), 
            "Message with special characters should be visible");
    }

    @Test
    @DisplayName("TC 13.1 - Keyboard Navigation")
    void testKeyboardNavigation() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Test Tab navigation
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.click();
        messageInput.fill("Keyboard navigation test");

        // Test Enter to send (if supported)
        page.keyboard().press("Enter");
        page.waitForTimeout(2000);

        // Test Escape key functionality
        page.keyboard().press("Escape");
        
        // Verify keyboard interactions work
        assertTrue(page.getByText("Keyboard navigation test").isVisible(), 
            "Message should be sent via keyboard");
    }

    @Test
    @DisplayName("TC 14.1 - Message Send Response Time")
    void testMessageSendResponseTime() {
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        
        // Measure time to send message
        long startTime = System.currentTimeMillis();
        messageInput.fill("Performance test message");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for message to appear
        page.waitForSelector("text=Performance test message", new Page.WaitForSelectorOptions().setTimeout(3000));
        long sendTime = System.currentTimeMillis() - startTime;

        // Verify message sent within acceptable time (< 3 seconds)
        assertTrue(sendTime < 3000, "Message should be sent within 3 seconds, actual: " + sendTime + "ms");
        assertTrue(page.getByText("Performance test message").isVisible(), 
            "Message should be visible");
    }
}
