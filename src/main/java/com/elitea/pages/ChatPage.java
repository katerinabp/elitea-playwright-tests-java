package com.elitea.pages;

import com.microsoft.playwright.Page;
import com.elitea.base.BasePage;

/**
 * Page Object for Chat Page
 */
public class ChatPage extends BasePage {
    
    // Locators
    private static final String CHAT_INPUT = "textarea[placeholder*='message']";
    private static final String SEND_BUTTON = "button[type='submit']";
    private static final String CHAT_MESSAGES = "[data-testid='message']";
    private static final String NEW_CHAT_BUTTON = "button:has-text('New chat')";
    private static final String USER_MENU = "[data-testid='user-menu']";
    
    // Expected URL pattern
    private static final String CHAT_URL_PATTERN = "**/chat**";
    
    public ChatPage(Page page) {
        super(page);
    }
    
    /**
     * Check if chat page is displayed
     */
    public boolean isChatPageDisplayed() {
        logStep("Verify chat page is displayed");
        waitForUrlMatches(CHAT_URL_PATTERN);
        return getCurrentUrl().contains("chat") && isElementVisible(CHAT_INPUT);
    }
    
    /**
     * Get page title
     */
    public String getChatPageTitle() {
        return page.title();
    }
    
    /**
     * Check if chat input is visible
     */
    public boolean isChatInputVisible() {
        return isElementVisible(CHAT_INPUT);
    }
    
    /**
     * Type message in chat input
     */
    public ChatPage typeMessage(String message) {
        logStep("Type message: " + message);
        fill(CHAT_INPUT, message);
        return this;
    }
    
    /**
     * Click send button
     */
    public ChatPage clickSendButton() {
        logStep("Click send button");
        click(SEND_BUTTON);
        return this;
    }
    
    /**
     * Send a chat message
     */
    public ChatPage sendMessage(String message) {
        logStep("Send chat message: " + message);
        typeMessage(message);
        clickSendButton();
        return this;
    }
    
    /**
     * Get number of chat messages
     */
    public int getMessagesCount() {
        int count = page.locator(CHAT_MESSAGES).count();
        logStep("Get messages count: " + count);
        return count;
    }
    
    /**
     * Start new chat
     */
    public ChatPage startNewChat() {
        logStep("Start new chat");
        if (isElementVisible(NEW_CHAT_BUTTON)) {
            click(NEW_CHAT_BUTTON);
        }
        return this;
    }
    
    /**
     * Check if user menu is visible
     */
    public boolean isUserMenuVisible() {
        return isElementVisible(USER_MENU);
    }
}
