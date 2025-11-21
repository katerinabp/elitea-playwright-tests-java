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
    private static final String EMAIL_INPUT = "input[type='email'], input[name='username'], input[placeholder*='email'], input[id*='email']";
    private static final String PIN_INPUT = "input[type='password'], input[name='password'], input[id*='password'], input[placeholder*='PIN']";
    private static final String CONTINUE_BUTTON = "button[type='submit'], input[type='submit'], button:has-text('Continue'), button:has-text('Next'), button:has-text('Sign in')";
    private static final String EPAM_EMAIL = "katerina_pikulik@epam.com";
    private static final String EPAM_PIN = System.getenv("EPAM_PIN") != null ? System.getenv("EPAM_PIN") : "";
    
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
     * Note: This assumes EPAM SSO credentials are already configured
     * or user is already authenticated in the browser session
     */
    public ChatPage loginWithEpamSSO() {
        logStep("Perform EPAM SSO login");
        clickEpamLogin();
        
        // Check if email input is present (SSO login screen)
        try {
            page.waitForSelector(EMAIL_INPUT, new Page.WaitForSelectorOptions().setTimeout(5000));
            logStep("Email input detected - entering EPAM email");
            fill(EMAIL_INPUT, EPAM_EMAIL);
            
            // Click continue/submit button
            if (isElementVisible(CONTINUE_BUTTON)) {
                logStep("Clicking continue button");
                click(CONTINUE_BUTTON);
            }
            
            // Wait for PIN/password input to appear
            page.waitForSelector(PIN_INPUT, new Page.WaitForSelectorOptions().setTimeout(10000));
            logStep("PIN input detected - entering PIN");
            
            if (!EPAM_PIN.isEmpty()) {
                fill(PIN_INPUT, EPAM_PIN);
                logStep("PIN entered successfully");
                
                // Click sign in button
                if (isElementVisible(CONTINUE_BUTTON)) {
                    logStep("Clicking sign in button");
                    click(CONTINUE_BUTTON);
                }
            } else {
                logStep("EPAM_PIN environment variable not set - waiting for manual PIN entry");
                // Wait longer for manual entry
                page.waitForTimeout(30000);
            }
        } catch (Exception e) {
            logStep("No email input found - assuming already authenticated or different flow");
        }
        
        // Wait for redirect to chat page
        waitForUrlContains("chat");
        logStep("Successfully logged in and redirected to chat page");
        
        return new ChatPage(page);
    }
    
    /**
     * Complete login flow
     */
    public ChatPage login() {
        return loginWithEpamSSO();
    }
}
