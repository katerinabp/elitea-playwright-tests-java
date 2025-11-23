package com.elitea.examples;

import com.elitea.utils.WaitUtils;
import com.elitea.utils.RetryUtils;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

/**
 * Examples demonstrating how to use WaitUtils and RetryUtils
 * in Playwright tests for reliable, maintainable test automation
 */
public class WaitUtilsExamples {
    
    private Page page;
    
    /**
     * Example 1: Replace hardcoded timeout with smart wait
     */
    public void example1_SmartWait() {
        // ❌ OLD WAY - Hardcoded timeout
        // page.waitForTimeout(2000);
        
        // ✅ NEW WAY - Smart wait for page ready
        WaitUtils.waitForPageReady(page);
        
        // Or wait for network idle specifically
        WaitUtils.waitForNetworkIdle(page);
    }
    
    /**
     * Example 2: Wait for element to be stable (animations)
     */
    public void example2_ElementStability() {
        Locator button = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Submit"));
        
        // ❌ OLD WAY - Random timeout hoping animation completes
        // page.waitForTimeout(1000);
        // button.click();
        
        // ✅ NEW WAY - Wait for element to stop moving
        WaitUtils.waitForElementToBeStable(button);
        button.click();
    }
    
    /**
     * Example 3: Wait for AJAX/API calls to complete
     */
    public void example3_AjaxWait() {
        // Click button that triggers AJAX request
        page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Load Data")).click();
        
        // ❌ OLD WAY - Arbitrary timeout
        // page.waitForTimeout(3000);
        
        // ✅ NEW WAY - Wait for AJAX to complete
        WaitUtils.waitForAjaxComplete(page);
        
        // Now verify data is loaded
        WaitUtils.waitForTextToAppear(page, "Data loaded successfully");
    }
    
    /**
     * Example 4: Wait for text to appear/disappear
     */
    public void example4_TextWait() {
        // ❌ OLD WAY - Try-catch with timeout
        // try {
        //     page.waitForSelector("text=Loading...", new Page.WaitForSelectorOptions().setTimeout(5000));
        // } catch (Exception e) {
        //     // Ignore
        // }
        
        // ✅ NEW WAY - Wait for loading text to disappear
        WaitUtils.waitForTextToDisappear(page, "Loading...");
        
        // Wait for success message to appear
        WaitUtils.waitForTextToAppear(page, "Success!");
    }
    
    /**
     * Example 5: Wait for element to be clickable
     */
    public void example5_ClickableWait() {
        Locator submitButton = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Submit"));
        
        // ✅ Wait until button is visible AND enabled
        WaitUtils.waitForElementClickable(submitButton);
        submitButton.click();
    }
    
    /**
     * Example 6: Custom condition wait
     */
    public void example6_CustomCondition() {
        // Wait until specific condition is met
        WaitUtils.waitUntil(() -> {
            int messageCount = page.locator(".message").count();
            return messageCount > 5;
        }, 10000, "At least 5 messages to appear");
    }
    
    /**
     * Example 7: Smart wait combining multiple strategies
     */
    public void example7_SmartWaitCombined() {
        Locator dataTable = page.locator("#data-table");
        
        // Smart wait: page ready + element visible + element stable
        WaitUtils.smartWait(page, dataTable);
        
        // Now safe to interact with table
        dataTable.getByRole(AriaRole.ROW).first().click();
    }
    
    /**
     * Example 8: Retry flaky operations
     */
    public void example8_RetryFlaky() {
        // ❌ OLD WAY - Hope it works
        // page.getByRole(AriaRole.BUTTON).click();
        
        // ✅ NEW WAY - Retry if it fails
        RetryUtils.retryVoid(() -> {
            page.getByRole(AriaRole.BUTTON, 
                new Page.GetByRoleOptions().setName("Save")).click();
        }, "Click Save button");
    }
    
    /**
     * Example 9: Retry with custom configuration
     */
    public void example9_RetryCustomConfig() {
        // Retry up to 5 times with 500ms initial delay
        RetryUtils.retryVoid(() -> {
            uploadFile();
        }, "Upload file", 
        RetryUtils.config()
            .maxAttempts(5)
            .initialDelay(500)
            .backoffMultiplier(1.5));
    }
    
    /**
     * Example 10: Retry with timeout
     */
    public void example10_RetryWithTimeout() {
        // Retry for max 10 seconds total
        String result = RetryUtils.retryWithTimeout(() -> {
            return fetchData();
        }, "Fetch data from API", 10000);
    }
    
    /**
     * Example 11: Retry only specific exceptions
     */
    public void example11_RetrySpecificExceptions() {
        // Only retry on TimeoutError, not on other exceptions
        RetryUtils.retryVoid(() -> {
            page.getByText("Submit").click();
        }, "Click submit",
        RetryUtils.config()
            .retryOn(com.microsoft.playwright.TimeoutError.class));
    }
    
    /**
     * Example 12: Wait for animation to complete
     */
    public void example12_AnimationWait() {
        Locator modal = page.locator(".modal");
        
        // Wait for modal animation (slide-in, fade-in) to complete
        WaitUtils.waitForAnimationComplete(modal);
        
        // Now interact with modal content
        modal.getByRole(AriaRole.BUTTON, 
            new Locator.GetByRoleOptions().setName("Close")).click();
    }
    
    /**
     * Example 13: Wait for specific number of elements
     */
    public void example13_ElementCount() {
        Locator items = page.locator(".list-item");
        
        // Wait until exactly 10 items are loaded
        WaitUtils.waitForElementCount(items, 10, 15000);
    }
    
    /**
     * Example 14: Fluent wait with custom polling
     */
    public void example14_FluentWait() {
        // Poll every 200ms for up to 10 seconds
        WaitUtils.fluentWait(() -> {
            String status = page.locator("#status").textContent();
            return "Completed".equals(status);
        }, 10000, 200, "Status to be 'Completed'");
    }
    
    /**
     * Example 15: Complete test scenario with smart waits
     */
    public void example15_CompleteScenario() {
        // Navigate and wait for page ready
        page.navigate("https://example.com");
        WaitUtils.waitForPageReady(page);
        
        // Wait for and click button with retry
        RetryUtils.retryVoid(() -> {
            Locator loginButton = page.getByRole(AriaRole.BUTTON, 
                new Page.GetByRoleOptions().setName("Login"));
            WaitUtils.waitForElementClickable(loginButton);
            loginButton.click();
        }, "Click login button");
        
        // Wait for form to appear and stabilize
        Locator form = page.locator("form#login-form");
        WaitUtils.waitForElementToBeStable(form);
        
        // Fill form and submit with retry
        RetryUtils.retryVoid(() -> {
            page.fill("#username", "testuser");
            page.fill("#password", "password123");
            page.click("button[type='submit']");
        }, "Submit login form");
        
        // Wait for AJAX login request to complete
        WaitUtils.waitForAjaxComplete(page);
        
        // Wait for redirect
        WaitUtils.waitForUrlContains(page, "/dashboard", 10000);
        
        // Wait for dashboard to load
        WaitUtils.waitForTextToAppear(page, "Welcome");
    }
    
    // Helper methods
    private void uploadFile() {
        // Simulated file upload
    }
    
    private String fetchData() {
        // Simulated API call
        return "data";
    }
}
