package com.helloworldconsulting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// LENIENT: some @BeforeEach stubs are not used by every test (e.g. quit-only tests)
@MockitoSettings(strictness = Strictness.LENIENT)
class LinkedInPosterTest {

    @Mock WebDriver           mockDriver;
    @Mock WebElement          mockElement;
    @Mock WebDriver.Navigation mockNavigation;

    private LinkedInPoster poster;

    @BeforeEach
    void setUp() {
        // All findElement calls return the same mock element → WebDriverWait succeeds immediately
        when(mockDriver.findElement(any(By.class))).thenReturn(mockElement);
        // navigate().to() must not NPE; mock Navigation so the groupUrl loop runs
        when(mockDriver.navigate()).thenReturn(mockNavigation);

        poster = new LinkedInPoster(mockDriver, List.of("https://example.com/groups/1/"), 0L);
    }

    // ── login ──────────────────────────────────────────────────────────────

    @Test
    void loginNavigatesToLinkedInLoginPage() throws InterruptedException {
        poster.login("user@example.com", "secret");
        verify(mockDriver).get("https://www.linkedin.com/login");
    }

    @Test
    void loginEntersCredentialsInOrder() throws InterruptedException {
        poster.login("user@example.com", "secret");

        InOrder inOrder = inOrder(mockElement);
        inOrder.verify(mockElement).sendKeys("user@example.com");
        inOrder.verify(mockElement).sendKeys("secret");
        inOrder.verify(mockElement).click();
    }

    @Test
    void loginWithCustomUrlNavigatesToThatUrl() throws InterruptedException {
        poster.login("u", "p", "https://mock.local/login");
        verify(mockDriver).get("https://mock.local/login");
    }

    // ── postToGroups ───────────────────────────────────────────────────────

    @Test
    void postToGroupsNavigatesToGroupUrl() throws InterruptedException {
        poster.postToGroups("https://yt.be/v=abc", "My Video");
        // navigate().to(groupUrl) must have been called
        verify(mockNavigation).to("https://example.com/groups/1/");
    }

    @Test
    void postToGroupsTypesUrlAndTitleIntoTextbox() throws InterruptedException {
        poster.postToGroups("https://yt.be/v=abc", "My Video Title");

        verify(mockElement, atLeastOnce()).sendKeys("https://yt.be/v=abc");
        verify(mockElement, atLeastOnce()).sendKeys("My Video Title");
    }

    @Test
    void postToGroupsClicksShareButtonAndPostButton() throws InterruptedException {
        poster.postToGroups("https://yt.be/v=abc", "My Video");
        // share button click + post button click = at least 2 element.click() calls
        verify(mockElement, atLeast(2)).click();
    }

    // ── quit ──────────────────────────────────────────────────────────────

    @Test
    void quitClosesWebDriver() {
        poster.quit();
        verify(mockDriver).quit();
    }

    @Test
    void quitIsIdempotentWhenCalledTwice() {
        poster.quit();
        poster.quit();
        verify(mockDriver, times(2)).quit();
    }

    // ── default group list ─────────────────────────────────────────────────

    @Test
    void defaultGroupUrlsIsNotEmpty() {
        assertFalse(LinkedInPoster.DEFAULT_GROUP_URLS.isEmpty());
    }
}
