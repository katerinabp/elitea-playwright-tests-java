package com.elitea.utils;

import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * Retry utilities for handling flaky operations
 * Provides multiple retry strategies with configurable behavior
 */
public class RetryUtils {
    
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_INITIAL_DELAY_MS = 1000;
    private static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0;
    
    /**
     * Retry configuration builder
     */
    public static class RetryConfig {
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private long initialDelayMs = DEFAULT_INITIAL_DELAY_MS;
        private double backoffMultiplier = DEFAULT_BACKOFF_MULTIPLIER;
        private long maxDelayMs = 30000; // 30 seconds max
        private boolean logAttempts = true;
        private Class<? extends Exception>[] retryableExceptions = null;
        private Consumer<Exception> onRetry = null;
        private Consumer<Exception> onFailure = null;
        
        public RetryConfig maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }
        
        public RetryConfig initialDelay(long delayMs) {
            this.initialDelayMs = delayMs;
            return this;
        }
        
        public RetryConfig backoffMultiplier(double multiplier) {
            this.backoffMultiplier = multiplier;
            return this;
        }
        
        public RetryConfig maxDelay(long maxDelayMs) {
            this.maxDelayMs = maxDelayMs;
            return this;
        }
        
        public RetryConfig logging(boolean enabled) {
            this.logAttempts = enabled;
            return this;
        }
        
        @SafeVarargs
        public final RetryConfig retryOn(Class<? extends Exception>... exceptions) {
            this.retryableExceptions = exceptions;
            return this;
        }
        
        public RetryConfig onRetry(Consumer<Exception> callback) {
            this.onRetry = callback;
            return this;
        }
        
        public RetryConfig onFailure(Consumer<Exception> callback) {
            this.onFailure = callback;
            return this;
        }
    }
    
    /**
     * Create a new retry configuration
     */
    public static RetryConfig config() {
        return new RetryConfig();
    }
    
    /**
     * Retry with exponential backoff using default config
     */
    public static <T> T retry(Supplier<T> action, String description) {
        return retry(action, description, config());
    }
    
    /**
     * Retry with exponential backoff using custom config
     */
    public static <T> T retry(Supplier<T> action, String description, RetryConfig config) {
        int attempt = 0;
        long delay = config.initialDelayMs;
        Exception lastException = null;
        
        while (attempt < config.maxAttempts) {
            attempt++;
            
            try {
                if (config.logAttempts) {
                    log("Attempt " + attempt + "/" + config.maxAttempts + ": " + description);
                }
                
                T result = action.get();
                
                if (config.logAttempts && attempt > 1) {
                    log("✓ Success on attempt " + attempt + ": " + description);
                }
                
                return result;
                
            } catch (Exception e) {
                lastException = e;
                
                // Check if this exception is retryable
                if (config.retryableExceptions != null && !isRetryableException(e, config.retryableExceptions)) {
                    if (config.logAttempts) {
                        log("✗ Non-retryable exception, aborting: " + e.getClass().getSimpleName());
                    }
                    break;
                }
                
                if (attempt >= config.maxAttempts) {
                    if (config.logAttempts) {
                        log("✗ All " + config.maxAttempts + " attempts failed: " + description);
                    }
                    if (config.onFailure != null) {
                        config.onFailure.accept(e);
                    }
                    break;
                }
                
                if (config.logAttempts) {
                    log("⚠ Attempt " + attempt + " failed, retrying in " + delay + "ms: " + e.getMessage());
                }
                
                if (config.onRetry != null) {
                    config.onRetry.accept(e);
                }
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                
                // Calculate next delay with exponential backoff
                delay = Math.min((long)(delay * config.backoffMultiplier), config.maxDelayMs);
            }
        }
        
        throw new RetryException(
            "Failed after " + config.maxAttempts + " attempts: " + description, 
            lastException);
    }
    
    /**
     * Retry void action with exponential backoff
     */
    public static void retryVoid(Runnable action, String description) {
        retryVoid(action, description, config());
    }
    
    /**
     * Retry void action with custom config
     */
    public static void retryVoid(Runnable action, String description, RetryConfig config) {
        retry(() -> {
            action.run();
            return null;
        }, description, config);
    }
    
    /**
     * Retry with fixed delay (no exponential backoff)
     */
    public static <T> T retryWithFixedDelay(Supplier<T> action, String description, int maxAttempts, long delayMs) {
        RetryConfig config = config()
            .maxAttempts(maxAttempts)
            .initialDelay(delayMs)
            .backoffMultiplier(1.0); // No exponential growth
        
        return retry(action, description, config);
    }
    
    /**
     * Retry with linear backoff
     */
    public static <T> T retryWithLinearBackoff(Supplier<T> action, String description, int maxAttempts, long incrementMs) {
        int attempt = 0;
        long delay = incrementMs;
        Exception lastException = null;
        
        while (attempt < maxAttempts) {
            attempt++;
            
            try {
                log("Attempt " + attempt + "/" + maxAttempts + ": " + description);
                return action.get();
                
            } catch (Exception e) {
                lastException = e;
                
                if (attempt >= maxAttempts) {
                    log("✗ All " + maxAttempts + " attempts failed: " + description);
                    break;
                }
                
                log("⚠ Attempt " + attempt + " failed, retrying in " + delay + "ms");
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                
                // Linear backoff: delay increases by fixed increment
                delay += incrementMs;
            }
        }
        
        throw new RetryException("Failed after " + maxAttempts + " attempts: " + description, lastException);
    }
    
    /**
     * Retry with jitter to avoid thundering herd
     */
    public static <T> T retryWithJitter(Supplier<T> action, String description, int maxAttempts) {
        RetryConfig config = config()
            .maxAttempts(maxAttempts)
            .onRetry(e -> {
                // Add random jitter to delay
                try {
                    long jitter = (long)(Math.random() * 500); // 0-500ms random jitter
                    Thread.sleep(jitter);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            });
        
        return retry(action, description, config);
    }
    
    /**
     * Retry only on specific exceptions
     */
    @SafeVarargs
    public static <T> T retryOn(Supplier<T> action, String description, Class<? extends Exception>... exceptions) {
        RetryConfig config = config().retryOn(exceptions);
        return retry(action, description, config);
    }
    
    /**
     * Immediate retry (no delay, useful for quick operations)
     */
    public static <T> T retryImmediately(Supplier<T> action, String description, int maxAttempts) {
        RetryConfig config = config()
            .maxAttempts(maxAttempts)
            .initialDelay(0)
            .backoffMultiplier(1.0);
        
        return retry(action, description, config);
    }
    
    /**
     * Retry with timeout - abort if total time exceeds limit
     */
    public static <T> T retryWithTimeout(Supplier<T> action, String description, long totalTimeoutMs) {
        long startTime = System.currentTimeMillis();
        int attempt = 0;
        long delay = DEFAULT_INITIAL_DELAY_MS;
        Exception lastException = null;
        
        while (System.currentTimeMillis() - startTime < totalTimeoutMs) {
            attempt++;
            
            try {
                log("Attempt " + attempt + " (within " + totalTimeoutMs + "ms timeout): " + description);
                return action.get();
                
            } catch (Exception e) {
                lastException = e;
                
                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = totalTimeoutMs - elapsed;
                
                if (remaining <= 0) {
                    log("✗ Timeout exceeded (" + totalTimeoutMs + "ms): " + description);
                    break;
                }
                
                long actualDelay = Math.min(delay, remaining);
                log("⚠ Attempt " + attempt + " failed, retrying in " + actualDelay + "ms");
                
                try {
                    Thread.sleep(actualDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
                
                delay = (long)(delay * DEFAULT_BACKOFF_MULTIPLIER);
            }
        }
        
        throw new RetryException(
            "Failed within timeout of " + totalTimeoutMs + "ms after " + attempt + " attempts: " + description, 
            lastException);
    }
    
    /**
     * Check if exception is retryable
     */
    private static boolean isRetryableException(Exception e, Class<? extends Exception>[] retryableExceptions) {
        for (Class<? extends Exception> exceptionClass : retryableExceptions) {
            if (exceptionClass.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Custom exception for retry failures
     */
    public static class RetryException extends RuntimeException {
        public RetryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Log retry operation
     */
    private static void log(String message) {
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println(String.format("[%s] RETRY %s", timestamp, message));
    }
}
