package com.elitea.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.elitea.config.ConfigManager;
import com.elitea.utils.WaitUtils;
import com.elitea.utils.RetryUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base Page Object containing common page operations
 * Enhanced with smart wait utilities and retry mechanisms
 */
public class BasePage {
    protected Page page;
    protected static final int DEFAULT_TIMEOUT = ConfigManager.getTimeout();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public BasePage(Page page) {
        this.page = page;
    }
    
    /**
     * Log step to console with timestamp
     */
    protected void logStep(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.out.println(String.format("[%s] -> %s", timestamp, message));
    }
    
    /**
     * Navigate to a URL with smart wait
     */
    protected BasePage navigateTo(String url) {
        logStep("Navigate to URL: " + url);
        RetryUtils.retryVoid(() -> page.navigate(url), "Navigate to " + url);
        waitForPageReady();
        return this;
    }
    
    /**
     * Wait for page to be fully loaded (smart wait)
     */
    protected void waitForPageLoad() {
        WaitUtils.waitForPageReady(page);
    }
    
    /**
     * Wait for page to be ready (DOM + Network)
     */
    protected void waitForPageReady() {
        WaitUtils.waitForPageReady(page, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for network to be idle
     */
    protected void waitForNetworkIdle() {
        WaitUtils.waitForNetworkIdle(page, DEFAULT_TIMEOUT);
    }
    
    /**
     * Wait for AJAX requests to complete
     */
    protected void waitForAjaxComplete() {
        WaitUtils.waitForAjaxComplete(page);
    }
    
    /**
     * Get current page URL
     */
    protected String getCurrentUrl() {
        return page.url();
    }
    
    /**
     * Get page title
     */
    protected String getPageTitle() {
        return page.title();
    }
    
    /**
     * Wait for URL to contain specific text
     */
    protected void waitForUrlContains(String urlPart) {
        page.waitForURL("**/" + urlPart + "**", new Page.WaitForURLOptions()
                .setTimeout(DEFAULT_TIMEOUT));
    }
    
    /**
     * Wait for URL to match pattern
     */
    protected void waitForUrlMatches(String pattern) {
        page.waitForURL(pattern, new Page.WaitForURLOptions()
                .setTimeout(DEFAULT_TIMEOUT));
    }
    
    /**
     * Check if element is visible
     */
    protected boolean isElementVisible(String selector) {
        return page.locator(selector).isVisible();
    }
    
    /**
     * Wait for element to be visible
     */
    protected BasePage waitForElement(String selector) {
        logStep("Wait for element: " + selector);
        Locator locator = page.locator(selector);
        locator.waitFor(new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT));
        return this;
    }
    
    /**
     * Wait for element to be stable (no movement/animation)
     */
    protected BasePage waitForElementStable(Locator locator) {
        WaitUtils.waitForElementToBeStable(locator, DEFAULT_TIMEOUT);
        return this;
    }
    
    /**
     * Wait for text to appear on page
     */
    protected BasePage waitForText(String text) {
        WaitUtils.waitForTextToAppear(page, text, DEFAULT_TIMEOUT);
        return this;
    }
    
    /**
     * Wait for text to disappear from page
     */
    protected BasePage waitForTextGone(String text) {
        WaitUtils.waitForTextToDisappear(page, text, DEFAULT_TIMEOUT);
        return this;
    }
    
    /**
     * Click on element with retry
     */
    protected BasePage click(String selector) {
        logStep("Click element: " + selector);
        Locator locator = page.locator(selector);
        RetryUtils.retryVoid(() -> {
            WaitUtils.waitForElementClickable(locator, DEFAULT_TIMEOUT);
            locator.click();
        }, "Click " + selector);
        return this;
    }
    
    /**
     * Click locator with retry and wait for stability
     */
    protected BasePage clickWithWait(Locator locator) {
        RetryUtils.retryVoid(() -> {
            WaitUtils.waitForElementClickable(locator, DEFAULT_TIMEOUT);
            locator.click();
        }, "Click element");
        return this;
    }
    
    /**
     * Fill text into input
     */
    protected BasePage fill(String selector, String text) {
        logStep("Fill '" + text + "' into: " + selector);
        page.locator(selector).fill(text);
        return this;
    }
    
    /**
     * Get text from element
     */
    protected String getText(String selector) {
        return page.locator(selector).textContent();
    }
    
    /**
     * Take screenshot
     */
    protected byte[] takeScreenshot() {
        return page.screenshot();
    }
}
