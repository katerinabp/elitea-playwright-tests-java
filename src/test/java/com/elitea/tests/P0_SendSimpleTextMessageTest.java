package com.elitea.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * spec: docs/EliteA-Chat-Test-Plan-Updated.md
 * Test Level: Integration
 * Priority: P0 - Critical
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P0_SendSimpleTextMessageTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(500)
                .setArgs(java.util.Arrays.asList("--incognito")));
    }

    @BeforeEach
    void createContextAndPage() {
        // Create new context with no storage state (incognito mode)
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        // Clear all storage, cookies, and cache before closing
        if (context != null) {
            context.clearCookies();
            page.evaluate("() => { localStorage.clear(); sessionStorage.clear(); }");
            context.close();
        }
    }

    @AfterAll
    void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @Test
    @DisplayName("TC 1.1 - Send Simple Text Message")
    void testSendSimpleTextMessage() {
        // 1. Navigate to https://next.elitea.ai/alita_ui/chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create a new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // 2. Verify default greeting message is displayed
        assertTrue(page.getByText("Hello, Katerina!").isVisible(), "Greeting message should be visible");
        assertTrue(page.getByText("What can I do for you today?").isVisible(), "Welcome message should be visible");

        // 3. Click in the message input textbox
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.click();

        // 4. Type a simple message: "Hello, can you help me?"
        messageInput.fill("Hello, can you help me?");

        // 5. Verify send button becomes enabled
        Locator sendButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question"));
        assertTrue(sendButton.isEnabled(), "Send button should be enabled after typing message");

        // 6. Click the "send your question" button
        sendButton.click();

        // Expected Results:
        // - User message appears in chat history with user avatar and timestamp
        assertTrue(page.getByText("Hello, can you help me?").isVisible(), "User message should be visible");
        assertTrue(page.getByText("Katerina Pikulik").isVisible(), "User name should be visible");

        // - Message shows "less than a minute ago" timing
        assertTrue(page.getByText(java.util.regex.Pattern.compile("less than a minute ago|minute ago", 
            java.util.regex.Pattern.CASE_INSENSITIVE)).isVisible(), "Timestamp should be visible");

        // - AI response appears below user message with "Alita" avatar
        assertTrue(page.getByText("Alita").isVisible(), "Alita avatar should be visible");

        // Wait for AI response to complete
        page.waitForSelector("text=/Thought for \\\\d+ sec/i", new Page.WaitForSelectorOptions().setTimeout(10000));

        // - Response includes "Thought for X sec" expandable section
        assertTrue(page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Thought for \\\\d+ sec", 
            java.util.regex.Pattern.CASE_INSENSITIVE))).isVisible(), "Thought process should be visible");

        // - Message has copy, regenerate, and delete action buttons
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Copy to clipboard")).first().isVisible(), 
            "Copy button should be visible");
        assertTrue(page.locator("generic").filter(new Locator.FilterOptions().setHasText("Regenerate")).first().isVisible(), 
            "Regenerate button should be visible");
        assertTrue(page.locator("generic").filter(new Locator.FilterOptions().setHasText("Delete")).first().isVisible(), 
            "Delete button should be visible");

        // - Conversation is automatically named based on message content
        // Wait for conversation to be named
        page.waitForTimeout(2000);

        // - URL updates to include conversation ID and name
        String currentUrl = page.url();
        assertTrue(currentUrl.contains("/chat/"), "URL should contain /chat/");
        assertTrue(currentUrl.contains("name="), "URL should contain conversation name");
    }
}
