# 🎭 Playwright Java – Sauce Demo Test Suite

Automation framework for [sauce-demo.myshopify.com](https://sauce-demo.myshopify.com) built with **Playwright for Java**, **JUnit 5**, and **Allure Reports**.


---

## Tech Stack

| Tool | Purpose |
|------|---------|
| [Playwright Java 1.44](https://playwright.dev/java/) | Browser automation |
| [JUnit 5](https://junit.org/junit5/) | Test framework |
| [Allure 2.27](https://allurereport.org/) | Rich test reporting |
| [Maven](https://maven.apache.org/) | Build & dependency management |
| [GitHub Actions](https://github.com/features/actions) | CI/CD pipeline |

---

## 📁 Project Structure

```
src/test/java/com/saucedemo/
├── base/
│   ├── BaseTest.java        # Thread-safe Playwright lifecycle (@BeforeEach / @AfterEach)
│   └── BasePage.java        # Common page actions with @Step Allure annotations
├── config/
│   └── ConfigReader.java    # Reads test.properties & system properties
├── pages/                   # Page Object Model
│   ├── LoginPage.java
│   ├── AccountPage.java
│   ├── StorePage.java
│   ├── ProductPage.java
│   └── CartPage.java
├── tests/
│   ├── LoginTest.java       # Auth tests with parameterized cases
│   ├── StoreTest.java       # Product catalog tests, parallel execution
│   └── ApiHybridTest.java   # API + UI hybrid tests & network mocking
└── utils/
    └── TestDataFactory.java # Loads users from testdata/users.json
```

---

## ✨ Features Demonstrated

- **Page Object Model (POM)** — all locators declared as fields, fluent page chaining
- **Parallel execution** — `ThreadLocal<Page>` ensures thread-safety; 4 threads by default
- **API + UI hybrid testing** — validate HTTP layer then assert UI reflects it
- **Network mocking** — intercept requests via `page.route()` to simulate 503 errors
- **Allure reporting** — `@Epic`, `@Feature`, `@Story`, `@Severity`, screenshots, traces
- **Parameterized tests** — `@ParameterizedTest` with `@CsvSource` and `@ValueSource`
- **Cross-browser CI** — GitHub Actions matrix runs Chromium, Firefox, and WebKit

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run all tests (headless Chromium)
```bash
mvn test
```

### Run on a specific browser
```bash
mvn test -Dbrowser=firefox
```

### Run in headed mode (see the browser)
```bash
mvn test -Dheadless=false
```

### Generate Allure report
```bash
mvn allure:serve
```

---

## 📊 Allure Report

The CI pipeline publishes the Allure report to **GitHub Pages** automatically on every push to `main`.

To view locally after a test run:
```bash
mvn allure:serve
```

---

## 🔍 Playwright Traces

Traces are saved to `target/traces/` after every test. Open them with:
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="show-trace target/traces/TEST.zip"
```

---

## ⚙️ Configuration

Edit `src/test/resources/test.properties`:

```properties
base.url=https://sauce-demo.myshopify.com
browser=chromium
headless=true
default.timeout.ms=10000
```

All properties can be overridden via Maven `-D` flags.
