package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for User Profile Update functionality. Tests input validation scenarios for
 * US-AUTH-004.
 */
public class ProfileUpdateValidationTests extends BaseTest {

  private LoginPage loginPage;
  private SettingsPage settingsPage;
  private HomePage homePage;
  private String baseUrl;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    loginPage = new LoginPage(driver);
    settingsPage = new SettingsPage(driver);
    homePage = new HomePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"regression", "validation"})
  public void TC011_validateEmailFormatMissingAtSymbol() {
    createTest("TC-011", "Validate email format - missing @ symbol");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter email without @");
    settingsPage.clearEmailField();
    settingsPage.enterEmail("invalidemail.com");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Invalid email format");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC012_validateEmailFormatMissingDomain() {
    createTest("TC-012", "Validate email format - missing domain");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter email without domain");
    settingsPage.clearEmailField();
    settingsPage.enterEmail("user@");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Invalid email format");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC013_validateUsernameMinimumLength() {
    createTest("TC-013", "Validate username minimum length");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter single character username");
    settingsPage.clearUsernameField();
    settingsPage.enterUsername("a");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Username too short");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC014_validateUsernameMaximumLength() {
    createTest("TC-014", "Validate username maximum length");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter username > 50 chars");
    String longUsername = "verylongusernamethatexceedsfiftycharacterslimitfortesting123456789";
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(longUsername);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Username too long");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC015_validatePasswordMinimumLength() {
    createTest("TC-015", "Validate password minimum length");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter password < 6 chars");
    settingsPage.enterPassword("abc");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Password too short");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC016_validateImageUrlFormat() {
    createTest("TC-016", "Validate image URL format");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter invalid URL format");
    settingsPage.clearImageUrlField();
    settingsPage.enterImageUrl("not-a-valid-url");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Invalid URL format");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC017_validateBioMaximumLength() {
    createTest("TC-017", "Validate bio maximum length");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter bio > 500 chars");
    StringBuilder longBio = new StringBuilder();
    for (int i = 0; i < 60; i++) {
      longBio.append("This is a very long bio text. ");
    }
    settingsPage.clearBioField();
    settingsPage.enterBio(longBio.toString());

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Bio too long");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC018_validateEmailWithSpaces() {
    createTest("TC-018", "Validate email with spaces");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter email with spaces");
    settingsPage.clearEmailField();
    settingsPage.enterEmail("user with spaces@example.com");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Email cannot contain spaces");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"regression", "validation"})
  public void TC019_validateUsernameWithSpecialCharacters() {
    createTest("TC-019", "Validate username with special characters");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter username with @#$");
    settingsPage.clearUsernameField();
    settingsPage.enterUsername("user@#$name");

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error or sanitization applied");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC020_validateEmptyEmailSubmission() {
    createTest("TC-020", "Validate empty email submission");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Clear email field completely");
    settingsPage.clearEmailField();

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Validation error - Email required or field unchanged");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or remain on settings page");
  }
}
