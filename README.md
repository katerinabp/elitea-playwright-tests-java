# Java Playwright Framework for Elitea Chat Testing

Automated testing framework for Elitea Chat application using Playwright for Java, Gradle, and JUnit 5.

## 🚀 Technology Stack

- **Java**: 21
- **Playwright**: 1.48.0
- **Build Tool**: Gradle 8.5
- **Test Framework**: JUnit 5.10.1
- **Reporting**: Allure 2.25.0
- **Assertions**: AssertJ 3.25.1
- **Logging**: Logback 1.4.14

## 📁 Project Structure

```
elitea_E2Etests_java/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/elitea/
│   │           ├── base/
│   │           │   └── BasePage.java          # Base page object
│   │           ├── config/
│   │           │   └── ConfigManager.java     # Configuration management
│   │           └── pages/
│   │               ├── LoginPage.java         # Login page object
│   │               └── ChatPage.java          # Chat page object
│   └── test/
│       ├── java/
│       │   └── com/elitea/
│       │       ├── base/
│       │       │   └── BaseTest.java          # Base test class
│       │       └── tests/
│       │           └── LoginTest.java         # Login test cases
│       └── resources/
│           └── config.properties              # Test configuration
├── build.gradle                               # Gradle build file
└── README.md                                  # This file
```

## 🛠️ Prerequisites

- Java 21 installed
- Gradle 8.5+ installed (or use wrapper)
- Git installed

## ⚙️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd elitea_E2Etests_java
```

### 2. Install Dependencies

```bash
# Using Gradle wrapper (recommended)
./gradlew build

# Or using installed Gradle
gradle build
```

### 3. Install Playwright Browsers

```bash
./gradlew installPlaywrightBrowsers
```

### 4. Configure Test Environment

Edit `src/test/resources/config.properties`:

```properties
# Application Configuration
app.url=https://next.elitea.ai/alita_ui/chat
app.timeout=30000
app.headless=false

# Browser Configuration
browser.type=chromium
browser.width=1920
browser.height=1080

# Playwright Configuration
playwright.slowmo=0
playwright.video=off
playwright.screenshot=only-on-failure
playwright.trace=retain-on-failure

# Test Configuration
test.parallel.enabled=true
test.parallel.threads=4
test.retry.count=0
```

## 🧪 Running Tests

### Basic Execution

#### Run All Tests (Default: Sequential, Headed)
```bash
./gradlew test
```

#### Run Tests in Parallel (Recommended for CI/CD)
```bash
# Fast parallel execution (headless)
./gradlew testParallel

# Run only critical P0 tests in parallel
./gradlew testP0

# Run smoke tests in parallel
./gradlew testSmoke
```

#### Run Tests Sequentially (Development)
```bash
# Sequential with browser UI visible
./gradlew testSequential

# Sequential with headless mode
./gradlew test -Dheadless=true
```

#### Compare Performance (Benchmark)
```bash
# Runs tests sequentially then in parallel, reports speed improvement
./gradlew testBenchmark
```

### Execution Modes

| Task | Mode | Headless | Speed | Use Case |
|------|------|----------|-------|----------|
| `testParallel` | Parallel | ✅ Yes | ⚡ Fastest | CI/CD pipelines |
| `testSequential` | Sequential | ❌ No | 🐢 Slowest | Local debugging |
| `testP0` | Parallel | ✅ Yes | ⚡ Fast | Critical tests only |
| `testSmoke` | Parallel | ✅ Yes | ⚡ Fast | Smoke testing |
| `testParallelHeaded` | Parallel | ❌ No | ⚡ Medium | Debugging parallel issues |
| `test` | Configurable | Configurable | Variable | Default task |

### Run with Specific Browser

```bash
./gradlew testParallel -Dbrowser=chromium
./gradlew testParallel -Dbrowser=firefox
./gradlew testParallel -Dbrowser=webkit
```

### Run Specific Test Class

```bash
# Parallel execution
./gradlew testParallel --tests "P0_SendSimpleTextMessageTest"

# Sequential execution
./gradlew testSequential --tests "LoginTest"
```

### Run Specific Test Method

```bash
./gradlew testParallel --tests "P0_SendSimpleTextMessageTest.testSendSimpleTextMessage"
```

### Custom Execution

```bash
# Parallel with custom thread count (fixed strategy)
./gradlew test -Dparallel=true -Dheadless=true

# Slow motion for debugging
./gradlew testSequential -DslowMo=500

# Custom browser and headless
./gradlew testParallel -Dbrowser=firefox -Dheadless=true
```

### Parallel Execution Details

The framework uses **JUnit 5 parallel execution** with thread-safe Playwright instances:

**Thread Pool Sizing:**
- **Dynamic (default)**: 1 thread per CPU core
- **Customizable**: Edit `src/test/resources/junit-platform.properties`

**Performance Benefits:**
- 50-70% reduction in total execution time
- Scalable to hundreds of tests
- No breaking changes to existing tests

**Configuration File:** `src/test/resources/junit-platform.properties`
```properties
# Enable parallel execution
junit.jupiter.execution.parallel.enabled = true

# Execute tests concurrently
junit.jupiter.execution.parallel.mode.default = concurrent

# Dynamic thread pool (1 thread per CPU core)
junit.jupiter.execution.parallel.config.strategy = dynamic
junit.jupiter.execution.parallel.config.dynamic.factor = 1.0
```

**For more details:** See [docs/Parallel-Execution-Implementation.md](docs/Parallel-Execution-Implementation.md)

## 📊 Test Reporting

### Allure Reports

Generate and view Allure report:

```bash
# Generate report
./gradlew allureReport

# Open report in browser
./gradlew allureServe
```

### Standard Test Report

After running tests, open:
```
build/reports/tests/test/index.html
```

## 🔧 Configuration Options

### config.properties Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `app.url` | Application URL | https://next.elitea.ai/alita_ui/chat |
| `app.timeout` | Default timeout (ms) | 30000 |
| `app.headless` | Run in headless mode | false |
| `browser.type` | Browser type (chromium/firefox/webkit) | chromium |
| `browser.width` | Browser width | 1920 |
| `browser.height` | Browser height | 1080 |
| `playwright.slowmo` | Slow down execution (ms) | 0 |
| `playwright.video` | Video recording (on/off/retain-on-failure) | off |
| `playwright.screenshot` | Screenshots (on/off/only-on-failure) | only-on-failure |
| `playwright.trace` | Tracing (on/off/retain-on-failure) | retain-on-failure |
| `test.parallel.enabled` | Enable parallel execution | true |
| `test.parallel.threads` | Number of parallel threads | 4 |
| `test.retry.count` | Retry failed tests | 0 |

### Override Properties via Command Line

```bash
./gradlew test -Dapp.url=https://staging.elitea.ai -Dbrowser.type=firefox
```

## 📝 Writing Tests

### Example Test

```java
@Test
@DisplayName("Test Description")
public void testName() {
    // Arrange
    LoginPage loginPage = new LoginPage(page);
    
    // Act
    loginPage.navigateToLoginPage(ConfigManager.getAppUrl());
    ChatPage chatPage = loginPage.login();
    
    // Assert
    assertThat(chatPage.isChatPageDisplayed()).isTrue();
}
```

### Page Object Pattern

```java
public class MyPage extends BasePage {
    private static final String ELEMENT = "selector";
    
    public MyPage(Page page) {
        super(page);
    }
    
    @Step("Perform action")
    public void performAction() {
        click(ELEMENT);
    }
}
```

## 🔐 Authentication

The framework supports **EPAM SSO** authentication. The login flow:

1. Navigate to application URL
2. Click EPAM IDP login button
3. EPAM SSO handles authentication (assumes credentials configured)
4. Redirects to chat page

**Note**: For CI/CD, ensure EPAM credentials are configured in the environment.

## 🐛 Debugging

### Enable Slow Motion

```bash
./gradlew test -Dplaywright.slowmo=500
```

### Enable Video Recording

```bash
./gradlew test -Dplaywright.video=on
```

### Enable Tracing

```bash
./gradlew test -Dplaywright.trace=on
```

### View Traces

Traces are saved to `test-results/traces/` and attached to Allure reports.

## 🚀 CI/CD Integration

The framework is ready for GitHub Actions integration. See `.github/workflows/` (to be created).

### Environment Variables

Set these secrets in GitHub:

- `JAVA_VERSION`: 21
- `APP_URL`: Application URL (optional, overrides config)
- `EPAM_USERNAME`: EPAM SSO username (if needed)
- `EPAM_PASSWORD`: EPAM SSO password (if needed)

## 📖 Best Practices

1. **Use Page Object Model** - Separate test logic from page interactions
2. **Use ConfigManager** - Externalize configuration
3. **Use Allure Annotations** - Enhance reporting with `@Step`, `@Description`, `@Epic`, `@Feature`
4. **Use AssertJ** - Fluent assertions with descriptive messages
5. **Handle Waits Properly** - Use Playwright's built-in waiting mechanisms
6. **Enable Tracing on Failure** - Helps debugging failed tests
7. **Run in Parallel** - Faster execution with `test.parallel.enabled=true`

## 🤝 Contributing

1. Create feature branch
2. Write tests following existing patterns
3. Ensure all tests pass
4. Submit pull request

## 📄 License

[Your License Here]

## 📞 Support

For issues or questions, contact [Your Team/Email]
