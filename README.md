# 🎭 Playwright Java

End-to-end automation framework for [saucedemo.com](https://www.saucedemo.com) built with **Playwright for Java**, **JUnit 5**, and **Allure Reports**.

![CI](https://github.com/olehnazarov/playwright-java-demo/actions/workflows/playwright.yml/badge.svg)

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

## Project Structure

```
src/test/java/com/saucedemo/
├── base/
│   ├── BaseTest.java        # Thread-safe Playwright lifecycle
│   └── BasePage.java        # Common page actions with @Step Allure annotations
├── config/
│   └── ConfigReader.java    # Reads test.properties & system properties
├── pages/                   # Page Object Model
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── ProductPage.java
│   ├── CartPage.java
│   └── CheckoutPage.java
├── tests/
│   ├── LoginTest.java       # Auth: valid login, locked user, validation
│   ├── InventoryTest.java   # Catalog: sorting, cart badge, product detail
│   ├── CartTest.java        # Cart: add/remove items, continue shopping
│   ├── CheckoutTest.java    # Checkout: happy path, validation errors
│   └── ApiHybridTest.java   # API + UI hybrid tests & network mocking
└── utils/
    └── TestDataFactory.java # Loads users from testdata/users.json
```

---

## Features Demonstrated

- **Page Object Model (POM)** — all locators declared as fields, fluent page chaining
- **Parallel execution** — `ThreadLocal<Page>` ensures thread-safety; 4 threads by default
- **API + UI hybrid testing** — validate HTTP layer then assert UI reflects it
- **Network mocking** — intercept requests via `page.route()` to simulate errors
- **Allure reporting** — `@Epic`, `@Feature`, `@Story`, `@Severity`, screenshots, traces
- **Parameterized tests** — `@ParameterizedTest` with `@CsvSource`
- **Cross-browser CI** — GitHub Actions matrix runs Chromium, Firefox, and WebKit

---

## Test Coverage

| Suite | Scenarios |
|-------|-----------|
| LoginTest | Valid login, locked user error, wrong password, empty fields validation |
| InventoryTest | Product count, names, prices, add/remove, sorting, logout |
| CartTest | Empty cart, add product, multiple products, remove, continue shopping |
| CheckoutTest | Full checkout flow, order total, missing first name, missing postal code |
| ApiHybridTest | HTTP 200 check, API→UI health, network mock 503 |

---

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+

### Install Playwright browsers
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install --with-deps"
```

### Run all tests
```bash
mvn test
```

### Run on a specific browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=webkit
```

### Run in headed mode
```bash
mvn test -Dheadless=false
```

### Run a specific test class
```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=CheckoutTest
```

### Generate Allure report
```bash
mvn allure:serve
```

---

## Allure Report

The CI pipeline publishes the Allure report to **GitHub Pages** on every push to `main`:

🔗 **[View Live Report](https://olehnazarov.github.io/playwright-java-demo/)**

---

## ⚙️ Configuration

Edit `src/test/resources/test.properties`:

```properties
base.url=https://www.saucedemo.com
browser=chromium
headless=true
default.timeout.ms=10000
```

All properties can be overridden via Maven `-D` flags.

---

## Test Users

| Role | Username | Notes |
|------|----------|-------|
| standard | `standard_user` | Default test user |
| locked | `locked_out_user` | Used to test error handling |
| problem | `problem_user` | UI glitch scenarios |
| performance | `performance_glitch_user` | Slow response scenarios |