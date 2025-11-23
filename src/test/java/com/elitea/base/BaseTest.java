package com.elitea.base;

import com.microsoft.playwright.*;
import com.elitea.config.ConfigManager;
import com.elitea.utils.AuthStateManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Paths;

/**
 * Base Test class containing common test setup and teardown.
 * Thread-safe implementation supporting parallel test execution.
 * 
 * <p>Each test gets its own isolated Playwright instance, browser, context, and page
 * via ThreadLocal storage. This ensures no shared state between parallel test executions.
 * 
 * <p>Configuration:
 * <ul>
 *   <li>browser: chromium (default), firefox, or webkit (from ConfigManager)</li>
 *   <li>headless: controlled by system property (true for parallel, false for sequential)</li>
 *   <li>parallel: controlled by junit-platform.properties</li>
 * </ul>
 */
public class BaseTest {
    
    // Thread-local storage for Playwright resources
    // Each thread gets its own isolated instances for parallel execution
    private static final ThreadLocal<Playwright> threadLocalPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> threadLocalBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> threadLocalContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> threadLocalPage = new ThreadLocal<>();
    
    // Configuration - check if parallel execution is enabled
    private static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "false"));
    private static final boolean PARALLEL_ENABLED = Boolean.parseBoolean(
        System.getProperty("junit.jupiter.execution.parallel.enabled", "false")
    );
    
    // Legacy direct field access - maintains backward compatibility with existing tests
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    
    /**
     * Gets the Playwright instance for the current thread.
     * Thread-safe accessor for parallel execution.
     */
    protected Playwright getPlaywright() {
        return threadLocalPlaywright.get();
    }

    /**
     * Gets the Browser instance for the current thread.
     * Thread-safe accessor for parallel execution.
     */
    protected Browser getBrowser() {
        return threadLocalBrowser.get();
    }

    /**
     * Gets the BrowserContext instance for the current thread.
     * Thread-safe accessor for parallel execution.
     */
    protected BrowserContext getContext() {
        return threadLocalContext.get();
    }

    /**
     * Gets the Page instance for the current thread.
     * Thread-safe accessor for parallel execution.
     */
    protected Page getPage() {
        return threadLocalPage.get();
    }
    
    @BeforeEach
    public void setUp() {
        // Initialize Playwright for this thread
        Playwright playwrightInstance = Playwright.create();
        threadLocalPlaywright.set(playwrightInstance);
        
        // Configure browser launch options
        // Force headless mode for parallel execution to avoid multiple browser windows
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(HEADLESS)
                .setSlowMo(ConfigManager.getSlowMo());
        
        // Launch browser based on configuration
        String browserType = ConfigManager.getBrowserType();
        Browser browserInstance = switch (browserType.toLowerCase()) {
            case "firefox" -> playwrightInstance.firefox().launch(launchOptions);
            case "webkit" -> playwrightInstance.webkit().launch(launchOptions);
            default -> playwrightInstance.chromium().launch(launchOptions);
        };
        threadLocalBrowser.set(browserInstance);
        
        // Configure browser context with incognito mode (each context is isolated)
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(ConfigManager.getBrowserWidth(), ConfigManager.getBrowserHeight());
        
        // Load saved authentication state if available
        if (java.nio.file.Files.exists(Paths.get("playwright/.auth/state.json"))) {
            System.out.println("[INFO] Loading saved authentication state...");
            contextOptions.setStorageStatePath(Paths.get("playwright/.auth/state.json"));
        } else {
            System.out.println("[WARN] No authentication state found. Tests may require login.");
            System.out.println("       Run: ./gradlew authSetup");
        }
        
        // Configure recording options based on settings
        String videoMode = ConfigManager.getVideoMode();
        if ("on".equals(videoMode) || ("retain-on-failure".equals(videoMode))) {
            contextOptions.setRecordVideoDir(Paths.get("test-results/videos"));
        }
        
        String screenshotMode = ConfigManager.getScreenshotMode();
        if ("on".equals(screenshotMode) || "only-on-failure".equals(screenshotMode)) {
            contextOptions.setRecordVideoDir(Paths.get("test-results/screenshots"));
        }
        
        // Create context (each new context is incognito by default in Playwright)
        BrowserContext contextInstance = browserInstance.newContext(contextOptions);
        threadLocalContext.set(contextInstance);
        
        // Enable tracing if configured
        String traceMode = ConfigManager.getTraceMode();
        if ("on".equals(traceMode) || "retain-on-failure".equals(traceMode)) {
            contextInstance.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }
        
        Page pageInstance = contextInstance.newPage();
        threadLocalPage.set(pageInstance);
        
        // Set legacy fields for backward compatibility
        this.playwright = playwrightInstance;
        this.browser = browserInstance;
        this.context = contextInstance;
        this.page = pageInstance;
    }
    
    @AfterEach
    public void tearDown() {
        BrowserContext contextInstance = threadLocalContext.get();
        Page pageInstance = threadLocalPage.get();
        
        // Stop tracing if enabled
        String traceMode = ConfigManager.getTraceMode();
        if (contextInstance != null && ("on".equals(traceMode) || "retain-on-failure".equals(traceMode))) {
            String tracePath = "test-results/traces/" + 
                    System.currentTimeMillis() + ".zip";
            contextInstance.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(tracePath)));
        }
        
        // Only add delay in sequential mode with UI visible
        // Skip delay in parallel/headless mode for faster execution
        if (!PARALLEL_ENABLED && !HEADLESS && pageInstance != null) {
            try {
                Thread.sleep(2000); // Wait 2 seconds before closing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Close browser resources for this thread
        if (pageInstance != null) {
            pageInstance.close();
            threadLocalPage.remove();
        }
        
        if (contextInstance != null) {
            contextInstance.close();
            threadLocalContext.remove();
        }
        
        Browser browserInstance = threadLocalBrowser.get();
        if (browserInstance != null) {
            browserInstance.close();
            threadLocalBrowser.remove();
        }
        
        Playwright playwrightInstance = threadLocalPlaywright.get();
        if (playwrightInstance != null) {
            playwrightInstance.close();
            threadLocalPlaywright.remove();
        }
        
        // Clear legacy fields
        this.page = null;
        this.context = null;
        this.browser = null;
        this.playwright = null;
    }
    
    /**
     * Navigate to application URL
     */
    protected void navigateToApp() {
        Page pageInstance = threadLocalPage.get();
        if (pageInstance != null) {
            pageInstance.navigate(ConfigManager.getAppUrl());
        }
    }
    
    /**
     * Take and save screenshot
     */
    protected void attachScreenshot(String name) {
        Page pageInstance = threadLocalPage.get();
        if (pageInstance != null) {
            pageInstance.screenshot();
            System.out.println("[Screenshot] " + name);
        }
    }
}
