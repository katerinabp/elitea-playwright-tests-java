package com.elitea.base;

import com.microsoft.playwright.*;
import com.elitea.config.ConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Paths;

/**
 * Base Test class containing common test setup and teardown
 */
public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    @BeforeEach
    public void setUp() {
        // Initialize Playwright
        playwright = Playwright.create();
        
        // Configure browser launch options - force headless to false to show browser
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(false)  // Always show browser
                .setSlowMo(ConfigManager.getSlowMo());
        
        // Launch browser based on configuration
        String browserType = ConfigManager.getBrowserType();
        browser = switch (browserType.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit" -> playwright.webkit().launch(launchOptions);
            default -> playwright.chromium().launch(launchOptions);
        };
        
        // Configure browser context with incognito mode (each context is isolated)
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(ConfigManager.getBrowserWidth(), ConfigManager.getBrowserHeight());
        
        // Configure recording options based on settings
        String videoMode = ConfigManager.getVideoMode();
        if ("on".equals(videoMode) || "retain-on-failure".equals(videoMode)) {
            contextOptions.setRecordVideoDir(Paths.get("test-results/videos"));
        }
        
        String screenshotMode = ConfigManager.getScreenshotMode();
        if ("on".equals(screenshotMode) || "only-on-failure".equals(screenshotMode)) {
            contextOptions.setRecordVideoDir(Paths.get("test-results/screenshots"));
        }
        
        // Create context (each new context is incognito by default in Playwright)
        context = browser.newContext(contextOptions);
        
        // Enable tracing if configured
        String traceMode = ConfigManager.getTraceMode();
        if ("on".equals(traceMode) || "retain-on-failure".equals(traceMode)) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
        
        page = context.newPage();
    }
    
    @AfterEach
    public void tearDown() {
        // Stop tracing if enabled
        String traceMode = ConfigManager.getTraceMode();
        if ("on".equals(traceMode) || "retain-on-failure".equals(traceMode)) {
            String tracePath = "test-results/traces/" + 
                    System.currentTimeMillis() + ".zip";
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(tracePath)));
        }
        
        // Close browser resources
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    /**
     * Navigate to application URL
     */
    protected void navigateToApp() {
        page.navigate(ConfigManager.getAppUrl());
    }
    
    /**
     * Take and save screenshot
     */
    protected void attachScreenshot(String name) {
        page.screenshot();
        System.out.println("[Screenshot] " + name);
    }
}
