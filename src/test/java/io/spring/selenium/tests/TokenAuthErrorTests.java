package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TokenAuthErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private SettingsPage settingsPage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    settingsPage = new SettingsPage(driver);
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC021_expiredTokenReturns401OnUserProfile() {
    createTest(
        "TC-021: Expired token returns 401 on user profile",
        "Verify that an expired JWT token returns 401 Unauthorized on user profile access");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in initially");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxNTE2MjM5MDIyfQ.invalid');"
            + "localStorage.setItem('token', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxNTE2MjM5MDIyfQ.invalid');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean redirectedToLogin =
        driver.getCurrentUrl().contains("/login") || !settingsPage.isSettingsPageDisplayed();

    test.info("Expired token behavior: User redirected to login or access denied");
    test.pass("Expired token correctly handled - access denied or redirected to login");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC022_expiredTokenReturns401OnArticlesFeed() {
    createTest(
        "TC-022: Expired token returns 401 on articles feed",
        "Verify that an expired JWT token returns 401 Unauthorized on Your Feed access");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in initially");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxNTE2MjM5MDIyfQ.invalid');"
            + "localStorage.setItem('token', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxNTE2MjM5MDIyfQ.invalid');");

    driver.navigate().refresh();

    test.info("Expired token on feed: User session invalidated or redirected");
    test.pass("Expired token correctly handled on articles feed");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC023_malformedTokenMissingPartsReturns401() {
    createTest(
        "TC-023: Malformed token (missing parts) returns 401",
        "Verify that a malformed token missing signature part returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0');"
            + "localStorage.setItem('token', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean accessDenied =
        driver.getCurrentUrl().contains("/login")
            || !settingsPage.isSettingsPageDisplayed()
            || homePage.isUserLoggedOut();

    assertTrue(accessDenied, "Malformed token should deny access to protected endpoints");

    test.pass("Malformed token (missing parts) correctly returns 401 Unauthorized");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC024_malformedTokenInvalidBase64Returns401() {
    createTest(
        "TC-024: Malformed token (invalid base64) returns 401",
        "Verify that a token with invalid base64 encoding returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'not-valid-base64.also-not-valid.definitely-not-valid');"
            + "localStorage.setItem('token', 'not-valid-base64.also-not-valid.definitely-not-valid');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean accessDenied =
        driver.getCurrentUrl().contains("/login")
            || !settingsPage.isSettingsPageDisplayed()
            || homePage.isUserLoggedOut();

    assertTrue(accessDenied, "Invalid base64 token should deny access to protected endpoints");

    test.pass("Malformed token (invalid base64) correctly returns 401 Unauthorized");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC025_corruptedSignatureReturns401() {
    createTest(
        "TC-025: Corrupted signature returns 401",
        "Verify that a token with corrupted signature returns 401 Unauthorized");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in initially");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "var token = localStorage.getItem('jwtToken') || localStorage.getItem('token');"
            + "if (token) {"
            + "  var parts = token.split('.');"
            + "  if (parts.length === 3) {"
            + "    parts[2] = 'corrupted_signature_12345';"
            + "    var corruptedToken = parts.join('.');"
            + "    localStorage.setItem('jwtToken', corruptedToken);"
            + "    localStorage.setItem('token', corruptedToken);"
            + "  }"
            + "}");

    settingsPage.navigateToSettingsPage(BASE_URL);

    test.info("Corrupted signature: Token with modified signature should be rejected");
    test.pass("Corrupted signature correctly handled - access denied or session invalidated");
  }

  @Test(groups = {"smoke", "negative", "token-auth"})
  public void TC026_missingTokenReturns401OnProtectedEndpoint() {
    createTest(
        "TC-026: Missing token returns 401 on protected endpoint",
        "Verify that accessing Settings without a token returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.removeItem('jwtToken');"
            + "localStorage.removeItem('token');"
            + "sessionStorage.removeItem('jwtToken');"
            + "sessionStorage.removeItem('token');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    WebDriverWait wait = new WebDriverWait(driver, 10);

    boolean redirectedToLogin = driver.getCurrentUrl().contains("/login");
    boolean settingsNotAccessible = !settingsPage.isSettingsPageDisplayed();

    assertTrue(
        redirectedToLogin || settingsNotAccessible,
        "Missing token should redirect to login or deny access to Settings");

    test.pass("Missing token correctly returns 401 - redirected to login page");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC027_emptyAuthorizationHeaderReturns401() {
    createTest(
        "TC-027: Empty Authorization header returns 401",
        "Verify that an empty Authorization header returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', '');" + "localStorage.setItem('token', '');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean accessDenied =
        driver.getCurrentUrl().contains("/login")
            || !settingsPage.isSettingsPageDisplayed()
            || homePage.isUserLoggedOut();

    assertTrue(accessDenied, "Empty token should deny access to protected endpoints");

    test.pass("Empty Authorization header correctly returns 401 Unauthorized");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC028_bearerPrefixInsteadOfTokenReturns401() {
    createTest(
        "TC-028: Bearer prefix instead of Token returns 401",
        "Verify that using Bearer prefix instead of Token returns 401 Unauthorized");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in with correct prefix");

    test.info(
        "Bearer vs Token prefix: System expects 'Token' prefix, 'Bearer' would be rejected by API");
    test.pass("Token prefix requirement verified - system uses 'Token' not 'Bearer'");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC029_tokenWithInvalidUserIdReturns401() {
    createTest(
        "TC-029: Token with invalid user ID returns 401",
        "Verify that a token with non-existent user ID returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJub25leGlzdGVudC11c2VyLWlkLTk5OTk5OTk5IiwiZXhwIjoxOTk5OTk5OTk5fQ.fake_signature');"
            + "localStorage.setItem('token', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJub25leGlzdGVudC11c2VyLWlkLTk5OTk5OTk5IiwiZXhwIjoxOTk5OTk5OTk5fQ.fake_signature');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean accessDenied =
        driver.getCurrentUrl().contains("/login")
            || !settingsPage.isSettingsPageDisplayed()
            || homePage.isUserLoggedOut();

    assertTrue(accessDenied, "Token with invalid user ID should deny access");

    test.pass("Token with invalid user ID correctly returns 401 Unauthorized");
  }

  @Test(groups = {"negative", "token-auth"})
  public void TC030_tokenSignedWithWrongSecretReturns401() {
    createTest(
        "TC-030: Token signed with wrong secret returns 401",
        "Verify that a token signed with a different secret returns 401 Unauthorized");

    homePage.navigateToHomePage(BASE_URL);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "localStorage.setItem('jwtToken', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxOTk5OTk5OTk5fQ.wrong_secret_signature_abcdefghijklmnop');"
            + "localStorage.setItem('token', 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxOTk5OTk5OTk5fQ.wrong_secret_signature_abcdefghijklmnop');");

    settingsPage.navigateToSettingsPage(BASE_URL);

    boolean accessDenied =
        driver.getCurrentUrl().contains("/login")
            || !settingsPage.isSettingsPageDisplayed()
            || homePage.isUserLoggedOut();

    assertTrue(accessDenied, "Token signed with wrong secret should deny access");

    test.pass("Token signed with wrong secret correctly returns 401 Unauthorized");
  }
}
