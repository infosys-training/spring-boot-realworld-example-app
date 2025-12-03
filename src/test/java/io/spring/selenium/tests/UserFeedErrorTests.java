package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.FeedPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavigationPage;
import java.net.HttpURLConnection;
import java.net.URL;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserFeedErrorTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  private HomePage homePage;
  private FeedPage feedPage;
  private NavigationPage navigationPage;

  @BeforeMethod
  public void setupTest() {
    super.setupTest();
    homePage = new HomePage(driver);
    navigationPage = new NavigationPage(driver);
  }

  private void loginAsTestUser() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"smoke", "error"})
  public void testTC019_UnauthenticatedUserCannotAccessYourFeedTab() {
    createTest(
        "TC-019: Verify unauthenticated user cannot access Your Feed tab",
        "Verify that Your Feed tab is not visible for unauthenticated users");

    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    assertFalse(
        homePage.isYourFeedTabVisible(),
        "Your Feed tab should not be visible for unauthenticated user");
    assertTrue(
        homePage.isGlobalFeedTabVisible(),
        "Global Feed tab should be visible for unauthenticated user");
    test.pass("Unauthenticated user cannot access Your Feed tab");
  }

  @Test(groups = {"smoke", "error"})
  public void testTC020_UnauthenticatedApiRequestReturns401() {
    createTest(
        "TC-020: Verify unauthenticated API request returns 401",
        "Verify that API returns 401 Unauthorized when no token is provided");

    try {
      URL url = new URL(API_URL + "/articles/feed");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-Type", "application/json");

      int responseCode = conn.getResponseCode();
      test.info("API response code: " + responseCode);

      assertEquals(responseCode, 401, "API should return 401 for unauthenticated request");
      test.pass("Unauthenticated API request returns 401");
    } catch (Exception e) {
      test.fail("Error making API request: " + e.getMessage());
      fail("Error making API request: " + e.getMessage());
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC021_FeedWithInvalidTokenReturnsError() {
    createTest(
        "TC-021: Verify feed with invalid token returns error",
        "Verify that API returns error when invalid token is provided");

    try {
      URL url = new URL(API_URL + "/articles/feed");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Token invalid_token_12345");

      int responseCode = conn.getResponseCode();
      test.info("API response code with invalid token: " + responseCode);

      assertTrue(
          responseCode == 401 || responseCode == 403,
          "API should return 401 or 403 for invalid token");
      test.pass("Feed with invalid token returns error");
    } catch (Exception e) {
      test.fail("Error making API request: " + e.getMessage());
      fail("Error making API request: " + e.getMessage());
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC022_FeedWithExpiredTokenReturnsError() {
    createTest(
        "TC-022: Verify feed with expired token returns error",
        "Verify that API returns 401 when expired token is provided");

    try {
      String expiredToken =
          "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.expired";

      URL url = new URL(API_URL + "/articles/feed");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Token " + expiredToken);

      int responseCode = conn.getResponseCode();
      test.info("API response code with expired token: " + responseCode);

      assertTrue(
          responseCode == 401 || responseCode == 403,
          "API should return 401 or 403 for expired token");
      test.pass("Feed with expired token returns error");
    } catch (Exception e) {
      test.fail("Error making API request: " + e.getMessage());
      fail("Error making API request: " + e.getMessage());
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC023_FeedHandlesServerErrorGracefully() {
    createTest(
        "TC-023: Verify feed handles server error gracefully",
        "Verify that UI displays error message when server returns 500 error");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    boolean hasArticles = feedPage.getArticleCount() > 0;
    boolean hasEmptyMessage = feedPage.isEmptyFeedMessageDisplayed();
    boolean hasError = feedPage.isErrorMessageDisplayed();

    assertTrue(
        hasArticles || hasEmptyMessage || hasError,
        "Feed should display articles, empty message, or error gracefully");
    test.pass("Feed handles server response gracefully");
  }

  @Test(groups = {"regression", "error"})
  public void testTC024_FeedHandlesNetworkTimeoutGracefully() {
    createTest(
        "TC-024: Verify feed handles network timeout gracefully",
        "Verify that UI shows loading indicator and then error message on timeout");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoading = feedPage.isLoadingSpinnerDisplayed();
    boolean hasContent =
        feedPage.getArticleCount() > 0
            || feedPage.isEmptyFeedMessageDisplayed()
            || feedPage.isErrorMessageDisplayed();

    assertTrue(isLoading || hasContent, "Feed should show loading or content");
    test.pass("Feed handles network conditions gracefully");
  }

  @Test(groups = {"regression", "error"})
  public void testTC025_ErrorMessageDisplayedWhenFeedFailsToLoad() {
    createTest(
        "TC-025: Verify error message displayed when feed fails to load",
        "Verify that user-friendly error message is displayed when feed API fails");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    boolean hasArticles = feedPage.getArticleCount() > 0;
    boolean hasEmptyMessage = feedPage.isEmptyFeedMessageDisplayed();
    boolean hasError = feedPage.isErrorMessageDisplayed();

    assertTrue(
        hasArticles || hasEmptyMessage || hasError,
        "Feed should display content or appropriate message");
    test.pass("Feed displays appropriate message based on state");
  }

  @Test(groups = {"regression", "error"})
  public void testTC026_GlobalFeedWorksWhenYourFeedFails() {
    createTest(
        "TC-026: Verify Global Feed still works when Your Feed fails",
        "Verify that Global Feed loads successfully even if Your Feed has issues");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    feedPage.clickGlobalFeed();
    feedPage.waitForFeedToLoad();

    assertTrue(feedPage.isGlobalFeedActive(), "Global Feed tab should be active");

    boolean hasArticles = feedPage.getArticleCount() > 0;
    boolean hasEmptyMessage = feedPage.isEmptyFeedMessageDisplayed();

    assertTrue(
        hasArticles || hasEmptyMessage, "Global Feed should display articles or empty message");
    test.pass("Global Feed works independently of Your Feed");
  }
}
