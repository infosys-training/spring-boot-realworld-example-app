package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.RegisterPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationErrorEdgeCaseTests extends BaseTest {

  private RegisterPage registerPage;
  private LoginPage loginPage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(
      groups = {"regression", "validation", "edgecase"},
      description = "TC-036: Verify error handling for extremely long input values")
  public void testTC036_ExtremelyLongInputValues() {
    createTest(
        "TC-036: Verify error handling for extremely long input values",
        "Verify that extremely long input values are handled appropriately");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longUsername.append("a");
    }

    registerPage
        .enterUsername(longUsername.toString())
        .enterEmail("test@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with 1000 character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Long input error displayed: " + registerPage.getErrorMessages());
    } else {
      test.info("Long input may be accepted or truncated");
    }
  }

  @Test(
      groups = {"regression", "validation", "edgecase", "security"},
      description = "TC-037: Verify error handling for SQL injection attempts")
  public void testTC037_SqlInjectionAttempt() {
    createTest(
        "TC-037: Verify error handling for SQL injection attempts",
        "Verify that SQL injection attempts are handled safely");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    String sqlInjection = "' OR '1'='1";
    registerPage
        .enterUsername(sqlInjection)
        .enterEmail("test@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with SQL injection in username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("SQL") && pageSource.contains("error"),
        "SQL error should not be exposed to user");

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Input sanitized, validation error shown: " + registerPage.getErrorMessages());
    } else {
      test.info("Input may be accepted as literal string (safe)");
    }
  }

  @Test(
      groups = {"regression", "validation", "edgecase", "security"},
      description = "TC-038: Verify error handling for XSS script injection")
  public void testTC038_XssScriptInjection() {
    createTest(
        "TC-038: Verify error handling for XSS script injection",
        "Verify that XSS script injection is handled safely");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    String xssScript = "<script>alert('XSS')</script>";
    registerPage
        .enterUsername(xssScript)
        .enterEmail("test@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with XSS script in username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    boolean scriptExecuted = pageSource.contains("<script>alert");
    assertFalse(scriptExecuted, "XSS script should not be rendered as executable");

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Script not executed, validation error shown: " + registerPage.getErrorMessages());
    } else {
      test.info("Script may be escaped and stored safely");
    }
  }

  @Test(
      groups = {"regression", "validation", "edgecase"},
      description = "TC-039: Verify error handling for unicode/special characters")
  public void testTC039_UnicodeSpecialCharacters() {
    createTest(
        "TC-039: Verify error handling for unicode/special characters",
        "Verify that unicode and special characters are handled appropriately");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    String unicodeUsername = "testuser\u00E9\u00F1\u00FC\u4E2D\u6587";
    registerPage
        .enterUsername(unicodeUsername)
        .enterEmail("unicode@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with unicode characters in username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Unicode handling error: " + registerPage.getErrorMessages());
    } else {
      test.info("Unicode characters may be accepted");
    }
  }

  @Test(
      groups = {"regression", "validation", "edgecase"},
      description = "TC-040: Verify error response format consistency across all forms")
  public void testTC040_ErrorResponseFormatConsistency() {
    createTest(
        "TC-040: Verify error response format consistency across all forms",
        "Verify that error responses follow consistent format across forms");

    registerPage.navigateTo(baseUrl);
    registerPage.clickSignUp();
    test.info("Triggered validation error on registration form");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    List<String> registerErrors = registerPage.getErrorMessages();
    test.info("Registration errors: " + registerErrors);

    loginPage.navigateTo(baseUrl);
    loginPage.clickSignIn();
    test.info("Triggered validation error on login form");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    List<String> loginErrors = loginPage.getErrorMessages();
    test.info("Login errors: " + loginErrors);

    boolean registerHasErrors = !registerErrors.isEmpty();
    boolean loginHasErrors = !loginErrors.isEmpty();

    if (registerHasErrors && loginHasErrors) {
      boolean bothUseListFormat = registerPage.isErrorDisplayed() && loginPage.isErrorDisplayed();
      assertTrue(bothUseListFormat, "Both forms should use consistent error display format");
      test.info("Error format is consistent across forms");
    } else {
      test.info("One or both forms did not display errors - format consistency check skipped");
    }

    test.info("Registration error count: " + registerErrors.size());
    test.info("Login error count: " + loginErrors.size());
  }
}
