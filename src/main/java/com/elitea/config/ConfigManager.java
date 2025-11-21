package com.elitea.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for loading and accessing test properties
 */
public class ConfigManager {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";
    private static final String TEST_CONFIG_FILE = "test.properties";
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        // Load main config.properties
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
        
        // Load test.properties (contains sensitive data like credentials)
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream(TEST_CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Warning: " + TEST_CONFIG_FILE + " not found, authentication may fail");
            } else {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to load " + TEST_CONFIG_FILE + ": " + e.getMessage());
        }
    }
    
    public static String getProperty(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }
    
    public static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }
    
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
    
    // Application URLs
    public static String getAppUrl() {
        return getProperty("app.url");
    }
    
    public static int getTimeout() {
        return getIntProperty("app.timeout");
    }
    
    public static boolean isHeadless() {
        return getBooleanProperty("app.headless");
    }
    
    // Browser Configuration
    public static String getBrowserType() {
        return getProperty("browser.type", "chromium");
    }
    
    public static int getBrowserWidth() {
        return getIntProperty("browser.width");
    }
    
    public static int getBrowserHeight() {
        return getIntProperty("browser.height");
    }
    
    // Playwright Configuration
    public static int getSlowMo() {
        return getIntProperty("playwright.slowmo");
    }
    
    public static String getVideoMode() {
        return getProperty("playwright.video");
    }
    
    public static String getScreenshotMode() {
        return getProperty("playwright.screenshot");
    }
    
    public static String getTraceMode() {
        return getProperty("playwright.trace");
    }
    
    // Test Configuration
    public static boolean isParallelEnabled() {
        return getBooleanProperty("test.parallel.enabled");
    }
    
    public static int getParallelThreads() {
        return getIntProperty("test.parallel.threads");
    }
    
    public static int getRetryCount() {
        return getIntProperty("test.retry.count");
    }
    
    // Authentication Configuration
    public static String getTestPin() {
        String pin = getProperty("test.pin");
        if (pin == null || pin.isEmpty()) {
            throw new RuntimeException("Test PIN not configured. Please set 'test.pin' in test.properties");
        }
        return pin;
    }
    
    public static String getTestUser() {
        return getProperty("test.user", "");
    }
}
