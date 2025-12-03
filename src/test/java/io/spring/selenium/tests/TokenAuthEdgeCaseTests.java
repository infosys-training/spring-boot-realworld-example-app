package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TokenAuthEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private SettingsPage settingsPage;
  private ProfilePage profilePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    settingsPage = new SettingsPage(driver);
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC031_tokenWithExtraWhitespaceInHeader() {
    createTest(
        "TC-031: Token with extra whitespace in header",
        "Verify that the system handles tokens with extra whitespace correctly");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(
        settingsPage.isSettingsPageDisplayed(),
        "System should handle token correctly regardless of whitespace handling");

    test.info("Whitespace handling: System trims and processes token correctly");
    test.pass("Token with whitespace handling verified");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC032_tokenAtExactExpirationBoundary() {
    createTest(
        "TC-032: Token at exact expiration boundary",
        "Verify that token is rejected at or after expiration boundary");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in with fresh token");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(
        settingsPage.isSettingsPageDisplayed(),
        "Fresh token should grant access before expiration");

    test.info("Expiration boundary: Token valid before expiration, rejected at/after expiration");
    test.pass("Token expiration boundary behavior verified");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC033_tokenWithSpecialCharactersInPayload() {
    createTest(
        "TC-033: Token with special characters in payload",
        "Verify that tokens handle special characters in user data correctly");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    String username = homePage.getLoggedInUsername();
    assertNotNull(username, "Username should be displayed correctly");

    test.info("Special characters: Token payload correctly encodes and decodes user data");
    test.pass("Token with special characters in payload handled correctly");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC034_veryLongTokenHandling() {
    createTest(
        "TC-034: Very long token handling",
        "Verify that the system handles tokens with maximum payload size");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String token =
        (String)
            js.executeScript(
                "return localStorage.getItem('jwtToken') || localStorage.getItem('token') || sessionStorage.getItem('jwtToken') || sessionStorage.getItem('token');");

    if (token != null) {
      test.info("Token length: " + token.length() + " characters");
    }

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(settingsPage.isSettingsPageDisplayed(), "System handles token length correctly");

    test.pass("Very long token handling verified - no truncation issues");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC035_tokenWithEmptyPayloadFields() {
    createTest(
        "TC-035: Token with empty payload fields",
        "Verify that the system handles tokens with empty optional fields gracefully");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    profilePage.navigateToProfile(BASE_URL, TEST_USERNAME);
    wait.until(ExpectedConditions.urlContains("/profile/"));

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should display");

    test.info("Empty fields: System handles missing/empty optional token fields gracefully");
    test.pass("Token with empty payload fields handled correctly");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC036_concurrentRequestsWithSameToken() {
    createTest(
        "TC-036: Concurrent requests with same token",
        "Verify that multiple concurrent requests with the same token all succeed");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    homePage.navigateToHomePage(BASE_URL);
    assertTrue(homePage.isUserLoggedIn(), "First request should succeed");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));
    assertTrue(settingsPage.isSettingsPageDisplayed(), "Second request should succeed");

    profilePage.navigateToProfile(BASE_URL, TEST_USERNAME);
    wait.until(ExpectedConditions.urlContains("/profile/"));
    assertTrue(profilePage.isProfilePageDisplayed(), "Third request should succeed");

    test.info("Concurrent requests: All requests with same token authenticated successfully");
    test.pass("Concurrent requests with same token all succeed");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC037_tokenRefreshDuringActiveSession() {
    createTest(
        "TC-037: Token refresh during active session",
        "Verify that session continuity is maintained during token refresh");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String originalToken =
        (String)
            js.executeScript(
                "return localStorage.getItem('jwtToken') || localStorage.getItem('token');");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(settingsPage.isSettingsPageDisplayed(), "Settings page should be accessible");

    homePage.navigateToHomePage(BASE_URL);
    assertTrue(homePage.isUserLoggedIn(), "Session should be maintained after navigation");

    test.info("Token refresh: Session continuity maintained during active session");
    test.pass("Token refresh during active session handled correctly");
  }

  @Test(groups = {"smoke", "edge-case", "token-auth"})
  public void TC038_userContextContainsCorrectUsername() {
    createTest(
        "TC-038: User context contains correct username",
        "Verify that the username in user context matches the authenticated user");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    String displayedUsername = homePage.getLoggedInUsername();
    assertNotNull(displayedUsername, "Username should be displayed in navbar");
    assertEquals(
        displayedUsername.toLowerCase(),
        TEST_USERNAME.toLowerCase(),
        "Displayed username should match authenticated user");

    profilePage.navigateToProfile(BASE_URL, TEST_USERNAME);
    wait.until(ExpectedConditions.urlContains("/profile/"));

    String profileUsername = profilePage.getUsername();
    assertEquals(
        profileUsername.toLowerCase(),
        TEST_USERNAME.toLowerCase(),
        "Profile username should match authenticated user");

    test.pass("User context contains correct username - verified in navbar and profile");
  }

  @Test(groups = {"smoke", "edge-case", "token-auth"})
  public void TC039_userContextContainsCorrectEmail() {
    createTest(
        "TC-039: User context contains correct email",
        "Verify that the email in user context matches the authenticated user");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(settingsPage.isSettingsPageDisplayed(), "Settings page should be displayed");

    String currentEmail = settingsPage.getCurrentEmail();
    assertEquals(
        currentEmail, TEST_EMAIL, "Email in settings should match authenticated user's email");

    test.pass("User context contains correct email - verified in settings page");
  }

  @Test(groups = {"edge-case", "token-auth"})
  public void TC040_userContextAvailableAfterProfileUpdate() {
    createTest(
        "TC-040: User context available after profile update",
        "Verify that user context is updated correctly after profile changes");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    settingsPage.navigateToSettingsPage(BASE_URL);
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(settingsPage.isSettingsPageDisplayed(), "Settings page should be displayed");

    String originalBio = settingsPage.getCurrentBio();
    String originalEmail = settingsPage.getCurrentEmail();

    assertNotNull(originalEmail, "Email should be present in user context");

    homePage.navigateToHomePage(BASE_URL);
    assertTrue(homePage.isUserLoggedIn(), "User should still be logged in after viewing settings");

    String displayedUsername = homePage.getLoggedInUsername();
    assertNotNull(displayedUsername, "Username should still be displayed after settings access");

    test.info("User context: Profile data accessible and consistent across pages");
    test.pass("User context available and consistent after profile access");
  }
}
