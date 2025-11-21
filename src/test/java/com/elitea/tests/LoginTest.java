package com.elitea.tests;

import com.elitea.base.BaseTest;
import com.elitea.pages.ChatPage;
import com.elitea.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Login functionality
 */
public class LoginTest extends BaseTest {
    
    @Test
    @DisplayName("TC_LOGIN_01 - Successful EPAM SSO Login")
    public void testEpamSsoLogin() {
        // Arrange
        LoginPage loginPage = new LoginPage(page);
        
        // Act
        loginPage.navigateToLoginPage(com.elitea.config.ConfigManager.getAppUrl());
        
        // Debug: Print current URL
        System.out.println("Current URL after navigation: " + page.url());
        
        // Check if already logged in (redirect to chat) or login page is shown
        if (page.url().contains("chat")) {
            System.out.println("Already logged in - on chat page");
            // Already logged in, navigate to chat
            ChatPage chatPage = new ChatPage(page);
            
            // Assert
            assertThat(chatPage.isChatPageDisplayed())
                    .as("Chat page should be displayed after navigation")
                    .isTrue();
            
            assertThat(chatPage.getChatPageTitle())
                    .as("Page title should contain 'Chat'")
                    .contains("Chat");
            
            assertThat(chatPage.isChatInputVisible())
                    .as("Chat input should be visible")
                    .isTrue();
        } else {
            System.out.println("On login page - checking for IDP buttons");
            // Login page is displayed
            System.out.println("Is login page displayed: " + loginPage.isLoginPageDisplayed());
            assertThat(loginPage.isLoginPageDisplayed())
                    .as("Login page should be displayed")
                    .isTrue();
            
            // Perform EPAM SSO login
            ChatPage chatPage = loginPage.loginWithEpamSSO();
            
            // Assert
            assertThat(chatPage.isChatPageDisplayed())
                    .as("Chat page should be displayed after login")
                    .isTrue();
            
            assertThat(chatPage.getChatPageTitle())
                    .as("Page title should contain 'Chat'")
                    .contains("Chat");
            
            assertThat(chatPage.isChatInputVisible())
                    .as("Chat input should be visible")
                    .isTrue();
        }
    }
    
    @Test
    @DisplayName("TC_LOGIN_02 - Verify Chat Page Elements")
    public void testChatPageElements() {
        // Arrange & Act
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigateToLoginPage(com.elitea.config.ConfigManager.getAppUrl());
        
        ChatPage chatPage;
        if (page.url().contains("chat")) {
            // Already logged in
            chatPage = new ChatPage(page);
        } else {
            // Need to login
            chatPage = loginPage.login();
        }
        
        // Assert
        assertThat(chatPage.isChatPageDisplayed())
                .as("Chat page should be displayed")
                .isTrue();
        
        assertThat(chatPage.isChatInputVisible())
                .as("Chat input should be visible")
                .isTrue();
        
        assertThat(chatPage.isUserMenuVisible())
                .as("User menu should be visible")
                .isTrue();
    }
}
