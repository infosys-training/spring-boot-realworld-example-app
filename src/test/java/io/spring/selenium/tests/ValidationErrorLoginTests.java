package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationErrorLoginTests extends BaseTest {

  private LoginPage loginPage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    loginPage = new LoginPage(driver);
  }

  @Test(
      groups = {"smoke", "validation", "login"},
      description = "TC-014: Verify error for empty login form submission")
  public void testTC014_EmptyLoginFormSubmission() {
    createTest(
        "TC-014: Verify error for empty login form submission",
        "Verify that submitting empty login form shows validation error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.clickSignIn();
    test.info("Clicked Sign in without entering any data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorDisplayed() || loginPage.getErrorCount() > 0,
        "Validation error should be displayed for empty form");
    test.info("Login validation errors displayed: " + loginPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "login"},
      description = "TC-015: Verify error for invalid email format on login")
  public void testTC015_InvalidEmailFormatOnLogin() {
    createTest(
        "TC-015: Verify error for invalid email format on login",
        "Verify that invalid email format shows appropriate error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.enterEmail("notanemail").enterPassword("password123").clickSignIn();
    test.info("Submitted login with invalid email format");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    test.info("Invalid email error displayed: " + loginPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "login"},
      description = "TC-016: Verify error for non-existent email")
  public void testTC016_NonExistentEmailError() {
    createTest(
        "TC-016: Verify error for non-existent email",
        "Verify that non-existent email shows invalid credentials error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.enterEmail("nonexistent@test.com").enterPassword("password123").clickSignIn();
    test.info("Submitted login with non-existent email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        loginPage.hasErrorContaining("email")
            || loginPage.hasErrorContaining("password")
            || loginPage.hasErrorContaining("invalid"),
        "Error should indicate invalid credentials");
    test.info("Non-existent email error displayed: " + loginPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "login"},
      description = "TC-017: Verify error for incorrect password")
  public void testTC017_IncorrectPasswordError() {
    createTest(
        "TC-017: Verify error for incorrect password",
        "Verify that incorrect password shows invalid credentials error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.enterEmail("john@example.com").enterPassword("wrongpassword").clickSignIn();
    test.info("Submitted login with incorrect password for existing user");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        loginPage.hasErrorContaining("email")
            || loginPage.hasErrorContaining("password")
            || loginPage.hasErrorContaining("invalid"),
        "Error should indicate invalid credentials");
    test.info("Incorrect password error displayed: " + loginPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "login"},
      description = "TC-018: Verify error for empty email field on login")
  public void testTC018_EmptyEmailFieldOnLogin() {
    createTest(
        "TC-018: Verify error for empty email field on login",
        "Verify that empty email field shows required error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.clearEmail().enterPassword("password123").clickSignIn();
    test.info("Submitted login with empty email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        loginPage.hasErrorContaining("email")
            || loginPage.hasErrorContaining("required")
            || loginPage.hasErrorContaining("blank"),
        "Error should indicate email is required");
    test.info("Empty email error displayed: " + loginPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "login"},
      description = "TC-019: Verify error for empty password field on login")
  public void testTC019_EmptyPasswordFieldOnLogin() {
    createTest(
        "TC-019: Verify error for empty password field on login",
        "Verify that empty password field shows required error");

    loginPage.navigateTo(baseUrl);
    test.info("Navigated to login page");

    loginPage.enterEmail("john@example.com").clearPassword().clickSignIn();
    test.info("Submitted login with empty password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        loginPage.hasErrorContaining("password")
            || loginPage.hasErrorContaining("required")
            || loginPage.hasErrorContaining("blank"),
        "Error should indicate password is required");
    test.info("Empty password error displayed: " + loginPage.getErrorMessages());
  }
}
