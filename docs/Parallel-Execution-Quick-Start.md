# Parallel Execution - Quick Start Guide

## 🚀 Quick Commands

### Fast Parallel Execution (Recommended)
```bash
./gradlew testParallel
```
- ⚡ Fastest execution (50-70% time reduction)
- 🖥️ Headless mode (no browser UI)
- 🧵 Uses all CPU cores
- ✅ Best for CI/CD

### Development Mode (Browser UI Visible)
```bash
./gradlew testSequential
```
- 👁️ Browser UI visible
- 🐢 Sequential execution
- ⏱️ 2-second delay between tests
- ✅ Best for debugging

### Compare Performance
```bash
./gradlew testBenchmark
```
- 📊 Runs tests sequentially then in parallel
- 📈 Reports speed improvement percentage
- ⏱️ Shows execution times

## 📊 Expected Performance

| Test Count | Sequential | Parallel (4 cores) | Speedup |
|------------|------------|-------------------|---------|
| 10 tests   | ~30s       | ~12s             | 2.5x    |
| 25 tests   | ~90s       | ~30s             | 3.0x    |
| 50 tests   | ~180s      | ~50s             | 3.6x    |
| 100 tests  | ~360s      | ~95s             | 3.8x    |

## 🎯 Run Specific Test Suites

```bash
# Critical P0 tests only
./gradlew testP0

# Smoke tests
./gradlew testSmoke

# Specific test class
./gradlew testParallel --tests "P0_SendSimpleTextMessageTest"

# Pattern matching
./gradlew testParallel --tests "*MessageTest"
```

## ⚙️ Configuration

### System Properties
| Property | Values | Default | Description |
|----------|--------|---------|-------------|
| `parallel` | true/false | false | Enable parallel execution |
| `headless` | true/false | false | Run headless mode |
| `browser` | chromium/firefox/webkit | chromium | Browser type |
| `slowMo` | 0-1000 | 50 | Slow motion (ms) |

### Examples
```bash
# Parallel with Firefox
./gradlew testParallel -Dbrowser=firefox

# Sequential with headless
./gradlew testSequential -Dheadless=true

# Slow motion for debugging
./gradlew testSequential -DslowMo=500
```

## 🔧 Thread Pool Tuning

Edit `src/test/resources/junit-platform.properties`:

```properties
# More threads (aggressive parallelization)
junit.jupiter.execution.parallel.config.dynamic.factor = 1.5

# Fewer threads (resource-constrained environments)
junit.jupiter.execution.parallel.config.dynamic.factor = 0.75

# Fixed thread count
junit.jupiter.execution.parallel.config.strategy = fixed
junit.jupiter.execution.parallel.config.fixed.parallelism = 8
```

## 🐛 Troubleshooting

### Out of Memory
```gradle
// In build.gradle, increase JVM heap
jvmArgs '-Xmx4g', '-XX:MaxMetaspaceSize=1g'
```

### Tests Fail Only in Parallel
```bash
# Debug with visible browsers (limit 2 parallel)
./gradlew testParallelHeaded --tests "FailingTest"

# Run sequentially for comparison
./gradlew testSequential --tests "FailingTest"
```

### Slow Parallel Execution
```properties
# Increase parallelism (if CPU usage is low)
junit.jupiter.execution.parallel.config.dynamic.factor = 1.5

# Decrease parallelism (if memory constrained)
junit.jupiter.execution.parallel.config.dynamic.factor = 0.5
```

## 🎭 All Available Tasks

| Task | Parallel | Headless | Description |
|------|----------|----------|-------------|
| `test` | Configurable | Configurable | Default test task |
| `testParallel` | ✅ Yes | ✅ Yes | Fast parallel execution |
| `testSequential` | ❌ No | ❌ No | Sequential with UI |
| `testParallelHeaded` | ✅ Yes (2 threads) | ❌ No | Parallel debugging |
| `testP0` | ✅ Yes | ✅ Yes | P0 tests only |
| `testSmoke` | ✅ Yes | ✅ Yes | Smoke tests only |
| `testBenchmark` | Both | Both | Performance comparison |

## 📚 More Information

See complete documentation: [docs/Parallel-Execution-Implementation.md](Parallel-Execution-Implementation.md)

## ✅ Implementation Status

✅ **Completed**
- JUnit 5 parallel configuration
- Thread-safe BaseTest with ThreadLocal
- Multiple execution mode tasks
- Gradle build configuration
- Performance benchmarking
- Comprehensive documentation

🚀 **Benefits**
- 50-70% faster test execution
- Zero breaking changes
- Backward compatible
- CI/CD ready
- Scalable architecture

---

**Last Updated:** 2024-01-15  
**Framework Version:** 1.0.0  
**Status:** Production Ready ✅
