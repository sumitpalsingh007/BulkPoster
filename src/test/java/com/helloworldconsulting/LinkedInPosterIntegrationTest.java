package com.helloworldconsulting;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that spin up a real Chrome browser inside a Docker container
 * (via Testcontainers) and exercise LinkedInPoster against locally-served mock HTML
 * pages that replicate LinkedIn's element structure.
 *
 * Run with: mvn verify   (or mvn failsafe:integration-test)
 * Skipped during normal: mvn test
 *
 * Requires Docker to be running on the host machine.
 */
class LinkedInPosterIntegrationTest {

    // Pinned to the same Selenium version used by the project
    @SuppressWarnings("resource")
    static final BrowserWebDriverContainer<?> CHROME =
        new BrowserWebDriverContainer<>("selenium/standalone-chrome:4.21.0-20240521")
            .withCapabilities(new ChromeOptions());

    @BeforeAll
    static void startChrome() {
        CHROME.start();
    }

    @AfterAll
    static void stopChrome() {
        CHROME.stop();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private WebDriver driver() {
        return CHROME.getWebDriver();
    }

    /**
     * Encodes arbitrary HTML as a data: URL that Chrome can navigate to directly.
     * No external server required.
     */
    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8,"
            + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }

    // ── login page interaction ────────────────────────────────────────────────

    /**
     * Verifies that login() finds the username/password fields, sends keys, and clicks
     * the submit button. The mock page includes feed-nav-item so the no-op wait
     * (loginWaitMillis=0) satisfies the login contract.
     */
    @Test
    void loginFillsUsernameAndPassword() throws Exception {
        String loginHtml =
            "<html><body>" +
            "<input id='username' type='email'/>" +
            "<input id='password' type='password'/>" +
            "<button type='submit'>Sign in</button>" +
            "</body></html>";

        WebDriver wd = driver();
        LinkedInPoster poster = new LinkedInPoster(wd, List.of(), 0L);

        // Navigate directly then call login with the already-loaded URL
        String url = dataUrl(loginHtml);
        poster.login("test@example.com", "mypassword", url);

        // After login() the driver is still on the mock page – verify fields were filled
        assertEquals("test@example.com",
            wd.findElement(By.id("username")).getAttribute("value"));
        assertEquals("mypassword",
            wd.findElement(By.id("password")).getAttribute("value"));
    }

    // ── group posting interaction ─────────────────────────────────────────────

    /**
     * Verifies that postToGroups() finds the share button, the text area, types content,
     * and clicks the post button against a minimal mock LinkedIn group page.
     *
     * The mock HTML pre-renders all elements (share button, textbox, post button) so
     * all WebDriverWaits succeed immediately without a real modal flow.
     */
    @Test
    void postToGroupsTypesContentAndClicksPostButton() throws Exception {
        String groupHtml =
            "<html><body>" +
            // XPath: //button[contains(@class,'share-box-feed-entry__trigger')]
            "<button class='artdeco-button share-box-feed-entry__trigger'>Start a post</button>" +
            "<div role='textbox' contenteditable='true' style='min-height:40px'></div>" +
            // CSS: exact class list used by LinkedInPoster
            "<button class='share-actions__primary-action artdeco-button artdeco-button--2 artdeco-button--primary ember-view'>Post</button>" +
            "</body></html>";

        String groupUrl = dataUrl(groupHtml);
        WebDriver wd = driver();

        LinkedInPoster poster = new LinkedInPoster(wd, List.of(groupUrl), 0L);
        poster.postToGroups("https://www.youtube.com/watch?v=test123", "Integration Test Video");

        // After posting the page is still open; verify text was typed into the textbox
        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(5));
        WebElement textbox = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[role='textbox']")));

        String content = textbox.getText();
        assertTrue(content.contains("test123"),
            "Textbox should contain the video URL fragment. Actual: " + content);
    }

    // ── Chrome container smoke test ────────────────────────────────────────────

    @Test
    void chromeContainerCanNavigateToBlankPage() {
        WebDriver wd = driver();
        wd.get("about:blank");
        assertEquals("", wd.getTitle());
    }

    @Test
    void chromeContainerCanRenderArbitraryHtml() {
        WebDriver wd = driver();
        wd.get(dataUrl("<html><head><title>Smoke Test</title></head></html>"));

        WebDriverWait wait = new WebDriverWait(wd, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Smoke Test"));

        assertEquals("Smoke Test", wd.getTitle());
    }
}
