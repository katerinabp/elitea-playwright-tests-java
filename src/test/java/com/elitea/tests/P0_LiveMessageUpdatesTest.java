package com.elitea.tests;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class P0_LiveMessageUpdatesTest {
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
    @DisplayName("TC 10.2 - Live Message Updates")
    void testLiveMessageUpdates() {
        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create a new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Wait for conversation to load
        assertTrue(page.getByText("Hello, Katerina!").isVisible(), "Greeting should be visible");

        // 1. Send a message
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("Tell me a short story");

        // Click send button
        Locator sendButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question"));
        sendButton.click();

        // Verify user message appears immediately
        page.waitForTimeout(500);
        assertTrue(page.getByText("Tell me a short story").isVisible(), 
            "User message should appear immediately");

        // 2. Observe streaming response
        // Wait for AI response to start appearing
        page.waitForTimeout(1000);

        // 3. Verify real-time updates
        // - AI responses stream in real-time
        // Look for the "Alita" avatar indicating AI response
        page.waitForTimeout(2000);
        assertTrue(page.getByText("Alita").isVisible(), 
            "Alita avatar should appear");

        // - Message appears incrementally (streaming)
        // Wait for thought process indicator
        page.waitForSelector("text=/Thought for \\\\d+ sec/i", new Page.WaitForSelectorOptions().setTimeout(15000));
        assertTrue(page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Thought for \\\\d+ sec", 
            java.util.regex.Pattern.CASE_INSENSITIVE))).isVisible(), 
            "Thought process indicator should be visible");

        // - No page refresh needed
        // Verify URL hasn't changed (no full page reload)
        assertTrue(page.url().contains("/chat/"), "URL should still contain /chat/");

        // Verify the complete response is visible
        page.waitForSelector("generic:has-text('Copy to clipboard')", new Page.WaitForSelectorOptions().setTimeout(5000));

        // Verify action buttons are present (indicates message is complete)
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Copy to clipboard"))
            .first().isVisible(), "Copy button should be visible");

        // Verify message timestamp is shown
        assertTrue(page.getByText(java.util.regex.Pattern.compile("less than a minute ago|minute ago", 
            java.util.regex.Pattern.CASE_INSENSITIVE)).isVisible(), 
            "Timestamp should be visible");
    }

    @Test
    @DisplayName("TC 10.2 - Live Message Updates - Streaming Verification")
    void testLiveMessageUpdatesStreamingVerification() {
        // Track network events to verify WebSocket usage
        List<String> webSocketMessages = new ArrayList<>();

        page.onWebSocket(ws -> {
            ws.onFrameReceived(frame -> webSocketMessages.add("received: " + frame.text()));
            ws.onFrameSent(frame -> webSocketMessages.add("sent: " + frame.text()));
        });

        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Create conversation and send message
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("Hello");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for response
        page.waitForSelector("text=/Thought for \\\\d+ sec/i", new Page.WaitForSelectorOptions().setTimeout(15000));

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

        // Create a new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Send first message
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("First message");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for first response
        page.waitForSelector("text=/Thought for \\\\d+ sec/i", new Page.WaitForSelectorOptions().setTimeout(15000));

        // Send second message
        messageInput.fill("Second message");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Verify both messages are visible
        assertTrue(page.getByText("First message").isVisible(), "First message should be visible");
        assertTrue(page.getByText("Second message").isVisible(), "Second message should be visible");

        // Wait for second response
        page.waitForTimeout(3000);

        // Verify multiple "Alita" responses (at least 2)
        int alitaResponses = page.locator("text=Alita").count();
        assertTrue(alitaResponses >= 2, "Should have at least 2 Alita responses");

        // Verify real-time updates continue to work
        assertTrue(page.locator("generic[title*='Socket']").isVisible(), "Socket status should still be visible");
    }
}
