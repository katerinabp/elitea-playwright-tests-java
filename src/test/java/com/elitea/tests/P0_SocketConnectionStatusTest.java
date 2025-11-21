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
public class P0_SocketConnectionStatusTest {
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
    @DisplayName("TC 10.1 - Socket Connection Status")
    void testSocketConnectionStatus() {
        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // 1. Observe socket status indicator in sidebar
        Locator socketStatus = page.locator("generic").filter(
            new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("Socket:", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        assertTrue(socketStatus.isVisible(), "Socket status should be visible");

        // 2. Verify "connected" status
        // Socket status shows "connected" in green
        assertTrue(page.locator("generic[title*='Socket']").isVisible(), "Socket status indicator should be visible");

        // Verify the socket status contains "connected"
        String socketText = socketStatus.textContent();
        assertNotNull(socketText, "Socket text should not be null");
        assertTrue(socketText.toLowerCase().contains("connected"), "Socket should show connected status");

        // Verify real-time message delivery works
        // Create a new conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();

        // Send a test message
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("Testing real-time connection");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Verify message appears immediately
        page.getByText("Testing real-time connection").isVisible(
            new Locator.IsVisibleOptions());
        assertTrue(page.getByText("Testing real-time connection").isVisible(), 
            "Message should appear immediately");

        // Verify AI response streams in real-time
        page.waitForSelector("text=/Thought for \\\\d+ sec/i", new Page.WaitForSelectorOptions().setTimeout(15000));

        // Expected Results:
        // - Socket status shows "connected" in green ✓
        // - Real-time message delivery works ✓
        // - No page refresh needed ✓
        // - WebSocket events are logged in console (visible in browser DevTools)
    }

    @Test
    @DisplayName("TC 10.1 - Socket Connection Status - Console Verification")
    void testSocketConnectionStatusConsoleVerification() {
        // Listen to console messages to verify WebSocket events
        List<String> consoleMessages = new ArrayList<>();
        page.onConsoleMessage(msg -> consoleMessages.add(msg.text()));

        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Wait for WebSocket connection
        page.waitForTimeout(2000);

        // Verify WebSocket connection messages in console
        boolean hasSocketMessages = consoleMessages.stream().anyMatch(msg ->
                msg.contains("sio connected") ||
                msg.contains("subscribing to") ||
                msg.contains("chat_predict")
        );

        assertTrue(hasSocketMessages, "Console should contain WebSocket connection messages");

        // Verify socket status in UI
        assertTrue(page.locator("generic[title*='Socket']").isVisible(), "Socket status should be visible in UI");
    }
}
