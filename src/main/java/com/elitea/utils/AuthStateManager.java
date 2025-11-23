package com.elitea.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

/**
 * Utility class for managing authentication state
 */
public class AuthStateManager {
    
    private static final String STATE_FILE = "playwright/.auth/state.json";
    private static final long MAX_AGE_HOURS = 24; // Session expires after 24 hours
    
    /**
     * Check if authentication state file exists
     */
    public static boolean authStateExists() {
        return Files.exists(getStatePath());
    }
    
    /**
     * Check if authentication state is valid (exists and not expired)
     */
    public static boolean isAuthStateValid() {
        if (!authStateExists()) {
            return false;
        }
        
        try {
            FileTime lastModified = Files.getLastModifiedTime(getStatePath());
            long fileAgeMillis = System.currentTimeMillis() - lastModified.toMillis();
            long maxAgeMillis = MAX_AGE_HOURS * 60 * 60 * 1000;
            
            return fileAgeMillis < maxAgeMillis;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Get the age of authentication state file in hours
     */
    public static long getAuthStateAgeHours() {
        if (!authStateExists()) {
            return -1;
        }
        
        try {
            FileTime lastModified = Files.getLastModifiedTime(getStatePath());
            long fileAgeMillis = System.currentTimeMillis() - lastModified.toMillis();
            return fileAgeMillis / (60 * 60 * 1000);
        } catch (IOException e) {
            return -1;
        }
    }
    
    /**
     * Get the path to authentication state file
     */
    public static Path getStatePath() {
        return Paths.get(STATE_FILE);
    }
    
    /**
     * Delete authentication state file
     */
    public static boolean deleteAuthState() {
        try {
            if (authStateExists()) {
                Files.delete(getStatePath());
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Print authentication state status
     */
    public static void printAuthStatus() {
        if (!authStateExists()) {
            System.err.println("❌ Authentication state not found");
            System.err.println("   Run: ./gradlew authSetup");
        } else if (!isAuthStateValid()) {
            long ageHours = getAuthStateAgeHours();
            System.err.println("⚠️  Authentication state expired");
            System.err.println("   Age: " + ageHours + " hours (max: " + MAX_AGE_HOURS + " hours)");
            System.err.println("   Run: ./gradlew authSetup");
        } else {
            long ageHours = getAuthStateAgeHours();
            System.out.println("✓ Authentication state valid");
            System.out.println("  Age: " + ageHours + " hours (expires in " + 
                             (MAX_AGE_HOURS - ageHours) + " hours)");
        }
    }
    
    /**
     * Main method for CLI usage
     */
    public static void main(String[] args) {
        printAuthStatus();
    }
}
