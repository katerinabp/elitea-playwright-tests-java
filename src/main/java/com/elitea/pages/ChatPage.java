package com.elitea.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.elitea.base.BasePage;
import com.elitea.utils.WaitUtils;
import com.elitea.utils.RetryUtils;
import com.elitea.utils.SelectorUtils;
import com.elitea.constants.SelectorConstants;

/**
 * Page Object for Chat Page
 * Handles main chat interface and conversation management
 * Enhanced with smart wait strategies and centralized selectors
 */
public class ChatPage extends BasePage {
    
    public ChatPage(Page page) {
        super(page);
    }
    
    /**
     * Navigate to chat page
     */
    public ChatPage navigateToChatPage(String url) {
        logStep("Navigate to chat page: " + url);
        navigateTo(url);
        // Wait for Create Conversation button to be visible (indicates page fully loaded with auth)
        // Page may redirect and needs time to initialize socket connections
        try {
            SelectorUtils.getButtonByName(page, SelectorConstants.ChatPage.CREATE_CONVERSATION_BUTTON)
                .waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(45000));
        } catch (Exception e) {
            // If button not found, might not be logged in - check URL
            logStep("Create Conversation button not found. Current URL: " + page.url());
            if (page.url().contains("auth")) {
                throw new RuntimeException("Not logged in - redirected to auth page. Run: ./gradlew authSetup");
            }
            throw e;
        }
        return this;
    }
    
    /**
     * Check if chat page is displayed
     */
    public boolean isChatPageDisplayed() {
        logStep("Verify chat page is displayed");
        waitForUrlMatches(SelectorConstants.ChatPage.CHAT_URL_PATTERN);
        return getCurrentUrl().contains("chat");
    }
    
    /**
     * Get page title
     */
    public String getChatPageTitle() {
        return getPageTitle();
    }
    
    /**
     * Create a new conversation
     */
    public ConversationPage createNewConversation() {
        logStep("Create new conversation");
        SelectorUtils.getButtonByName(page, 
            SelectorConstants.ChatPage.CREATE_CONVERSATION_BUTTON).click();
        page.waitForTimeout(1000);
        return new ConversationPage(page);
    }
    
    /**
     * Check if create conversation button is visible
     */
    public boolean isCreateConversationButtonVisible() {
        logStep("Check if create conversation button is visible");
        return SelectorUtils.getButtonByName(page, 
            SelectorConstants.ChatPage.CREATE_CONVERSATION_BUTTON).isVisible();
    }
    
    /**
     * Check if user profile button is visible
     */
    public boolean isUserProfileVisible() {
        logStep("Check if user profile button is visible");
        try {
            page.waitForTimeout(1000);
            return SelectorUtils.getButtonByName(page, 
                SelectorConstants.ChatPage.USER_PROFILE_BUTTON).isVisible();
        } catch (Exception e) {
            logStep("Error checking user profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Open search functionality
     */
    public ChatPage openSearch() {
        logStep("Open search");
        SelectorUtils.getButtonByNameIgnoreCase(page, 
            SelectorConstants.ChatPage.SEARCH_BUTTON).click();
        return this;
    }
    
    /**
     * Type in search field
     */
    public ChatPage searchConversations(String searchTerm) {
        logStep("Search conversations: " + searchTerm);
        Locator searchInput = SelectorUtils.getTextboxByNamePattern(page,
            SelectorUtils.caseInsensitive(SelectorConstants.ChatPage.SEARCH_INPUT));
        searchInput.fill(searchTerm);
        return this;
    }
    
    /**
     * Get conversations list
     */
    public Locator getConversationsList() {
        return SelectorUtils.getByCssSelector(page, 
            SelectorConstants.ChatPage.CONVERSATIONS_LIST).first();
    }
    
    /**
     * Select conversation by name
     */
    public ConversationPage selectConversationByName(String conversationName) {
        logStep("Select conversation: " + conversationName);
        SelectorUtils.getByTextFirst(page, conversationName).click();
        page.waitForTimeout(1000);
        return new ConversationPage(page);
    }
    
    /**
     * Check if conversation exists in list
     */
    public boolean isConversationVisible(String conversationName) {
        logStep("Check if conversation exists: " + conversationName);
        return SelectorUtils.getByTextFirst(page, conversationName).isVisible();
    }
    
    /**
     * Navigate to chat using browser back button
     */
    public ChatPage goBack() {
        logStep("Navigate back");
        page.goBack();
        waitForPageReady();
        return this;
    }
}
