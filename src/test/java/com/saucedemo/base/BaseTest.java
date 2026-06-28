package com.saucedemo.base;

import com.microsoft.playwright.*;
import com.saucedemo.config.ConfigReader;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Thread-safe base class for all tests.
 * Each test thread owns its own Playwright → Browser → BrowserContext → Page chain.
 */
public class BaseTest {

    // ThreadLocal ensures parallel tests never share browser state
    protected static final ThreadLocal<Playwright>     playwrightTL = new ThreadLocal<>();
    protected static final ThreadLocal<Browser>        browserTL    = new ThreadLocal<>();
    protected static final ThreadLocal<BrowserContext> contextTL    = new ThreadLocal<>();
    protected static final ThreadLocal<Page>           pageTL       = new ThreadLocal<>();

    protected Page page() { return pageTL.get(); }
    protected BrowserContext context() { return contextTL.get(); }

    @BeforeEach
    void setUp() {
        Playwright playwright = Playwright.create();
        playwrightTL.set(playwright);

        Browser browser = resolveBrowser(playwright)
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(ConfigReader.isHeadless()));
        browserTL.set(browser);

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setRecordVideoDir(Paths.get("target/videos/"))
                .setLocale("en-US"));

        context.setDefaultTimeout(ConfigReader.getDefaultTimeout());

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        contextTL.set(context);
        pageTL.set(context.newPage());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) throws IOException {
        String safeName = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");

        // Attach screenshot to Allure on every test
        byte[] screenshot = pageTL.get().screenshot();
        Allure.addAttachment("Screenshot – " + safeName,
                "image/png", new ByteArrayInputStream(screenshot), "png");

        // Save Playwright trace
        Path tracePath = Paths.get("target/traces/" + safeName + ".zip");
        Files.createDirectories(tracePath.getParent());
        contextTL.get().tracing().stop(new Tracing.StopOptions().setPath(tracePath));

        pageTL.get().close();
        contextTL.get().close();
        browserTL.get().close();
        playwrightTL.get().close();
    }

    private BrowserType resolveBrowser(Playwright pw) {
        return switch (ConfigReader.getBrowser().toLowerCase()) {
            case "firefox" -> pw.firefox();
            case "webkit"  -> pw.webkit();
            default        -> pw.chromium();
        };
    }
}
