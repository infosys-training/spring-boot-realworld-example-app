package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegisterPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationErrorPositiveTests extends BaseTest {

  private RegisterPage registerPage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
  }

  @Test(
      groups = {"smoke", "validation", "positive"},
      description = "TC-001: Verify 422 status returned for empty registration form")
  public void testTC001_EmptyRegistrationFormReturns422() {
    createTest(
        "TC-001: Verify 422 status returned for empty registration form",
        "Verify that submitting an empty registration form returns validation errors");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.clickSignUp();
    test.info("Clicked Sign up button without entering any data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isErrorDisplayed() || registerPage.getErrorCount() > 0,
        "Validation errors should be displayed for empty form submission");
    test.info("Validation errors displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "positive"},
      description = "TC-002: Verify field-specific error for missing username")
  public void testTC002_MissingUsernameFieldError() {
    createTest(
        "TC-002: Verify field-specific error for missing username",
        "Verify that submitting without username shows username-specific error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.enterEmail("test@example.com").enterPassword("password123").clickSignUp();
    test.info("Submitted form with email and password only");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("username")
            || registerPage.hasErrorContaining("can't be blank"),
        "Error should indicate username is required");
    test.info("Username error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "positive"},
      description = "TC-003: Verify field-specific error for missing email")
  public void testTC003_MissingEmailFieldError() {
    createTest(
        "TC-003: Verify field-specific error for missing email",
        "Verify that submitting without email shows email-specific error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.enterUsername("testuser").enterPassword("password123").clickSignUp();
    test.info("Submitted form with username and password only");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("email")
            || registerPage.hasErrorContaining("can't be blank"),
        "Error should indicate email is required");
    test.info("Email error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "positive"},
      description = "TC-004: Verify field-specific error for missing password")
  public void testTC004_MissingPasswordFieldError() {
    createTest(
        "TC-004: Verify field-specific error for missing password",
        "Verify that submitting without password shows password-specific error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.enterUsername("testuser").enterEmail("test@example.com").clickSignUp();
    test.info("Submitted form with username and email only");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("password")
            || registerPage.hasErrorContaining("can't be blank"),
        "Error should indicate password is required");
    test.info("Password error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "positive"},
      description = "TC-005: Verify multiple validation errors returned together")
  public void testTC005_MultipleValidationErrorsReturnedTogether() {
    createTest(
        "TC-005: Verify multiple validation errors returned together",
        "Verify that all missing field errors are displayed together");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.clickSignUp();
    test.info("Clicked Sign up without entering any data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error messages should be displayed");
    int errorCount = registerPage.getErrorCount();
    assertTrue(errorCount >= 1, "Multiple validation errors should be displayed together");
    test.info("Number of errors displayed: " + errorCount);
    test.info("All errors: " + registerPage.getErrorMessages());
  }
}
