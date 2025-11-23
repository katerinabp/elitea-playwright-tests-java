# 2FA Testing Strategy for EliteA E2E Tests

## Overview
This document outlines the strategy for handling Two-Factor Authentication (2FA) in automated tests.

## Approach: Global Authentication Setup with State Reuse

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│  1. One-Time Manual Login (Global Setup)                    │
│     - Run once before test suite                            │
│     - Complete 2FA manually                                 │
│     - Save authentication state (cookies, localStorage)     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  2. All Tests Load Saved State                              │
│     - Each test starts with authenticated session           │
│     - No repeated logins or 2FA prompts                     │
│     - Tests run in parallel (if needed)                     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  3. State Refresh (When Expired)                            │
│     - Detect expired session                                │
│     - Re-run global setup                                   │
│     - Update saved state                                    │
└─────────────────────────────────────────────────────────────┘
```

### Benefits
- ✅ **No 2FA interruption during test runs** - Complete 2FA once, reuse for all tests
- ✅ **Fast test execution** - Skip login flow for every test
- ✅ **Parallel execution safe** - Each test gets its own context with shared auth
- ✅ **CI/CD friendly** - Save state as artifact, reuse across builds
- ✅ **Session management** - Easy to detect and refresh expired sessions

## Implementation

### 1. Global Setup (One-Time Login)

**File**: `src/test/java/com/elitea/setup/AuthSetup.java`

Run manually when:
- Starting test development
- Authentication state expires
- Switching test environments
- After credential changes

**Command**:
```bash
./gradlew authSetup
```

This will:
1. Open browser (headed mode)
2. Navigate to login page
3. Wait for you to complete 2FA manually
4. Save authentication state to `playwright/.auth/state.json`
5. Close browser

### 2. Test Execution with Saved State

**File**: `src/test/java/com/elitea/base/BaseTest.java`

All tests extend `BaseTest` which:
1. Loads saved authentication state before each test
2. Creates isolated browser context with authenticated session
3. Tests start on authenticated pages immediately

**Command**:
```bash
./gradlew test              # Run all tests
./gradlew test --tests "*LoginTest*"  # Run specific tests
```

### 3. Session Expiration Handling

If authentication expires during test execution:

**Option A: Automatic Detection**
```java
// Add to BaseTest.setUp()
if (isAuthenticationExpired()) {
    throw new RuntimeException("Authentication expired. Run: ./gradlew authSetup");
}
```

**Option B: Graceful Fallback**
```java
// Tests can detect and skip if auth fails
if (!isAuthenticated()) {
    Assumptions.assumeTrue(false, "Skipping test - authentication required");
}
```

### 4. CI/CD Integration

#### For GitHub Actions / Jenkins:

**Step 1**: Generate auth state locally (with 2FA)
```bash
./gradlew authSetup
```

**Step 2**: Commit state file (if using long-lived sessions)
```bash
git add playwright/.auth/state.json
git commit -m "Update auth state"
```

**OR**

**Step 2**: Store as CI secret/artifact
```yaml
# GitHub Actions example
- name: Restore auth state
  uses: actions/cache@v3
  with:
    path: playwright/.auth/state.json
    key: auth-state-${{ hashFiles('**/package-lock.json') }}
```

**Step 3**: Run tests in CI
```bash
./gradlew test
```

## Alternative Approaches (Not Recommended)

### ❌ Approach 1: 2FA on Every Test
**Problem**: Impossible to automate, requires manual intervention

### ❌ Approach 2: Disable 2FA in Test Environment
**Problem**: Security risk, doesn't test real authentication flow

### ❌ Approach 3: TOTP Automation
**Problem**: 
- Requires sharing TOTP secrets
- Security concerns
- Fragile implementation
- Not supported by all 2FA systems

### ❌ Approach 4: Browser Profile Reuse
**Problem**:
- Not isolated between tests
- State pollution
- Harder to parallelize

## Best Practices

### 1. State File Management
```bash
# .gitignore - Don't commit sensitive auth state
playwright/.auth/*.json

# Exception: Can commit for CI if using short-lived tokens
!playwright/.auth/state.json  # If absolutely needed
```

### 2. State Validation
Add helper to check if state is valid:
```java
public static boolean isAuthStateValid() {
    Path statePath = Paths.get("playwright/.auth/state.json");
    if (!Files.exists(statePath)) return false;
    
    // Check file age (expire after 24 hours)
    long fileAge = System.currentTimeMillis() - 
                   Files.getLastModifiedTime(statePath).toMillis();
    return fileAge < 24 * 60 * 60 * 1000;
}
```

### 3. Environment-Specific States
```bash
playwright/.auth/
  ├── state.dev.json      # Development environment
  ├── state.staging.json  # Staging environment
  └── state.prod.json     # Production environment
```

### 4. Team Sharing
**Option 1**: Shared credentials
- One team member generates state
- Shares via secure channel (encrypted file, secrets manager)
- Team loads same state

**Option 2**: Individual credentials
- Each developer runs `authSetup` with their own credentials
- State file is gitignored
- CI uses service account credentials

## Troubleshooting

### "Authentication state not found"
**Solution**: Run `./gradlew authSetup`

### "Session expired" errors during tests
**Solution**: 
1. Re-run `./gradlew authSetup`
2. Check session duration in application
3. Consider implementing auto-refresh logic

### Tests fail with "Not authenticated"
**Solution**:
1. Verify `playwright/.auth/state.json` exists
2. Check file contents (should have cookies and localStorage)
3. Re-run auth setup
4. Verify BaseTest loads state correctly

### 2FA prompt appears during test
**Solution**:
- State file is missing or invalid
- Session expired
- Application requires re-authentication
- Run `./gradlew authSetup` again

## File Locations

```
elitea_E2Etests_java/
├── src/test/java/com/elitea/
│   ├── setup/
│   │   └── AuthSetup.java          # Global auth setup
│   ├── base/
│   │   └── BaseTest.java           # Loads saved state
│   └── tests/
│       └── *Test.java              # All tests extend BaseTest
├── playwright/.auth/
│   └── state.json                  # Saved authentication state
└── build.gradle                    # Task: authSetup
```

## Summary

This approach provides:
- **Zero 2FA interruption** during test execution
- **Fast, parallelizable tests**
- **CI/CD compatibility**
- **Simple maintenance** (refresh state when expired)

The key is: **Authenticate once, test many times** 🚀
