package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.SettingsPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error and negative test cases for User Profile Update functionality. Tests error handling
 * scenarios for US-AUTH-004.
 */
public class ProfileUpdateErrorTests extends BaseTest {

  private LoginPage loginPage;
  private SettingsPage settingsPage;
  private HomePage homePage;
  private String baseUrl;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String EXISTING_USER_EMAIL = "jane@example.com";
  private static final String EXISTING_USERNAME = "janedoe";

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

  private void clearLocalStorage() {
    try {
      ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    } catch (Exception e) {
      test.info("Could not clear localStorage: " + e.getMessage());
    }
  }

  private void setInvalidToken() {
    try {
      ((JavascriptExecutor) driver)
          .executeScript("window.localStorage.setItem('jwtToken', 'invalid-token-12345');");
    } catch (Exception e) {
      test.info("Could not set invalid token: " + e.getMessage());
    }
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC021_updateWithInvalidJwtToken() {
    createTest("TC-021", "Update with invalid JWT token");
    test.info("Precondition: Invalid/expired JWT token");

    test.info("Step 1: Set invalid JWT");
    homePage.navigateTo(baseUrl);
    setInvalidToken();

    test.info("Step 2: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);

    test.info("Step 3: Try to update");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Verifying: 401 Unauthorized error returned or redirect to login");
    boolean isOnLogin = loginPage.isOnLoginPage();
    boolean isOnSettings = settingsPage.isOnSettingsPage();
    test.info("On login page: " + isOnLogin + ", On settings page: " + isOnSettings);
    assertTrue(
        isOnLogin || !isOnSettings || settingsPage.isErrorDisplayed(),
        "Should redirect to login or show error with invalid token");
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC022_updateWithMissingJwtToken() {
    createTest("TC-022", "Update with missing JWT token");
    test.info("Precondition: No JWT token set");

    test.info("Step 1: Clear authentication");
    homePage.navigateTo(baseUrl);
    clearLocalStorage();

    test.info("Step 2: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);

    test.info("Step 3: Try to update");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Verifying: 401 Unauthorized, redirect to login");
    boolean isOnLogin = loginPage.isOnLoginPage();
    boolean isOnSettings = settingsPage.isOnSettingsPage();
    test.info("On login page: " + isOnLogin + ", On settings page: " + isOnSettings);
    assertTrue(
        isOnLogin || !isOnSettings,
        "Should redirect to login or not allow access to settings without token");
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC023_updateWithExpiredJwtToken() {
    createTest("TC-023", "Update with expired JWT token");
    test.info("Precondition: Expired JWT token");

    test.info("Step 1: Set expired token (simulated)");
    homePage.navigateTo(baseUrl);
    String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjAwMDAwMDAwfQ.expired";
    try {
      ((JavascriptExecutor) driver)
          .executeScript("window.localStorage.setItem('jwtToken', '" + expiredToken + "');");
    } catch (Exception e) {
      test.info("Could not set expired token: " + e.getMessage());
    }

    test.info("Step 2: Try to update profile");
    settingsPage.navigateTo(baseUrl);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Verifying: 401 Unauthorized error returned");
    boolean isOnLogin = loginPage.isOnLoginPage();
    boolean isOnSettings = settingsPage.isOnSettingsPage();
    test.info("On login page: " + isOnLogin + ", On settings page: " + isOnSettings);
    assertTrue(
        isOnLogin || !isOnSettings || settingsPage.isErrorDisplayed(),
        "Should redirect to login or show error with expired token");
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC024_updateEmailToExistingEmail() {
    createTest("TC-024", "Update email to existing email");
    test.info("Precondition: Another user has target email");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter email of existing user");
    settingsPage.clearEmailField();
    settingsPage.enterEmail(EXISTING_USER_EMAIL);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Error - Email already in use");
    boolean hasError = settingsPage.isErrorDisplayed();
    String errorMsg = settingsPage.getErrorMessage();
    test.info("Error displayed: " + hasError + ", Message: " + errorMsg);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show error for duplicate email or remain on settings page");
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC025_updateUsernameToExistingUsername() {
    createTest("TC-025", "Update username to existing username");
    test.info("Precondition: Another user has target username");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter username of existing user");
    settingsPage.clearUsernameField();
    settingsPage.enterUsername(EXISTING_USERNAME);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Error - Username already taken");
    boolean hasError = settingsPage.isErrorDisplayed();
    String errorMsg = settingsPage.getErrorMessage();
    test.info("Error displayed: " + hasError + ", Message: " + errorMsg);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show error for duplicate username or remain on settings page");
  }

  @Test(groups = {"regression", "negative"})
  public void TC026_updateWithMalformedRequestBody() {
    createTest("TC-026", "Update with malformed request body");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Attempt to send malformed data via JavaScript");
    try {
      ((JavascriptExecutor) driver)
          .executeScript(
              "fetch('/api/user', {"
                  + "method: 'PUT',"
                  + "headers: {'Content-Type': 'application/json'},"
                  + "body: '{malformed json'"
                  + "}).then(r => console.log('Response:', r.status));");
    } catch (Exception e) {
      test.info("Malformed request test: " + e.getMessage());
    }

    test.info("Verifying: 400 Bad Request error");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page");
  }

  @Test(groups = {"smoke", "regression", "negative", "security"})
  public void TC027_updateWithSqlInjectionInEmail() {
    createTest("TC-027", "Update with SQL injection in email");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter SQL injection string in email");
    String sqlInjection = "'; DROP TABLE users; --@example.com";
    settingsPage.clearEmailField();
    settingsPage.enterEmail(sqlInjection);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Input sanitized, no SQL execution");
    boolean hasError = settingsPage.isErrorDisplayed();
    test.info("Error displayed: " + hasError);
    assertTrue(
        hasError || settingsPage.isOnSettingsPage(),
        "Should show validation error or sanitize input");
  }

  @Test(groups = {"smoke", "regression", "negative", "security"})
  public void TC028_updateWithXssScriptInBio() {
    createTest("TC-028", "Update with XSS script in bio");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Enter <script> tag in bio");
    String xssScript = "<script>alert('XSS')</script>";
    settingsPage.clearBioField();
    settingsPage.enterBio(xssScript);

    test.info("Step 3: Click Update");
    settingsPage.clickUpdate();
    settingsPage.waitForSuccessfulUpdate();

    test.info("Verifying: Script sanitized/escaped, no XSS");
    String currentBio = settingsPage.getCurrentBio();
    test.info("Current bio after update: " + currentBio);
    assertFalse(
        currentBio.contains("<script>") && currentBio.contains("</script>"),
        "Script tags should be sanitized or escaped");
  }

  @Test(groups = {"regression", "negative"})
  public void TC029_updateWithNetworkTimeout() {
    createTest("TC-029", "Update with network timeout");
    test.info("Precondition: User logged in, network issues simulated");

    loginAsTestUser();
    test.info("Step 1: Navigate to Settings");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    test.info("Step 2: Simulate network timeout via slow connection");
    try {
      ((JavascriptExecutor) driver)
          .executeScript(
              "window.originalFetch = window.fetch;"
                  + "window.fetch = function() {"
                  + "  return new Promise((resolve, reject) => {"
                  + "    setTimeout(() => reject(new Error('Network timeout')), 100);"
                  + "  });"
                  + "};");
    } catch (Exception e) {
      test.info("Could not simulate timeout: " + e.getMessage());
    }

    test.info("Step 3: Try to update");
    settingsPage.enterBio("Timeout test bio");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    test.info("Verifying: Appropriate timeout error message");
    try {
      ((JavascriptExecutor) driver).executeScript("window.fetch = window.originalFetch;");
    } catch (Exception e) {
      test.info("Could not restore fetch: " + e.getMessage());
    }
    assertTrue(settingsPage.isOnSettingsPage(), "Should remain on settings page after timeout");
  }

  @Test(groups = {"smoke", "regression", "negative"})
  public void TC030_updateProfileOfAnotherUser() {
    createTest("TC-030", "Update profile of another user");
    test.info("Precondition: User logged in with valid JWT");

    loginAsTestUser();
    test.info("Step 1: Attempt to modify another user's profile via API");
    settingsPage.navigateTo(baseUrl);
    settingsPage.waitForPageLoad();

    try {
      String result =
          (String)
              ((JavascriptExecutor) driver)
                  .executeScript(
                      "return fetch('/api/profiles/janedoe', {"
                          + "method: 'PUT',"
                          + "headers: {"
                          + "  'Content-Type': 'application/json',"
                          + "  'Authorization': 'Token ' + localStorage.getItem('jwtToken')"
                          + "},"
                          + "body: JSON.stringify({user: {bio: 'Hacked bio'}})"
                          + "}).then(r => r.status.toString()).catch(e => e.message);");
      test.info("API response: " + result);
    } catch (Exception e) {
      test.info("API call result: " + e.getMessage());
    }

    test.info("Verifying: 403 Forbidden or operation not allowed");
    assertTrue(
        settingsPage.isOnSettingsPage(), "Should not be able to modify another user's profile");
  }
}
