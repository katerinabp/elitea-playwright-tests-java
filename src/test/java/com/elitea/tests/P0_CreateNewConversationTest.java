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
public class P0_CreateNewConversationTest {
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
    @DisplayName("TC 2.1 - Create New Conversation")
    void testCreateNewConversation() {
        // Navigate to chat
        page.navigate("https://next.elitea.ai/alita_ui/chat");

        // Get initial URL to compare later
        String initialUrl = page.url();

        // 1. From any chat page, click "Create Conversation" button in sidebar
        Locator createConversationButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Create Conversation"));
        createConversationButton.click();

        // 2. Verify new conversation is created
        // - New conversation appears with default greeting
        assertTrue(page.getByText("Hello, Katerina!").isVisible(), "Greeting message should be visible");
        assertTrue(page.getByText("What can I do for you today?").isVisible(), "Welcome message should be visible");

        // - Conversation shows "Naming" status initially
        assertTrue(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Naming")).isVisible(), 
            "Naming status should be visible");

        // - URL changes to new conversation ID
        String newUrl = page.url();
        assertNotEquals(initialUrl, newUrl, "URL should change after creating conversation");
        assertTrue(newUrl.contains("/chat"), "URL should contain /chat");

        // Send a message to trigger auto-naming
        Locator messageInput = page.getByRole(AriaRole.TEXTBOX, 
            new Page.GetByRoleOptions().setName(java.util.regex.Pattern.compile("Type your message", 
            java.util.regex.Pattern.CASE_INSENSITIVE)));
        messageInput.fill("This is a test conversation");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("send your question")).click();

        // Wait for response
        page.waitForTimeout(3000);

        // 3. Check conversations list
        // - After first message, conversation is auto-named
        Locator conversationsList = page.locator(".MuiBox-root").filter(
            new Locator.FilterOptions().setHasText("Conversations"));
        assertTrue(conversationsList.isVisible(), "Conversations list should be visible");

        // - Conversation appears under "Today" section in sidebar
        assertTrue(page.getByRole(AriaRole.HEADING, 
            new Page.GetByRoleOptions().setName("Today").setLevel(6)).isVisible(), 
            "Today section should be visible");

        // Verify we can create another conversation
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create Conversation")).click();
        assertTrue(page.getByText("Hello, Katerina!").isVisible(), "New conversation should show greeting");

        // Verify previous conversation is still in the list
        assertTrue(page.getByRole(AriaRole.HEADING, 
            new Page.GetByRoleOptions().setName("Today").setLevel(6)).isVisible(), 
            "Today section should still be visible with previous conversations");
    }
}
