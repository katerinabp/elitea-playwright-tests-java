# Configuration Guide

## Overview

The EliteA test framework uses a centralized configuration system via `ConfigManager` class. Configuration is split into two files:

1. **config.properties** - Non-sensitive configuration (committed to git)
2. **test.properties** - Sensitive data like credentials (NOT committed to git)

## Setup Instructions

### 1. Create test.properties File

Create a file at `src/test/resources/test.properties` with your credentials:

```properties
# Authentication Configuration
test.pin=YOUR_PIN_HERE
test.user=your.email@epam.com
```

**Important:** This file is in .gitignore and will NOT be committed to version control.

### 2. Verify config.properties

The main configuration file `src/test/resources/config.properties` contains all non-sensitive settings.

## Using ConfigManager in Tests

### Basic Usage

```java
import com.elitea.config.ConfigManager;

public class MyTest {
    @Test
    void myTest() {
        // Get application URL
        String url = ConfigManager.getAppUrl();
        
        // Get authentication PIN
        String pin = ConfigManager.getTestPin();
        
        // Get user email
        String user = ConfigManager.getTestUser();
        
        // Get browser settings
        boolean headless = ConfigManager.isHeadless();
        int timeout = ConfigManager.getTimeout();
    }
}
```

### Available Methods

#### Application Configuration
```java
ConfigManager.getAppUrl()          // Returns base application URL
ConfigManager.getTimeout()         // Returns default timeout in milliseconds
ConfigManager.isHeadless()         // Returns true if headless mode enabled
```

#### Authentication Configuration
```java
ConfigManager.getTestPin()         // Returns authentication PIN (throws exception if not set)
ConfigManager.getTestUser()        // Returns test user email
```

#### Browser Configuration
```java
ConfigManager.getBrowserType()     // Returns browser type (chromium, firefox, webkit)
ConfigManager.getBrowserWidth()    // Returns browser viewport width
ConfigManager.getBrowserHeight()   // Returns browser viewport height
```

#### Playwright Configuration
```java
ConfigManager.getSlowMo()          // Returns slow motion delay in ms
ConfigManager.getVideoMode()       // Returns video recording mode
ConfigManager.getScreenshotMode()  // Returns screenshot capture mode
ConfigManager.getTraceMode()       // Returns trace recording mode
```

#### Test Configuration
```java
ConfigManager.isParallelEnabled()  // Returns true if parallel execution enabled
ConfigManager.getParallelThreads() // Returns number of parallel threads
ConfigManager.getRetryCount()      // Returns test retry count
```

### Custom Properties

For properties not covered by helper methods, use the generic getter:

```java
String value = ConfigManager.getProperty("my.custom.property");
String valueWithDefault = ConfigManager.getProperty("my.property", "defaultValue");
```

## Security Best Practices

1. **Never commit test.properties** - It's already in .gitignore
2. **Use environment variables for CI/CD** - Override properties using system properties:
   ```bash
   ./gradlew test -Dtest.pin=$SECRET_PIN -Dtest.user=$TEST_USER
   ```
3. **Document required properties** - Keep this guide updated with any new credentials needed
4. **Share test.properties.example** - Create a template file for new team members:
   ```properties
   # test.properties.example
   # Copy this file to test.properties and fill in your credentials
   test.pin=YOUR_PIN_HERE
   test.user=your.email@epam.com
   ```

## Example: Updating a Test to Use ConfigManager

**Before:**
```java
@Test
void testLogin() {
    page.navigate("https://next.elitea.ai/alita_ui/chat");
    // Hardcoded values
}
```

**After:**
```java
@Test
void testLogin() {
    page.navigate(ConfigManager.getAppUrl());
    String pin = ConfigManager.getTestPin();
    String user = ConfigManager.getTestUser();
    // Use configuration values
}
```

## Troubleshooting

### Error: "Test PIN not configured"

**Cause:** test.properties file is missing or doesn't contain `test.pin` property.

**Solution:** Create `src/test/resources/test.properties` with:
```properties
test.pin=091083
test.user=katerina_pikulik@epam.com
```

### Warning: "test.properties not found"

**Cause:** The file doesn't exist in `src/test/resources/`.

**Solution:** Create the file as described above. Tests will continue but authentication will fail.

### Property Override Not Working

**Cause:** System properties take precedence but might be misspelled.

**Solution:** Use exact property names from config files:
```bash
./gradlew test -Dtest.pin=123456  # Correct
./gradlew test -DtestPin=123456   # Wrong - won't work
```

## CI/CD Integration

For GitHub Actions or other CI systems, set properties as secrets:

```yaml
- name: Run Tests
  env:
    TEST_PIN: ${{ secrets.TEST_PIN }}
    TEST_USER: ${{ secrets.TEST_USER }}
  run: ./gradlew test -Dtest.pin=$TEST_PIN -Dtest.user=$TEST_USER
```

## Additional Resources

- Main configuration: `src/test/resources/config.properties`
- ConfigManager source: `src/main/java/com/elitea/config/ConfigManager.java`
- Test plan: `test-cases/EliteA-Chat-Test-Plan-Updated.md`
