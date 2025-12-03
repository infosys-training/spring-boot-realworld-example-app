package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error handling test cases for User Login functionality (TC-021 to TC-030). Tests error handling
 * and negative scenarios for US-AUTH-002: User Login.
 */
public class LoginErrorTests extends BaseTest {

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";
  private static final String INVALID_EMAIL = "nonexistent@example.com";
  private static final String INVALID_PASSWORD = "wrongpassword";
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  /** TC-021: Invalid email displays error message */
  @Test(groups = {"regression", "error"})
  public void testTC021_InvalidEmailDisplaysErrorMessage() {
    createTest(
        "TC-021: Invalid email displays error message",
        "Verify error message when non-existent email is used");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError(INVALID_EMAIL, VALID_PASSWORD);

    assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");

    String errorText = loginPage.getErrorMessageText().toLowerCase();
    assertTrue(
        errorText.contains("invalid")
            || errorText.contains("email")
            || errorText.contains("password"),
        "Error message should indicate invalid credentials");

    test.info("Invalid email correctly displays error message");
  }

  /** TC-022: Invalid password displays error message */
  @Test(groups = {"regression", "error"})
  public void testTC022_InvalidPasswordDisplaysErrorMessage() {
    createTest(
        "TC-022: Invalid password displays error message",
        "Verify error message when wrong password is used");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError(VALID_EMAIL, INVALID_PASSWORD);

    assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");

    String errorText = loginPage.getErrorMessageText().toLowerCase();
    assertTrue(
        errorText.contains("invalid")
            || errorText.contains("email")
            || errorText.contains("password"),
        "Error message should indicate invalid credentials");

    test.info("Invalid password correctly displays error message");
  }

  /** TC-023: Non-existent user displays error message */
  @Test(groups = {"regression", "error"})
  public void testTC023_NonExistentUserDisplaysErrorMessage() {
    createTest(
        "TC-023: Non-existent user displays error message",
        "Verify error message for non-existent user");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError("nonexistent@example.com", "anypassword");

    assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");

    test.info("Non-existent user correctly displays error message");
  }

  /** TC-024: Correct email with wrong password shows error */
  @Test(groups = {"regression", "error"})
  public void testTC024_CorrectEmailWrongPasswordShowsError() {
    createTest(
        "TC-024: Correct email with wrong password shows error",
        "Verify error when valid email is used with wrong password");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError(VALID_EMAIL, "wrongpassword");

    assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/login"), "User should remain on login page");

    test.info("Correct email with wrong password correctly shows error");
  }

  /** TC-025: Wrong email with correct password shows error */
  @Test(groups = {"regression", "error"})
  public void testTC025_WrongEmailCorrectPasswordShowsError() {
    createTest(
        "TC-025: Wrong email with correct password shows error",
        "Verify error when wrong email is used with valid password");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError("wrong@example.com", VALID_PASSWORD);

    assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/login"), "User should remain on login page");

    test.info("Wrong email with correct password correctly shows error");
  }

  /** TC-026: Empty form submission shows validation errors */
  @Test(groups = {"regression", "error"})
  public void testTC026_EmptyFormSubmissionShowsValidationErrors() {
    createTest(
        "TC-026: Empty form submission shows validation errors",
        "Verify validation errors when form is submitted empty");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.clearAllFields();
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnLoginPage = currentUrl.contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Empty form should show validation errors or prevent submission");

    test.info("Empty form submission handled correctly");
  }

  /** TC-027: SQL injection attempt handled safely */
  @Test(groups = {"security", "error"})
  public void testTC027_SqlInjectionAttemptHandledSafely() {
    createTest(
        "TC-027: SQL injection attempt handled safely",
        "Verify SQL injection in email field is handled safely");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("' OR '1'='1");
    loginPage.enterPassword("' OR '1'='1");
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnLoginPage = currentUrl.contains("/login");

    assertTrue(stayedOnLoginPage, "SQL injection should not bypass login");

    String pageSource = loginPage.getPageSource().toLowerCase();
    assertFalse(pageSource.contains("sql"), "No SQL error should be exposed");
    assertFalse(pageSource.contains("exception"), "No exception details should be exposed");

    test.info("SQL injection attempt handled safely");
  }

  /** TC-028: XSS attempt in email field handled safely */
  @Test(groups = {"security", "error"})
  public void testTC028_XssAttemptInEmailFieldHandledSafely() {
    createTest(
        "TC-028: XSS attempt in email field handled safely",
        "Verify XSS script in email field is not executed");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("<script>alert('xss')</script>@test.com");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = loginPage.getPageSource();
    assertFalse(
        pageSource.contains("<script>alert('xss')</script>"),
        "XSS script should be sanitized or escaped");

    test.info("XSS attempt handled safely");
  }

  /** TC-029: Error message does not reveal if email exists */
  @Test(groups = {"security", "error"})
  public void testTC029_ErrorMessageDoesNotRevealEmailExists() {
    createTest(
        "TC-029: Error message does not reveal if email exists",
        "Verify same error message for existing and non-existing emails");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.loginExpectingError(VALID_EMAIL, INVALID_PASSWORD);
    String errorWithValidEmail = loginPage.getErrorMessageText();

    loginPage.navigateTo(baseUrl);
    loginPage.loginExpectingError(INVALID_EMAIL, INVALID_PASSWORD);
    String errorWithInvalidEmail = loginPage.getErrorMessageText();

    assertEquals(
        errorWithValidEmail,
        errorWithInvalidEmail,
        "Error messages should be identical for security (not reveal if email exists)");

    test.info("Error message does not reveal if email exists");
  }

  /** TC-030: Multiple failed login attempts handled */
  @Test(groups = {"regression", "error"})
  public void testTC030_MultipleFailedLoginAttemptsHandled() {
    createTest(
        "TC-030: Multiple failed login attempts handled",
        "Verify system handles multiple failed login attempts gracefully");

    LoginPage loginPage = new LoginPage(driver);

    for (int i = 0; i < 5; i++) {
      loginPage.navigateTo(baseUrl);
      loginPage.loginExpectingError(VALID_EMAIL, INVALID_PASSWORD);

      assertTrue(
          loginPage.isErrorMessageDisplayed(),
          "Error message should be displayed on attempt " + (i + 1));

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertTrue(
        loginPage.isEmailInputDisplayed(),
        "Login form should still be functional after multiple failed attempts");

    test.info("Multiple failed login attempts handled gracefully");
  }
}
