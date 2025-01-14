package com.helloworldconsulting;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinkedInPoster {

    private static final String SHARE_BUTTON = "button[class='artdeco-button artdeco-button--muted artdeco-button--4 artdeco-button--tertiary ember-view share-box-feed-entry__trigger']";
    private static final String SHARE_BUTTON_BY_XPATH = "//button[contains(@class, 'share-box-feed-entry__trigger')]";
    private static final String DIV_ROLE_TEXTBOX = "div[role='textbox']";
    private static final String BUTTON_PRIMARY_EMBER_VIEW = "button[class='share-actions__primary-action artdeco-button artdeco-button--2 artdeco-button--primary ember-view']";
    private WebDriver driver;

    public LinkedInPoster() {
        //WebDriverManager.chromedriver().setup();

        // Setting ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addExtensions(new File("/path/to/extension.crx"));
        // Initializing the WebDriver with ChromeOptions
        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");
        driver = new ChromeDriver(options);
    }

    public void login(String username, String password) throws InterruptedException {
        driver.get("https://www.linkedin.com/login");

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
        // Click the login butto

        // Wait for the verification code input to appear
        //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        //wait.until(ExpectedConditions.presenceOfElementLocated(By.id("feed-nav-item")));
        /*if(!driver.findElement(By.id("feed-nav-item")).isDisplayed()) {
            WebElement verificationInput = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("input_verification_code"))
            );

            // Prompt user to enter the verification code manually
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the verification code sent to your email: ");
            String verificationCode = scanner.nextLine();
            scanner.close();

            // Enter the verification code
            verificationInput.sendKeys(verificationCode);
            verificationInput.sendKeys(Keys.RETURN);

            // Wait for the homepage to load (example: wait for the feed to load)
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("feed-nav-item")));
        }*/
        System.out.println("Successfully logged in!");
    }

    public void postToGroups(String url, String title) throws InterruptedException {
        driver.get("https://www.linkedin.com/groups/");
        Thread.sleep(5000);
        //List<WebElement> groups = driver.findElements(By.cssSelector("a[href*='/groups/']"));
        //new WebDriverWait(driver, Duration.ofSeconds(10))
            //.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='/groups/']")));

        //List<WebElement> groupLinks = driver.findElements(By.cssSelector("a[href*='/groups/']"));

        //List<WebElement> groups = driver.findElements(By.className("ul[class='artdeco-list artdeco-list--offset-2']"));
        //WebElement ulElement = driver.findElement(By.cssSelector("ul.artdeco-list.artdeco-list--offset-2"));

        //List<WebElement> liElements = ulElement.findElements(By.cssSelector("li.artdeco-list__item"));

        List<String> urls = new ArrayList<>();
        //urls.add("https://www.linkedin.com/groups/9074455/");
        urls.add("https://www.linkedin.com/groups/10330716/");
        urls.add("https://www.linkedin.com/groups/12491361/");
        urls.add("https://www.linkedin.com/groups/1821361/");
        urls.add("https://www.linkedin.com/groups/2219633/");
        urls.add("https://www.linkedin.com/groups/1804077/");
        urls.add("https://www.linkedin.com/groups/737627/");
        //urls.add("https://www.linkedin.com/groups/46964/");
        urls.add("https://www.linkedin.com/groups/1687427/");
        urls.add("https://www.linkedin.com/groups/4056558/");
        urls.add("https://www.linkedin.com/groups/118012/");
        urls.add("https://www.linkedin.com/groups/77334/");
        urls.add("https://www.linkedin.com/groups/2843596/");
        urls.add("https://www.linkedin.com/groups/6749504/");
        urls.add("https://www.linkedin.com/groups/7018767/");
        urls.add("https://www.linkedin.com/groups/4125702/");
        urls.add("https://www.linkedin.com/groups/6690249/");
        urls.add("https://www.linkedin.com/groups/8529383/");
        urls.add("https://www.linkedin.com/groups/8321253/");
        urls.add("https://www.linkedin.com/groups/13901055/");
        urls.add("https://www.linkedin.com/groups/9073445/");
        urls.add("https://www.linkedin.com/groups/43888/");
        urls.add("https://www.linkedin.com/groups/2512858/");
        urls.add("https://www.linkedin.com/groups/70526/");
        urls.add("https://www.linkedin.com/groups/3067985/");
        urls.add("https://www.linkedin.com/groups/3852364/");
        urls.add("https://www.linkedin.com/groups/10491264/");
        urls.add("https://www.linkedin.com/groups/12111230/");
        urls.add("https://www.linkedin.com/groups/13890947/");
        urls.add("https://www.linkedin.com/groups/3674984/");
        urls.add("https://www.linkedin.com/groups/7050539/");
        urls.add("https://www.linkedin.com/groups/8491835/");
        urls.add("https://www.linkedin.com/groups/13887989/");
        urls.add("https://www.linkedin.com/groups/13887854/");
        urls.add("https://www.linkedin.com/groups/8428322/");
        urls.add("https://www.linkedin.com/groups/143130/");
        urls.add("https://www.linkedin.com/groups/99500/");
        urls.add("https://www.linkedin.com/groups/52152/");
        //urls.add("https://www.linkedin.com/groups/3983267/");
        urls.add("https://www.linkedin.com/groups/14360803/");
        urls.add("https://www.linkedin.com/groups/9078278/");
        urls.add("https://www.linkedin.com/groups/6795031/");
        urls.add("https://www.linkedin.com/groups/10306078/");
        urls.add("https://www.linkedin.com/groups/4226744/");
        urls.add("https://www.linkedin.com/groups/8444948/");
        urls.add("https://www.linkedin.com/groups/2066905/");
        urls.add("https://www.linkedin.com/groups/1251651");
        int count  = 0;
        for (String groupUrl : urls) {
            try {
                driver.navigate().to(groupUrl);
                new WebDriverWait(driver, Duration.ofSeconds(40))
                        .until(ExpectedConditions.presenceOfElementLocated(By.xpath(SHARE_BUTTON_BY_XPATH)));
                WebElement startPostButton = driver.findElement(By.xpath(SHARE_BUTTON_BY_XPATH));
                startPostButton.click();

                new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(DIV_ROLE_TEXTBOX)));
                WebElement postTextArea = driver.findElement(By.cssSelector(DIV_ROLE_TEXTBOX));

                postTextArea.sendKeys(url);
                Thread.sleep(2000);

                postTextArea.sendKeys(title);

                new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(BUTTON_PRIMARY_EMBER_VIEW)));
                WebElement postButton = driver.findElement(By.cssSelector(BUTTON_PRIMARY_EMBER_VIEW));
                postButton.click();
                Thread.sleep(1000);
                count++;
                System.out.println("posted in :- " + groupUrl + " count " + count);
                //Thread.sleep(2000);
                //driver.navigate().back();
                //Thread.sleep(1000);
                //WebElement dismissButton = driver.findElement(By.cssSelector("button[aria-label='Dismiss “Post successful.” notification 1 of 1.']"));
                //dismissButton.click();
                //Thread.sleep(2000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("total posts :- " + count);
    }

    public void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static void main(String[] args) throws InterruptedException, GeneralSecurityException, IOException {
        LinkedInPoster poster = new LinkedInPoster();
        /*Video randomVideo = YouTubeChannelVideos.getRandomYoutubeVideos();
        poster.postToGroups("https://www.youtube.com/watch?v="  + randomVideo.getId(),
                randomVideo.getSnippet().getTitle());*/
        poster.login(App.MAIL, App.PASSWORD);
        poster.postToGroups(App.POST, App.TITLE);


        poster.quit();
    }
}