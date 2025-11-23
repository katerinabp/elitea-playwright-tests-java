package com.elitea.utils;

import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Manual authentication setup utility.
 * Run this class to manually login and save authentication state.
 * 
 * Usage: Right-click this file and select "Run 'ManualAuthSetup.main()'"
 */
public class ManualAuthSetup {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            System.out.println("Starting browser for manual login...");
            
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
            );
            
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            System.out.println("Navigating to chat page...");
            page.navigate("https://next.elitea.ai/alita_ui/chat");
            
            System.out.println("\n===========================================");
            System.out.println("PLEASE LOG IN MANUALLY IN THE BROWSER WINDOW");
            System.out.println("Steps:");
            System.out.println("1. Click EPAM SSO");
            System.out.println("2. Complete MetaDefender compliance check (wait ~30 seconds)");
            System.out.println("3. Wait until you see the chat interface with your profile");
            System.out.println("4. Then press ENTER in this terminal");
            System.out.println("===========================================\n");
            
            // Wait for user to press Enter
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Press ENTER after you've logged in successfully: ");
                scanner.nextLine();
            }
            
            System.out.println("Saving authentication state...");
            context.storageState(new BrowserContext.StorageStateOptions()
                .setPath(Paths.get("playwright/.auth/state.json")));
            
            System.out.println("[SUCCESS] Authentication state saved to playwright/.auth/state.json");
            
            browser.close();
            System.out.println("Done! You can now run tests with stored authentication.");
            System.out.println("\nTo run tests:");
            System.out.println("  ./gradlew test --tests \"P0_CreateNewConversationTest\" -Dheadless=false");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
