package com.elitea.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Custom wait utilities for smart, reliable test waits
 * Replaces hardcoded page.waitForTimeout() with intelligent waiting strategies
 */
public class WaitUtils {
    
    private static final int DEFAULT_TIMEOUT_MS = 30000;
    private static final int DEFAULT_POLL_INTERVAL_MS = 500;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    /**
     * Wait for page to be fully loaded (DOM + Network)
     */
    public static void waitForPageReady(Page page) {
        waitForPageReady(page, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for page to be fully loaded with custom timeout
     */
    public static void waitForPageReady(Page page, int timeoutMs) {
        logWait("Wait for page ready (DOM + Network)");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED, 
            new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
        page.waitForLoadState(LoadState.NETWORKIDLE, 
            new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
    }
    
    /**
     * Wait for network to be idle (no network activity for at least 500ms)
     */
    public static void waitForNetworkIdle(Page page) {
        waitForNetworkIdle(page, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for network to be idle with custom timeout
     */
    public static void waitForNetworkIdle(Page page, int timeoutMs) {
        logWait("Wait for network idle");
        page.waitForLoadState(LoadState.NETWORKIDLE, 
            new Page.WaitForLoadStateOptions().setTimeout(timeoutMs));
    }
    
    /**
     * Wait for element to be stable (not moving/changing)
     * Useful for animations, dynamic content
     */
    public static void waitForElementToBeStable(Locator locator) {
        waitForElementToBeStable(locator, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for element to be stable with custom timeout
     */
    public static void waitForElementToBeStable(Locator locator, int timeoutMs) {
        logWait("Wait for element to be stable");
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutMs));
        
        // Wait for element position to stabilize (no movement for 200ms)
        long startTime = System.currentTimeMillis();
        int stableCount = 0;
        int requiredStableChecks = 3;
        
        String lastBoundingBox = "";
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                String currentBoundingBox = locator.boundingBox().toString();
                if (currentBoundingBox.equals(lastBoundingBox)) {
                    stableCount++;
                    if (stableCount >= requiredStableChecks) {
                        return; // Element is stable
                    }
                } else {
                    stableCount = 0;
                }
                lastBoundingBox = currentBoundingBox;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted", e);
            }
        }
        logWait("Element stabilized after position checks");
    }
    
    /**
     * Wait for text to appear on page
     */
    public static void waitForTextToAppear(Page page, String text) {
        waitForTextToAppear(page, text, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for text to appear on page with custom timeout
     */
    public static void waitForTextToAppear(Page page, String text, int timeoutMs) {
        logWait("Wait for text to appear: " + text);
        page.getByText(text).first().waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutMs));
    }
    
    /**
     * Wait for text to disappear from page
     */
    public static void waitForTextToDisappear(Page page, String text) {
        waitForTextToDisappear(page, text, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for text to disappear from page with custom timeout
     */
    public static void waitForTextToDisappear(Page page, String text, int timeoutMs) {
        logWait("Wait for text to disappear: " + text);
        try {
            page.getByText(text).first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(timeoutMs));
        } catch (Exception e) {
            // Text already gone - this is success
            logWait("Text not found (already disappeared): " + text);
        }
    }
    
    /**
     * Wait for element to be visible and clickable
     */
    public static void waitForElementClickable(Locator locator) {
        waitForElementClickable(locator, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Wait for element to be visible and clickable with custom timeout
     */
    public static void waitForElementClickable(Locator locator, int timeoutMs) {
        logWait("Wait for element to be clickable");
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutMs));
        
        // Ensure element is enabled
        waitUntil(() -> {
            try {
                return locator.isEnabled();
            } catch (Exception e) {
                return false;
            }
        }, timeoutMs, "Element to be enabled");
    }
    
    /**
     * Wait for AJAX/XHR requests to complete
     * Monitors network for specific duration with no new requests
     */
    public static void waitForAjaxComplete(Page page) {
        waitForAjaxComplete(page, 1000);
    }
    
    /**
     * Wait for AJAX/XHR requests to complete with custom quiet period
     */
    public static void waitForAjaxComplete(Page page, int quietPeriodMs) {
        logWait("Wait for AJAX requests to complete");
        
        // Track pending requests
        final int[] pendingRequests = {0};
        final long[] lastRequestTime = {System.currentTimeMillis()};
        
        page.onRequest(request -> {
            if (request.resourceType().equals("xhr") || request.resourceType().equals("fetch")) {
                pendingRequests[0]++;
                lastRequestTime[0] = System.currentTimeMillis();
            }
        });
        
        page.onResponse(response -> {
            if (response.request().resourceType().equals("xhr") || 
                response.request().resourceType().equals("fetch")) {
                pendingRequests[0] = Math.max(0, pendingRequests[0] - 1);
                lastRequestTime[0] = System.currentTimeMillis();
            }
        });
        
        // Wait for quiet period (no requests for specified duration)
        waitUntil(() -> {
            long timeSinceLastRequest = System.currentTimeMillis() - lastRequestTime[0];
            return pendingRequests[0] == 0 && timeSinceLastRequest >= quietPeriodMs;
        }, DEFAULT_TIMEOUT_MS, "AJAX requests to complete");
    }
    
    /**
     * Wait for animation to complete
     * Waits for element's computed style to stabilize
     */
    public static void waitForAnimationComplete(Locator locator) {
        waitForAnimationComplete(locator, 2000);
    }
    
    /**
     * Wait for animation to complete with custom timeout
     */
    public static void waitForAnimationComplete(Locator locator, int timeoutMs) {
        logWait("Wait for animation to complete");
        
        // Wait for element to be visible first
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutMs));
        
        // Wait for position and size to stabilize
        waitForElementToBeStable(locator, timeoutMs);
    }
    
    /**
     * Generic wait until condition is true with polling
     */
    public static void waitUntil(BooleanSupplier condition, int timeoutMs, String description) {
        logWait("Wait until: " + description);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;
        
        while (System.currentTimeMillis() < endTime) {
            try {
                if (condition.getAsBoolean()) {
                    logWait("Condition met: " + description);
                    return;
                }
                Thread.sleep(DEFAULT_POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Wait interrupted: " + description, e);
            }
        }
        
        throw new RuntimeException("Timeout waiting for: " + description + 
            " (waited " + timeoutMs + "ms)");
    }
    
    /**
     * Retry an action with exponential backoff
     * Useful for flaky operations like API calls, file uploads
     */
    public static <T> T retryWithExponentialBackoff(Supplier<T> action, String actionDescription) {
        return retryWithExponentialBackoff(action, actionDescription, MAX_RETRY_ATTEMPTS);
    }
    
    /**
     * Retry an action with exponential backoff and custom max attempts
     */
    public static <T> T retryWithExponentialBackoff(Supplier<T> action, String actionDescription, int maxAttempts) {
        int attempt = 0;
        long delay = 1000; // Start with 1 second
        
        while (attempt < maxAttempts) {
            try {
                attempt++;
                logWait("Attempt " + attempt + "/" + maxAttempts + ": " + actionDescription);
                return action.get();
            } catch (Exception e) {
                if (attempt >= maxAttempts) {
                    logWait("All retry attempts failed for: " + actionDescription);
                    throw new RuntimeException(
                        "Failed after " + maxAttempts + " attempts: " + actionDescription, e);
                }
                
                logWait("Attempt " + attempt + " failed, retrying in " + delay + "ms: " + e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                
                // Exponential backoff: 1s, 2s, 4s, 8s, etc.
                delay *= 2;
            }
        }
        
        throw new RuntimeException("Retry logic failed unexpectedly");
    }
    
    /**
     * Retry a void action with exponential backoff
     */
    public static void retryVoidWithExponentialBackoff(Runnable action, String actionDescription) {
        retryVoidWithExponentialBackoff(action, actionDescription, MAX_RETRY_ATTEMPTS);
    }
    
    /**
     * Retry a void action with exponential backoff and custom max attempts
     */
    public static void retryVoidWithExponentialBackoff(Runnable action, String actionDescription, int maxAttempts) {
        retryWithExponentialBackoff(() -> {
            action.run();
            return null;
        }, actionDescription, maxAttempts);
    }
    
    /**
     * Wait for specific number of elements to be visible
     */
    public static void waitForElementCount(Locator locator, int expectedCount, int timeoutMs) {
        logWait("Wait for " + expectedCount + " elements to be visible");
        
        waitUntil(() -> {
            try {
                return locator.count() == expectedCount;
            } catch (Exception e) {
                return false;
            }
        }, timeoutMs, expectedCount + " elements to appear");
    }
    
    /**
     * Wait for element attribute to have specific value
     */
    public static void waitForAttributeValue(Locator locator, String attribute, String expectedValue, int timeoutMs) {
        logWait("Wait for attribute '" + attribute + "' to be '" + expectedValue + "'");
        
        waitUntil(() -> {
            try {
                String actualValue = locator.getAttribute(attribute);
                return expectedValue.equals(actualValue);
            } catch (Exception e) {
                return false;
            }
        }, timeoutMs, "Attribute '" + attribute + "' to be '" + expectedValue + "'");
    }
    
    /**
     * Smart wait - combines multiple wait strategies
     * Waits for: DOM ready -> Network idle -> Element visible -> Element stable
     */
    public static void smartWait(Page page, Locator locator) {
        smartWait(page, locator, DEFAULT_TIMEOUT_MS);
    }
    
    /**
     * Smart wait with custom timeout
     */
    public static void smartWait(Page page, Locator locator, int timeoutMs) {
        logWait("Smart wait - combining multiple wait strategies");
        
        // 1. Wait for page ready
        waitForPageReady(page, timeoutMs);
        
        // 2. Wait for element visible
        locator.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(timeoutMs));
        
        // 3. Wait for element stable
        waitForElementToBeStable(locator, timeoutMs);
        
        logWait("Smart wait completed successfully");
    }
    
    /**
     * Wait for URL to contain specific text
     */
    public static void waitForUrlContains(Page page, String urlPart, int timeoutMs) {
        logWait("Wait for URL to contain: " + urlPart);
        
        waitUntil(() -> page.url().contains(urlPart), 
            timeoutMs, "URL to contain '" + urlPart + "'");
    }
    
    /**
     * Wait for URL to match pattern
     */
    public static void waitForUrlMatches(Page page, String pattern, int timeoutMs) {
        logWait("Wait for URL to match pattern: " + pattern);
        
        page.waitForURL(pattern, new Page.WaitForURLOptions().setTimeout(timeoutMs));
    }
    
    /**
     * Fluent wait - poll until condition is true or timeout
     */
    public static void fluentWait(BooleanSupplier condition, int timeoutMs, int pollIntervalMs, String description) {
        logWait("Fluent wait: " + description);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeoutMs;
        
        while (System.currentTimeMillis() < endTime) {
            try {
                if (condition.getAsBoolean()) {
                    logWait("Fluent wait condition met: " + description);
                    return;
                }
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Fluent wait interrupted: " + description, e);
            }
        }
        
        throw new RuntimeException("Fluent wait timeout: " + description + 
            " (waited " + timeoutMs + "ms with " + pollIntervalMs + "ms polling)");
    }
    
    /**
     * Wait with custom error message
     */
    public static void waitWithMessage(BooleanSupplier condition, int timeoutMs, String successMessage, String errorMessage) {
        try {
            waitUntil(condition, timeoutMs, successMessage);
            logWait(successMessage);
        } catch (Exception e) {
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    /**
     * Log wait operation with timestamp
     */
    private static void logWait(String message) {
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println(String.format("[%s] WAIT %s", timestamp, message));
    }
}
