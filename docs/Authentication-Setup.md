# Authentication Management for 2FA Testing

## Quick Start

### 1. First Time Setup (Complete 2FA Once)
```bash
./gradlew authSetup
```
- Browser will open
- Complete EPAM SSO login with 2FA manually
- Authentication state will be saved automatically
- Browser will close

### 2. Run Tests (No 2FA Required)
```bash
./gradlew test              # All tests
./gradlew test --tests "*P0*"  # Priority 0 tests only
```
All tests will use the saved authentication state - **no login required**!

### 3. Check Authentication Status
```bash
./gradlew authStatus
```
Shows if authentication is valid or expired.

### 4. Refresh Authentication (When Expired)
```bash
./gradlew authClean   # Optional: delete old state
./gradlew authSetup   # Login again with 2FA
```

## Available Gradle Tasks

| Task | Description | When to Use |
|------|-------------|-------------|
| `./gradlew authSetup` | Perform login and save auth state | First time, when expired, or switching accounts |
| `./gradlew authStatus` | Check if auth state is valid | Before running tests |
| `./gradlew authClean` | Delete saved auth state | To force fresh login |
| `./gradlew test` | Run all tests with saved auth | Anytime after authSetup |
| `./gradlew testWithAuth` | Run tests (validates auth first) | Production test runs |

## How It Works

### With 2FA Support:

```
┌──────────────────────────────────────────────────┐
│ Step 1: One-Time Setup (./gradlew authSetup)    │
├──────────────────────────────────────────────────┤
│ 1. Browser opens                                 │
│ 2. Navigate to login page                       │
│ 3. Click EPAM SSO                               │
│ 4. YOU: Enter email + password                  │
│ 5. YOU: Complete 2FA (SMS/App)                  │
│ 6. ✓ Redirected to chat page                    │
│ 7. Save cookies & storage → state.json          │
│ 8. Browser closes                               │
└──────────────────────────────────────────────────┘
                     ↓
                State Saved
                     ↓
┌──────────────────────────────────────────────────┐
│ Step 2: Run Tests (./gradlew test)              │
├──────────────────────────────────────────────────┤
│ For EACH test:                                   │
│   1. Load state.json                            │
│   2. Create browser context with auth           │
│   3. Navigate to chat page (already logged in!) │
│   4. Execute test steps                         │
│   5. Close browser                              │
│                                                  │
│ ✓ NO login prompts                              │
│ ✓ NO 2FA prompts                                │
│ ✓ Tests run independently                       │
└──────────────────────────────────────────────────┘
```

## Authentication State File

**Location**: `playwright/.auth/state.json`

**Contains**:
- Session cookies
- localStorage data
- sessionStorage data
- Other browser state

**Expires**: After 24 hours (configurable in `AuthStateManager.java`)

**Security**: 
- ⚠️ **Do NOT commit to git** (contains active session)
- Add to `.gitignore`
- Share securely if needed for CI/CD

## Troubleshooting

### ❌ "Authentication state not found"
**Cause**: No state.json file exists  
**Solution**: 
```bash
./gradlew authSetup
```

### ⚠️ "Authentication state expired"
**Cause**: state.json is older than 24 hours  
**Solution**:
```bash
./gradlew authSetup  # Login again
```

### ❌ Tests fail with "Not authenticated" errors
**Cause**: Session expired or invalid state  
**Solution**:
```bash
./gradlew authClean   # Delete old state
./gradlew authSetup   # Create new state
```

### 🔄 2FA prompt appears during authSetup
**This is expected!**  
- Complete 2FA manually in the browser
- State will be saved after successful login
- Future tests won't require 2FA

### 🌐 Browser doesn't show during tests
**This is normal when tests run fast**  
- Tests use saved auth state
- Browser opens and closes quickly
- To slow down and see browser:
  - Edit `test.properties`: set `slowMo=1000`
  - Edit `build.gradle`: add `Thread.sleep(5000)` in tests

## CI/CD Integration

### Option 1: Commit State (Short-lived sessions)
```bash
# Generate state
./gradlew authSetup

# Commit (if session tokens are short-lived)
git add playwright/.auth/state.json
git commit -m "Update auth state for CI"
git push
```

### Option 2: CI Secrets (Recommended)
```yaml
# .github/workflows/tests.yml
- name: Setup Auth
  run: |
    mkdir -p playwright/.auth
    echo "${{ secrets.AUTH_STATE }}" > playwright/.auth/state.json

- name: Run Tests
  run: ./gradlew test
```

### Option 3: Manual Login in CI (Headful)
Not recommended for 2FA - requires manual intervention.

## Best Practices

### ✅ DO:
- Run `authSetup` once before starting test development
- Check `authStatus` before long test runs
- Refresh auth state when expired
- Keep state.json secure (don't share publicly)
- Use environment-specific state files for different environments

### ❌ DON'T:
- Commit state.json to public repos
- Share state files insecurely
- Try to automate 2FA codes
- Run tests without valid auth state

## File Structure

```
elitea_E2Etests_java/
├── playwright/.auth/
│   └── state.json              ← Saved authentication (gitignored)
├── src/
│   ├── main/java/com/elitea/
│   │   └── utils/
│   │       └── AuthStateManager.java  ← Auth validation utilities
│   └── test/java/com/elitea/
│       ├── setup/
│       │   └── AuthSetup.java        ← Login and save state
│       ├── base/
│       │   └── BaseTest.java         ← Loads state for tests
│       └── tests/
│           └── *Test.java            ← All tests use saved auth
├── docs/
│   └── 2FA-Testing-Strategy.md       ← Detailed strategy docs
└── build.gradle                       ← Tasks: authSetup, authStatus, authClean
```

## Summary

**The Goal**: Complete 2FA once, run tests many times without interruption.

**The Flow**:
1. `./gradlew authSetup` → Complete 2FA manually → State saved
2. `./gradlew test` → All tests use saved state → No 2FA prompts
3. (When expired) → Repeat step 1

**Benefits**:
- ✅ No 2FA interruption during test runs
- ✅ Fast test execution
- ✅ Parallel execution safe
- ✅ CI/CD friendly

🚀 **You're ready to test with 2FA!**
