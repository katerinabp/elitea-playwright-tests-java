package com.elitea.pages;

import com.microsoft.playwright.Page;
import com.elitea.base.BasePage;

/**
 * Page Object for Login Page
 */
public class LoginPage extends BasePage {
    
    // Locators
    private static final String EPAM_IDP_BUTTON = "a[href*='epam']";
    private static final String FOREFRONT_IDP_BUTTON = "a[href*='forefront']";
    
    public LoginPage(Page page) {
        super(page);
    }
    
    /**
     * Navigate to login page
     */
    public LoginPage navigateToLoginPage(String url) {
        logStep("Navigate to login page");
        navigateTo(url);
        return this;
    }
    
    /**
     * Check if login page is displayed
     */
    public boolean isLoginPageDisplayed() {
        logStep("Verify login page is displayed");
        return isElementVisible(EPAM_IDP_BUTTON) || 
               isElementVisible(FOREFRONT_IDP_BUTTON);
    }
    
    /**
     * Click on EPAM IDP login button
     */
    public LoginPage clickEpamLogin() {
        logStep("Click EPAM IDP login button");
        click(EPAM_IDP_BUTTON);
        // Wait for navigation to complete
        page.waitForLoadState();
        return this;
    }
    
    /**
     * Click on ForeFront IDP login button
     */
    public LoginPage clickForeFrontLogin() {
        logStep("Click ForeFront IDP login button");
        click(FOREFRONT_IDP_BUTTON);
        // Wait for navigation to complete
        page.waitForLoadState();
        return this;
    }
    
    /**
     * Perform EPAM SSO login
     * 
     * MCP-verified flow (2024-01-15):
     * 1. Navigate to /alita_ui/chat → redirects to auth.elitea.ai login page
     * 2. Click "EPAM" link → redirects to MetaDefender compliance check
     * 3. MetaDefender runs for ~30 seconds → auto-completes
     * 4. Redirects to chat page (fully authenticated)
     * 
     * Note: No email/PIN entry required - SSO session is already established
     */
    public ChatPage loginWithEpamSSO() {
        logStep("Perform EPAM SSO login");
        clickEpamLogin();
        
        // After clicking EPAM, wait for MetaDefender compliance check
        try {
            logStep("Waiting for MetaDefender compliance check to complete (max 60 seconds)");
            // Wait for page to contain "chat" in URL or title (indicates successful login)
            page.waitForURL(url -> url.contains("chat"), new Page.WaitForURLOptions().setTimeout(60000));
            logStep("MetaDefender compliance check completed - redirected to chat page");
            
            // Wait for page to be fully loaded (network idle + DOM ready)
            logStep("Waiting for chat page to fully load");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE, 
                new Page.WaitForLoadStateOptions().setTimeout(30000));
            logStep("Chat page fully loaded");
        } catch (Exception e) {
            logStep("Timeout waiting for redirect to chat page - compliance check may have failed");
            throw new RuntimeException("Login failed - MetaDefender compliance check timeout", e);
        }
        
        logStep("Successfully logged in via EPAM SSO");
        return new ChatPage(page);
    }
    
    /**
     * Complete login flow
     */
    public ChatPage login() {
        return loginWithEpamSSO();
    }
}
