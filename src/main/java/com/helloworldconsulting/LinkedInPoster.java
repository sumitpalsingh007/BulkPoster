package com.helloworldconsulting;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class LinkedInPoster {

    private static final Logger log = LoggerFactory.getLogger(LinkedInPoster.class);

    // XPath: more resilient than exact-class CSS selector
    private static final String SHARE_BUTTON_XPATH =
            "//button[contains(@class, 'artdeco-button--tertiary') and contains(@class, 'artdeco-button--4')]";

    private static final String TEXTBOX_CSS =
        "div[role='textbox']";
    // Exact class match required by LinkedIn's current markup
    private static final String POST_BUTTON_CSS =
        "button[class='share-actions__primary-action artdeco-button artdeco-button--2 artdeco-button--primary ember-view']";

    static final List<String> DEFAULT_GROUP_URLS = List.of(
        "https://www.linkedin.com/groups/10330716/",
        "https://www.linkedin.com/groups/12491361/",
        "https://www.linkedin.com/groups/1821361/",
        "https://www.linkedin.com/groups/2219633/",
        "https://www.linkedin.com/groups/1804077/",
        "https://www.linkedin.com/groups/737627/",
        "https://www.linkedin.com/groups/1687427/",
        "https://www.linkedin.com/groups/4056558/",
        "https://www.linkedin.com/groups/118012/",
        "https://www.linkedin.com/groups/77334/",
        "https://www.linkedin.com/groups/2843596/",
        "https://www.linkedin.com/groups/6749504/",
        "https://www.linkedin.com/groups/7018767/",
        "https://www.linkedin.com/groups/4125702/",
        "https://www.linkedin.com/groups/6690249/",
        "https://www.linkedin.com/groups/8529383/",
        "https://www.linkedin.com/groups/8321253/",
        "https://www.linkedin.com/groups/13901055/",
        "https://www.linkedin.com/groups/9073445/",
        "https://www.linkedin.com/groups/43888/",
        "https://www.linkedin.com/groups/2512858/",
        "https://www.linkedin.com/groups/70526/",
        "https://www.linkedin.com/groups/3067985/",
        "https://www.linkedin.com/groups/3852364/",
        "https://www.linkedin.com/groups/10491264/",
        "https://www.linkedin.com/groups/12111230/",
        "https://www.linkedin.com/groups/13890947/",
        "https://www.linkedin.com/groups/3674984/",
        "https://www.linkedin.com/groups/7050539/",
        "https://www.linkedin.com/groups/8491835/",
        "https://www.linkedin.com/groups/13887989/",
        "https://www.linkedin.com/groups/13887854/",
        "https://www.linkedin.com/groups/8428322/",
        "https://www.linkedin.com/groups/143130/",
        "https://www.linkedin.com/groups/99500/",
        "https://www.linkedin.com/groups/52152/",
        "https://www.linkedin.com/groups/14360803/",
        "https://www.linkedin.com/groups/9078278/",
        "https://www.linkedin.com/groups/6795031/",
        "https://www.linkedin.com/groups/10306078/",
        "https://www.linkedin.com/groups/4226744/",
        "https://www.linkedin.com/groups/8444948/",
        "https://www.linkedin.com/groups/2066905/",
        "https://www.linkedin.com/groups/1251651/"
    );

    private final WebDriver driver;
    private final List<String> groupUrls;
    private final long loginWaitMillis;

    /**
     * Creates a production instance with headless Chrome and the full group list.
     */
    public static LinkedInPoster create() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--headless=new",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-gpu",
            "--window-size=1920,1080",
            "--remote-allow-origins=*"
        );
        return new LinkedInPoster(new ChromeDriver(options), DEFAULT_GROUP_URLS, 10000L);
    }

    /**
     * Package-private: lets tests inject a mock/real WebDriver and custom group URLs.
     * loginWaitMillis = 0 in tests to skip the post-login sleep.
     */
    LinkedInPoster(WebDriver driver, List<String> groupUrls, long loginWaitMillis) {
        this.driver = driver;
        this.groupUrls = groupUrls;
        this.loginWaitMillis = loginWaitMillis;
    }

    /** Convenience constructor for integration tests (no login wait). */
    LinkedInPoster(WebDriver driver, List<String> groupUrls) {
        this(driver, groupUrls, 0L);
    }

    public void login(String username, String password) throws InterruptedException {
        login(username, password, "https://www.linkedin.com/login");
    }

    /**
     * Logs in to LinkedIn. The loginUrl parameter is overridable for integration tests
     * that point to a local mock page.
     */
    void login(String username, String password, String loginUrl) throws InterruptedException {
        driver.get(loginUrl);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton  = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();

        // Wait for post-login redirect. In real usage LinkedIn redirects to the feed;
        // we use a simple sleep here because the feed element ID changes with LinkedIn updates.
        if (loginWaitMillis > 0) {
            Thread.sleep(loginWaitMillis);
        }
        log.info("Login submitted for {}", username);
    }

    public void postToGroups(String url, String title) throws InterruptedException {
        int count = 0;
        for (String groupUrl : groupUrls) {
            try {
                driver.navigate().to(groupUrl);

                //checking for member exist in the group or not
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath(SHARE_BUTTON_XPATH)
                    ));
                } catch (Exception e) {
                    log.warn("Not a member of group {}, skipping...", groupUrl);
                    continue; //move to next group
                }

                // Member exist - post
                WebElement postBtn = driver.findElement(By.xpath(SHARE_BUTTON_XPATH));
                postBtn.click();

                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TEXTBOX_CSS)));
                WebElement textBox = driver.findElement(By.cssSelector(TEXTBOX_CSS));

                textBox.sendKeys(url);
                Thread.sleep(2000);
                textBox.sendKeys(title);

                new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(POST_BUTTON_CSS)));
                driver.findElement(By.cssSelector(POST_BUTTON_CSS)).click();

                Thread.sleep(1000);
                count++;
                log.info("Posted to group {} (total: {})", groupUrl, count);
            } catch (Exception e) {
                log.warn("Failed to post to group {}: {}", groupUrl, e.getMessage());
            }
        }
        log.info("Finished posting. Successful: {}/{}", count, groupUrls.size());
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }
}
