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
 * Page Object for Conversation Page
 * Handles all interactions within a specific conversation
 * Enhanced with smart wait strategies and centralized selectors
 */
public class ConversationPage extends BasePage {
    
    public ConversationPage(Page page) {
        super(page);
    }
    
    /**
     * Wait for conversation to load completely
     */
    public ConversationPage waitForConversationLoad() {
        logStep("Wait for conversation to load");
        waitForPageReady();
        return this;
    }
    
    /**
     * Verify greeting message is displayed
     */
    public boolean isGreetingDisplayed() {
        logStep("Check if greeting message is displayed");
        return RetryUtils.retry(() -> 
            SelectorUtils.getByTextFirst(page, 
                SelectorConstants.ConversationPage.GREETING_MESSAGE).isVisible(),
            "Check greeting visibility", 
            RetryUtils.config().maxAttempts(3).initialDelay(1000));
    }
    
    /**
     * Verify welcome message is displayed
     */
    public boolean isWelcomeMessageDisplayed() {
        logStep("Check if welcome message is displayed");
        return SelectorUtils.getByTextFirst(page, 
            SelectorConstants.ConversationPage.WELCOME_MESSAGE).isVisible();
    }
    
    /**
     * Get message input field
     * Uses placeholder attribute which is more reliable than accessible name
     */
    private Locator getMessageInput() {
        // Using ID selector (most reliable) - element id="standard-multiline-static"
        return page.locator("#standard-multiline-static");
    }
    
    /**
     * Click in message input field
     */
    public ConversationPage clickMessageInput() {
        logStep("Click message input field");
        getMessageInput().click();
        return this;
    }
    
    /**
     * Type message in input field
     * Uses Playwright's pressSequentially() method which simulates actual keyboard typing
     * This is required because the UI listens for keyboard events to enable the send button
     */
    public ConversationPage typeMessage(String message) {
        logStep("Type message: " + message);
        Locator input = getMessageInput();
        // Wait for input to be stable and visible
        input.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(10000));
        // Click to focus the input field first
        input.click();
        // Use pressSequentially() to simulate actual keyboard input (triggers keydown/keyup events)
        input.pressSequentially(message);
        return this;
    }
    
    /**
     * Get send button
     */
    private Locator getSendButton() {
        return SelectorUtils.getButtonByName(page, 
            SelectorConstants.ConversationPage.SEND_BUTTON);
    }
    
    /**
     * Check if send button is enabled
     */
    public boolean isSendButtonEnabled() {
        logStep("Check if send button is enabled");
        return getSendButton().isEnabled();
    }
    
    /**
     * Click send button with smart wait
     */
    public ConversationPage clickSendButton() {
        logStep("Click send button");
        Locator sendButton = getSendButton();
        WaitUtils.waitForElementClickable(sendButton, DEFAULT_TIMEOUT);
        clickWithWait(sendButton);
        waitForAjaxComplete(); // Wait for message to be sent
        return this;
    }
    
    /**
     * Send a message (type and click send) with smart waits
     */
    public ConversationPage sendMessage(String message) {
        logStep("Send message: " + message);
        RetryUtils.retryVoid(() -> {
            typeMessage(message);
            clickSendButton();
        }, "Send message: " + message);
        return this;
    }
    
    /**
     * Check if user message is visible
     */
    public boolean isUserMessageVisible(String message) {
        logStep("Check if user message is visible: " + message);
        return SelectorUtils.getByTextFirst(page, message).isVisible();
    }
    
    /**
     * Check if user name is visible in message
     */
    public boolean isUserNameVisible() {
        logStep("Check if user name is visible");
        return SelectorUtils.getByTextFirst(page, 
            SelectorConstants.ConversationPage.USER_NAME).isVisible();
    }
    
    /**
     * Check if AI name (Alita) is visible
     */
    public boolean isAINameVisible() {
        logStep("Check if AI name is visible");
        return SelectorUtils.getByTextFirst(page, 
            SelectorConstants.ConversationPage.AI_NAME).isVisible();
    }
    
    /**
     * Check if timestamp is visible
     */
    public boolean isTimestampVisible() {
        logStep("Check if timestamp is visible");
        return SelectorUtils.getByTextPattern(page, 
            SelectorConstants.ConversationPage.TIMESTAMP_PATTERN).first().isVisible();
    }
    
    /**
     * Wait for AI response thought process indicator
     */
    public ConversationPage waitForThoughtProcess(int timeoutMs) {
        logStep("Wait for AI thought process indicator");
        WaitUtils.waitForTextToAppear(page, "Thought for", timeoutMs);
        return this;
    }
    
    /**
     * Wait for AI response thought process indicator with default timeout
     */
    public ConversationPage waitForThoughtProcess() {
        return waitForThoughtProcess(30000);
    }
    
    /**
     * Check if thought process indicator is visible
     */
    public boolean isThoughtProcessVisible() {
        logStep("Check if thought process indicator is visible");
        return RetryUtils.retry(() -> 
            SelectorUtils.getByRoleAndNamePattern(page, AriaRole.BUTTON,
                SelectorConstants.ConversationPage.THOUGHT_PROCESS_PATTERN).isVisible(),
            "Check thought process visibility",
            RetryUtils.config().maxAttempts(2).initialDelay(500));
    }
    
    /**
     * Check if copy button is visible
     */
    public boolean isCopyButtonVisible() {
        logStep("Check if copy button is visible");
        return SelectorUtils.getButtonByName(page, 
            SelectorConstants.ConversationPage.COPY_BUTTON).first().isVisible();
    }
    
    /**
     * Check if regenerate button is visible
     */
    public boolean isRegenerateButtonVisible() {
        logStep("Check if regenerate button is visible");
        try {
            Locator regenerateBtn = page.getByLabel(SelectorConstants.ConversationPage.REGENERATE_BUTTON, 
                new Page.GetByLabelOptions().setExact(true)).first();
            regenerateBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return regenerateBtn.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Click regenerate button
     */
    public ConversationPage clickRegenerateButton() {
        logStep("Click regenerate button");
        page.getByLabel(SelectorConstants.ConversationPage.REGENERATE_BUTTON, 
            new Page.GetByLabelOptions().setExact(true)).first().click();
        return this;
    }
    
    /**
     * Check if delete button is visible
     */
    public boolean isDeleteButtonVisible() {
        logStep("Check if delete button is visible");
        try {
            Locator deleteBtn = page.getByLabel(SelectorConstants.ConversationPage.DELETE_BUTTON, 
                new Page.GetByLabelOptions().setExact(true)).first();
            deleteBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return deleteBtn.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Click delete button
     */
    public ConversationPage clickDeleteButton() {
        logStep("Click delete button");
        SelectorUtils.filterByText(
            SelectorUtils.getByCssSelector(page, 
                SelectorConstants.ConversationPage.ACTION_BUTTONS),
            SelectorConstants.ConversationPage.DELETE_BUTTON).first().click();
        return this;
    }
    
    /**
     * Check if conversation URL contains conversation ID
     */
    public boolean hasConversationIdInUrl() {
        String url = getCurrentUrl();
        logStep("Check if URL contains conversation ID: " + url);
        return url.contains("/chat/") && url.contains("name=");
    }
    
    /**
     * Get conversation ID from URL
     */
    public String getConversationIdFromUrl() {
        String url = getCurrentUrl();
        logStep("Extract conversation ID from URL: " + url);
        
        if (url.contains("/chat/")) {
            String[] parts = url.split("/chat/");
            if (parts.length > 1) {
                String idPart = parts[1].split("\\?")[0];
                return idPart;
            }
        }
        return "";
    }
    
    /**
     * Wait for AI response to complete with smart wait
     */
    public ConversationPage waitForAIResponse(int timeoutMs) {
        logStep("Wait for AI response to complete");
        WaitUtils.waitForAjaxComplete(page);
        page.waitForTimeout(Math.min(timeoutMs, 2000)); // Max 2s additional wait
        return this;
    }
    
    /**
     * Wait for AI response with default timeout
     */
    public ConversationPage waitForAIResponse() {
        return waitForAIResponse(2000);
    }
    
    /**
     * Click copy button
     */
    public ConversationPage clickCopyButton() {
        logStep("Click copy button");
        SelectorUtils.getButtonByName(page, 
            SelectorConstants.ConversationPage.COPY_BUTTON).first().click();
        return this;
    }
    
    /**
     * Check if "Naming" status is visible
     */
    public boolean isNamingStatusVisible() {
        logStep("Check if naming status is visible");
        return SelectorUtils.getButtonByName(page, "Naming").isVisible();
    }
    
    /**
     * Check if message exists in chat history
     */
    public boolean isMessageInHistory(String message) {
        logStep("Check if message is in chat history: " + message);
        return isUserMessageVisible(message);
    }
    
    /**
     * Check if message with specific timestamp exists
     */
    public boolean hasMessageWithTimestamp(String timestamp) {
        logStep("Check if message has timestamp: " + timestamp);
        return SelectorUtils.getByTextFirst(page, timestamp).isVisible();
    }
    
    /**
     * Get latest AI response locator
     */
    public Locator getLatestAIResponse() {
        logStep("Get latest AI response");
        return SelectorUtils.getByTextFirst(page, SelectorConstants.ConversationPage.AI_NAME);
    }
    
    /**
     * Check if thought section is visible (alias for isThoughtProcessVisible)
     */
    public boolean isThoughtSectionVisible() {
        return isThoughtProcessVisible();
    }
    
    /**
     * Check if AI response text is visible
     */
    public boolean hasAIResponse() {
        logStep("Check if AI response is visible");
        return isAINameVisible();
    }
    
    /**
     * Check if "Today" section is visible in conversations list
     */
    public boolean isTodaySectionVisible() {
        logStep("Check if Today section is visible");
        return page.getByRole(AriaRole.HEADING, 
            new Page.GetByRoleOptions().setName("Today").setLevel(6)).isVisible();
    }
}
