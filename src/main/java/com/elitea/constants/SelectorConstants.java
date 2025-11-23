package com.elitea.constants;

import java.util.regex.Pattern;

/**
 * Centralized selector constants for all page objects
 * Provides consistent, maintainable selectors across the test framework
 * 
 * Organization:
 * - Chat Page selectors
 * - Conversation Page selectors
 * - Common UI selectors
 * - Navigation selectors
 * - Form selectors
 * 
 * Selector Strategy Priority:
 * 1. Accessible role-based selectors (preferred)
 * 2. Test IDs (data-testid)
 * 3. Semantic HTML (if accessible)
 * 4. CSS selectors (last resort)
 */
public class SelectorConstants {
    
    // ==================== CHAT PAGE SELECTORS ====================
    
    public static class ChatPage {
        // Buttons
        public static final String CREATE_CONVERSATION_BUTTON = "Create Conversation";
        public static final String USER_PROFILE_BUTTON = "User profile";
        public static final String SEARCH_BUTTON = "search";
        public static final String SETTINGS_BUTTON = "Settings";
        
        // Input Fields
        public static final String SEARCH_INPUT = "search";
        
        // Lists and Containers
        public static final String CONVERSATIONS_LIST = "[data-testid='conversations-list'], .conversations-list";
        public static final String SIDEBAR = "[data-testid='sidebar'], .sidebar";
        
        // Navigation Items
        public static final String NAV_CHAT = "Chat";
        public static final String NAV_AGENTS = "Agents";
        public static final String NAV_PIPELINES = "Pipelines";
        public static final String NAV_TOOLKITS = "Toolkits";
        public static final String NAV_MCP = "MCP";
        public static final String NAV_COLLECTIONS = "Collections";
        public static final String NAV_ARTIFACTS = "Artifacts";
        
        // URL Patterns
        public static final String CHAT_URL_PATTERN = "**/chat**";
    }
    
    // ==================== CONVERSATION PAGE SELECTORS ====================
    
    public static class ConversationPage {
        // Input Fields
        public static final String MESSAGE_INPUT = "Type your message";
        public static final String MESSAGE_INPUT_PLACEHOLDER = "Type your message here...";
        
        // Buttons
        public static final String SEND_BUTTON = "send your question";
        public static final String COPY_BUTTON = "Copy to clipboard";
        public static final String REGENERATE_BUTTON = "Regenerate";
        public static final String DELETE_BUTTON = "Delete";
        public static final String ADD_AGENT_BUTTON = "Add agent";
        public static final String SELECT_MODEL_BUTTON = "Select LLM Model";
        public static final String CLEAR_CHAT_BUTTON = "Clear the chat history";
        
        // Message Elements
        public static final String USER_NAME = "Katerina Pikulik";
        public static final String AI_NAME = "Alita";
        public static final String GREETING_MESSAGE = "Hello, Katerina!";
        public static final String WELCOME_MESSAGE = "What can I do for you today?";
        
        // Patterns (Regex)
        public static final Pattern THOUGHT_PROCESS_PATTERN = Pattern.compile(
            "Thought for \\d+ sec", Pattern.CASE_INSENSITIVE);
        public static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
            "less than a minute ago|minute ago|minutes ago|hour ago|hours ago", 
            Pattern.CASE_INSENSITIVE);
        
        // Locators (CSS/Generic)
        public static final String MESSAGE_CONTAINER = ".message-container";
        public static final String USER_MESSAGE = ".user-message";
        public static final String AI_MESSAGE = ".ai-message";
        public static final String ACTION_BUTTONS = "generic";
        public static final String THOUGHT_SECTION = ".thought-section";
        
        // URL Patterns
        public static final String CONVERSATION_URL_PATTERN = "**/chat/**";
    }
    
    // ==================== COMMON UI SELECTORS ====================
    
    public static class Common {
        // Buttons
        public static final String SUBMIT_BUTTON = "Submit";
        public static final String CANCEL_BUTTON = "Cancel";
        public static final String CLOSE_BUTTON = "Close";
        public static final String SAVE_BUTTON = "Save";
        public static final String EDIT_BUTTON = "Edit";
        public static final String DELETE_BUTTON = "Delete";
        public static final String CONFIRM_BUTTON = "Confirm";
        
        // Modal/Dialog
        public static final String MODAL = "[role='dialog'], .modal";
        public static final String DIALOG = "[role='dialog']";
        public static final String ALERT = "[role='alert']";
        
        // Loading States
        public static final String LOADING_SPINNER = ".loading, .spinner, [data-loading='true']";
        public static final String LOADING_TEXT = "Loading...";
        
        // Notifications
        public static final String SUCCESS_MESSAGE = ".success-message, .alert-success";
        public static final String ERROR_MESSAGE = ".error-message, .alert-error";
        public static final String WARNING_MESSAGE = ".warning-message, .alert-warning";
        public static final String INFO_MESSAGE = ".info-message, .alert-info";
        
        // Form Elements
        public static final String TEXT_INPUT = "input[type='text']";
        public static final String EMAIL_INPUT = "input[type='email']";
        public static final String PASSWORD_INPUT = "input[type='password']";
        public static final String CHECKBOX = "input[type='checkbox']";
        public static final String RADIO = "input[type='radio']";
        public static final String SELECT = "select";
        
        // Accessibility
        public static final String ARIA_LABEL_CLOSE = "[aria-label='Close']";
        public static final String ARIA_LABEL_MENU = "[aria-label='Menu']";
    }
    
    // ==================== AGENT PAGE SELECTORS ====================
    
    public static class AgentPage {
        // Buttons
        public static final String CREATE_AGENT_BUTTON = "Create Agent";
        public static final String EDIT_AGENT_BUTTON = "Edit Agent";
        public static final String DELETE_AGENT_BUTTON = "Delete Agent";
        public static final String DUPLICATE_AGENT_BUTTON = "Duplicate";
        
        // Input Fields
        public static final String AGENT_NAME_INPUT = "Agent Name";
        public static final String AGENT_DESCRIPTION = "Description";
        public static final String AGENT_INSTRUCTIONS = "Instructions";
        
        // Lists
        public static final String AGENTS_LIST = "[data-testid='agents-list'], .agents-list";
        public static final String AGENT_CARD = ".agent-card";
        
        // Filters
        public static final String FILTER_BY_TAG = "Filter by tag";
        public static final String SEARCH_AGENTS = "Search agents";
        
        // URL Patterns
        public static final String AGENTS_URL_PATTERN = "**/agents**";
    }
    
    // ==================== PIPELINE PAGE SELECTORS ====================
    
    public static class PipelinePage {
        // Buttons
        public static final String CREATE_PIPELINE_BUTTON = "Create Pipeline";
        public static final String RUN_PIPELINE_BUTTON = "Run";
        public static final String STOP_PIPELINE_BUTTON = "Stop";
        
        // Input Fields
        public static final String PIPELINE_NAME = "Pipeline Name";
        
        // URL Patterns
        public static final String PIPELINE_URL_PATTERN = "**/pipelines**";
    }
    
    // ==================== TOOLKIT PAGE SELECTORS ====================
    
    public static class ToolkitPage {
        // Buttons
        public static final String CREATE_TOOLKIT_BUTTON = "Create Toolkit";
        
        // URL Patterns
        public static final String TOOLKIT_URL_PATTERN = "**/toolkits**";
    }
    
    // ==================== LOGIN PAGE SELECTORS ====================
    
    public static class LoginPage {
        // Input Fields
        public static final String USERNAME_INPUT = "username";
        public static final String PASSWORD_INPUT = "password";
        public static final String EMAIL_INPUT = "email";
        
        // Buttons
        public static final String LOGIN_BUTTON = "Log in";
        public static final String SIGNUP_BUTTON = "Sign up";
        public static final String FORGOT_PASSWORD = "Forgot password";
        public static final String SSO_LOGIN = "Sign in with SSO";
        
        // Messages
        public static final String ERROR_MESSAGE = ".error-message";
        public static final String SUCCESS_MESSAGE = ".success-message";
        
        // URL Patterns
        public static final String LOGIN_URL_PATTERN = "**/login**";
        public static final String SIGNUP_URL_PATTERN = "**/signup**";
    }
    
    // ==================== SETTINGS PAGE SELECTORS ====================
    
    public static class SettingsPage {
        // Navigation Tabs
        public static final String PROFILE_TAB = "Profile";
        public static final String PREFERENCES_TAB = "Preferences";
        public static final String SECURITY_TAB = "Security";
        public static final String BILLING_TAB = "Billing";
        
        // Buttons
        public static final String SAVE_SETTINGS = "Save Settings";
        public static final String CANCEL_SETTINGS = "Cancel";
        
        // URL Patterns
        public static final String SETTINGS_URL_PATTERN = "**/settings**";
    }
    
    // ==================== DATA ATTRIBUTES (Test IDs) ====================
    
    public static class TestIds {
        // Chat
        public static final String CHAT_CONTAINER = "[data-testid='chat-container']";
        public static final String MESSAGE_LIST = "[data-testid='message-list']";
        public static final String MESSAGE_ITEM = "[data-testid='message-item']";
        
        // Conversation
        public static final String CONVERSATION_HEADER = "[data-testid='conversation-header']";
        public static final String CONVERSATION_BODY = "[data-testid='conversation-body']";
        public static final String CONVERSATION_FOOTER = "[data-testid='conversation-footer']";
        
        // Navigation
        public static final String SIDEBAR_NAV = "[data-testid='sidebar-nav']";
        public static final String MAIN_CONTENT = "[data-testid='main-content']";
        
        // Agents
        public static final String AGENT_CARD = "[data-testid='agent-card']";
        public static final String AGENT_LIST = "[data-testid='agent-list']";
    }
    
    // ==================== ARIA ROLES (for role-based selectors) ====================
    
    public static class AriaRoles {
        public static final String BUTTON = "button";
        public static final String TEXTBOX = "textbox";
        public static final String LINK = "link";
        public static final String NAVIGATION = "navigation";
        public static final String DIALOG = "dialog";
        public static final String ALERT = "alert";
        public static final String LIST = "list";
        public static final String LISTITEM = "listitem";
        public static final String HEADING = "heading";
        public static final String SEARCH = "search";
        public static final String MENU = "menu";
        public static final String MENUITEM = "menuitem";
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Create a dynamic selector for text content
     */
    public static String byText(String text) {
        return String.format("text=%s", text);
    }
    
    /**
     * Create a dynamic selector for partial text match
     */
    public static String byPartialText(String text) {
        return String.format("text=/%s/i", text);
    }
    
    /**
     * Create a dynamic selector for test ID
     */
    public static String byTestId(String testId) {
        return String.format("[data-testid='%s']", testId);
    }
    
    /**
     * Create a dynamic selector for aria-label
     */
    public static String byAriaLabel(String label) {
        return String.format("[aria-label='%s']", label);
    }
    
    /**
     * Create a dynamic selector for class name
     */
    public static String byClass(String className) {
        return String.format(".%s", className);
    }
    
    /**
     * Create a dynamic selector for ID
     */
    public static String byId(String id) {
        return String.format("#%s", id);
    }
    
    /**
     * Create a pattern matcher for dynamic text
     */
    public static Pattern createPattern(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Create a pattern matcher for thought process with dynamic time
     */
    public static Pattern thoughtProcessPattern() {
        return SelectorConstants.ConversationPage.THOUGHT_PROCESS_PATTERN;
    }
    
    /**
     * Create a pattern matcher for timestamp
     */
    public static Pattern timestampPattern() {
        return SelectorConstants.ConversationPage.TIMESTAMP_PATTERN;
    }
    
    // ==================== PRIVATE CONSTRUCTOR ====================
    
    /**
     * Private constructor to prevent instantiation
     */
    private SelectorConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}
