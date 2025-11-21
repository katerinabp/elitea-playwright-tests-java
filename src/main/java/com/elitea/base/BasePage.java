package com.elitea.base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.elitea.config.ConfigManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base Page Object containing common page operations
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
        System.out.println(String.format("[%s] ✓ %s", timestamp, message));
    }
    
    /**
     * Navigate to a URL
     */
    protected BasePage navigateTo(String url) {
        logStep("Navigate to URL: " + url);
        page.navigate(url);
        waitForPageLoad();
        return this;
    }
    
    /**
     * Wait for page to be fully loaded
     */
    protected void waitForPageLoad() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }
    
    /**
     * Wait for network to be idle
     */
    protected void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
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
        page.locator(selector).waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setTimeout(DEFAULT_TIMEOUT));
        return this;
    }
    
    /**
     * Click on element
     */
    protected BasePage click(String selector) {
        logStep("Click element: " + selector);
        page.locator(selector).click();
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
