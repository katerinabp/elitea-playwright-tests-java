package com.elitea.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.regex.Pattern;

/**
 * Utility class for working with selectors in Playwright
 * Provides helper methods for common selector patterns and dynamic selectors
 */
public class SelectorUtils {
    
    // ==================== ROLE-BASED SELECTORS ====================
    
    /**
     * Get element by role and name
     */
    public static Locator getByRoleAndName(Page page, AriaRole role, String name) {
        return page.getByRole(role, new Page.GetByRoleOptions().setName(name));
    }
    
    /**
     * Get element by role and name pattern
     */
    public static Locator getByRoleAndNamePattern(Page page, AriaRole role, Pattern pattern) {
        return page.getByRole(role, new Page.GetByRoleOptions().setName(pattern));
    }
    
    /**
     * Get element by role and name (case insensitive)
     */
    public static Locator getByRoleAndNameIgnoreCase(Page page, AriaRole role, String name) {
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        return page.getByRole(role, new Page.GetByRoleOptions().setName(pattern));
    }
    
    /**
     * Get button by name
     */
    public static Locator getButtonByName(Page page, String name) {
        return getByRoleAndName(page, AriaRole.BUTTON, name);
    }
    
    /**
     * Get button by name (case insensitive)
     */
    public static Locator getButtonByNameIgnoreCase(Page page, String name) {
        return getByRoleAndNameIgnoreCase(page, AriaRole.BUTTON, name);
    }
    
    /**
     * Get textbox by name
     */
    public static Locator getTextboxByName(Page page, String name) {
        return getByRoleAndName(page, AriaRole.TEXTBOX, name);
    }
    
    /**
     * Get textbox by name pattern
     */
    public static Locator getTextboxByNamePattern(Page page, Pattern pattern) {
        return getByRoleAndNamePattern(page, AriaRole.TEXTBOX, pattern);
    }
    
    /**
     * Get link by name
     */
    public static Locator getLinkByName(Page page, String name) {
        return getByRoleAndName(page, AriaRole.LINK, name);
    }
    
    // ==================== TEXT SELECTORS ====================
    
    /**
     * Get element by exact text
     */
    public static Locator getByText(Page page, String text) {
        return page.getByText(text);
    }
    
    /**
     * Get element by exact text (first match)
     */
    public static Locator getByTextFirst(Page page, String text) {
        return page.getByText(text).first();
    }
    
    /**
     * Get element by text pattern
     */
    public static Locator getByTextPattern(Page page, Pattern pattern) {
        return page.getByText(pattern);
    }
    
    /**
     * Get element by partial text (case insensitive)
     */
    public static Locator getByPartialText(Page page, String text) {
        Pattern pattern = Pattern.compile(".*" + Pattern.quote(text) + ".*", 
            Pattern.CASE_INSENSITIVE);
        return page.getByText(pattern);
    }
    
    // ==================== DATA ATTRIBUTE SELECTORS ====================
    
    /**
     * Get element by test ID
     */
    public static Locator getByTestId(Page page, String testId) {
        return page.locator(String.format("[data-testid='%s']", testId));
    }
    
    /**
     * Get element by data attribute
     */
    public static Locator getByDataAttribute(Page page, String attribute, String value) {
        return page.locator(String.format("[data-%s='%s']", attribute, value));
    }
    
    /**
     * Get element by aria-label
     */
    public static Locator getByAriaLabel(Page page, String label) {
        return page.locator(String.format("[aria-label='%s']", label));
    }
    
    /**
     * Get element by aria-labelledby
     */
    public static Locator getByAriaLabelledBy(Page page, String id) {
        return page.locator(String.format("[aria-labelledby='%s']", id));
    }
    
    // ==================== CSS SELECTORS ====================
    
    /**
     * Get element by class name
     */
    public static Locator getByClass(Page page, String className) {
        return page.locator(String.format(".%s", className));
    }
    
    /**
     * Get element by ID
     */
    public static Locator getById(Page page, String id) {
        return page.locator(String.format("#%s", id));
    }
    
    /**
     * Get element by CSS selector
     */
    public static Locator getByCssSelector(Page page, String selector) {
        return page.locator(selector);
    }
    
    // ==================== COMBINED SELECTORS ====================
    
    /**
     * Get element by class and text
     */
    public static Locator getByClassAndText(Page page, String className, String text) {
        return page.locator(String.format(".%s:has-text('%s')", className, text));
    }
    
    /**
     * Get element by tag and text
     */
    public static Locator getByTagAndText(Page page, String tag, String text) {
        return page.locator(String.format("%s:has-text('%s')", tag, text));
    }
    
    // ==================== LOCATOR HELPERS ====================
    
    /**
     * Filter locator by text
     */
    public static Locator filterByText(Locator locator, String text) {
        return locator.filter(new Locator.FilterOptions().setHasText(text));
    }
    
    /**
     * Filter locator by text pattern
     */
    public static Locator filterByTextPattern(Locator locator, Pattern pattern) {
        return locator.filter(new Locator.FilterOptions().setHasText(pattern));
    }
    
    /**
     * Get nth element from locator
     */
    public static Locator getNth(Locator locator, int index) {
        return locator.nth(index);
    }
    
    /**
     * Get first element from locator
     */
    public static Locator getFirst(Locator locator) {
        return locator.first();
    }
    
    /**
     * Get last element from locator
     */
    public static Locator getLast(Locator locator) {
        return locator.last();
    }
    
    // ==================== PATTERN BUILDERS ====================
    
    /**
     * Create case-insensitive pattern
     */
    public static Pattern caseInsensitive(String text) {
        return Pattern.compile(text, Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Create pattern for exact match
     */
    public static Pattern exactMatch(String text) {
        return Pattern.compile("^" + Pattern.quote(text) + "$");
    }
    
    /**
     * Create pattern for partial match
     */
    public static Pattern partialMatch(String text) {
        return Pattern.compile(".*" + Pattern.quote(text) + ".*", 
            Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Create pattern for starts with
     */
    public static Pattern startsWith(String text) {
        return Pattern.compile("^" + Pattern.quote(text) + ".*", 
            Pattern.CASE_INSENSITIVE);
    }
    
    /**
     * Create pattern for ends with
     */
    public static Pattern endsWith(String text) {
        return Pattern.compile(".*" + Pattern.quote(text) + "$", 
            Pattern.CASE_INSENSITIVE);
    }
    
    // ==================== VISIBILITY HELPERS ====================
    
    /**
     * Check if element is visible
     */
    public static boolean isVisible(Locator locator) {
        try {
            return locator.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     */
    public static boolean isEnabled(Locator locator) {
        try {
            return locator.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if element exists (count > 0)
     */
    public static boolean exists(Locator locator) {
        try {
            return locator.count() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ==================== XPATH HELPERS ====================
    
    /**
     * Get element by XPath
     */
    public static Locator getByXPath(Page page, String xpath) {
        return page.locator("xpath=" + xpath);
    }
    
    /**
     * Create XPath for text content
     */
    public static String xpathByText(String text) {
        return String.format("//*[text()='%s']", text);
    }
    
    /**
     * Create XPath for partial text content
     */
    public static String xpathByPartialText(String text) {
        return String.format("//*[contains(text(), '%s')]", text);
    }
    
    /**
     * Create XPath for attribute
     */
    public static String xpathByAttribute(String attribute, String value) {
        return String.format("//*[@%s='%s']", attribute, value);
    }
    
    // ==================== FRAME HELPERS ====================
    
    /**
     * Get locator in iframe by selector
     */
    public static Locator getInFrame(Page page, String frameSelector, String elementSelector) {
        return page.frameLocator(frameSelector).locator(elementSelector);
    }
    
    /**
     * Get locator in iframe by name
     */
    public static Locator getInFrameByName(Page page, String frameName, String elementSelector) {
        return page.frameLocator(String.format("[name='%s']", frameName))
            .locator(elementSelector);
    }
    
    // ==================== SHADOW DOM HELPERS ====================
    
    /**
     * Get element in shadow DOM
     */
    public static Locator getInShadowDom(Page page, String hostSelector, String shadowSelector) {
        return page.locator(hostSelector + " >>> " + shadowSelector);
    }
    
    // ==================== PRIVATE CONSTRUCTOR ====================
    
    /**
     * Private constructor to prevent instantiation
     */
    private SelectorUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
