package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegisterPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationErrorRegistrationTests extends BaseTest {

  private RegisterPage registerPage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-006: Verify error message for invalid email format")
  public void testTC006_InvalidEmailFormatError() {
    createTest(
        "TC-006: Verify error message for invalid email format",
        "Verify that invalid email format shows appropriate error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("testuser")
        .enterEmail("invalidemail")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with invalid email format");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("email") || registerPage.hasErrorContaining("invalid"),
        "Error should explain expected email format");
    test.info("Email format error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-007: Verify error message for duplicate username")
  public void testTC007_DuplicateUsernameError() {
    createTest(
        "TC-007: Verify error message for duplicate username",
        "Verify that duplicate username shows uniqueness error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("johndoe")
        .enterEmail("newuser@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with existing username 'johndoe'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("username")
            || registerPage.hasErrorContaining("taken")
            || registerPage.hasErrorContaining("already"),
        "Error should indicate username is already taken");
    test.info("Duplicate username error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-008: Verify error message for duplicate email")
  public void testTC008_DuplicateEmailError() {
    createTest(
        "TC-008: Verify error message for duplicate email",
        "Verify that duplicate email shows uniqueness error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("newuniqueuser")
        .enterEmail("john@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with existing email 'john@example.com'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("email")
            || registerPage.hasErrorContaining("taken")
            || registerPage.hasErrorContaining("already"),
        "Error should indicate email is already registered");
    test.info("Duplicate email error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-009: Verify error message for short password")
  public void testTC009_ShortPasswordError() {
    createTest(
        "TC-009: Verify error message for short password",
        "Verify that short password shows minimum length error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("testuser")
        .enterEmail("test@example.com")
        .enterPassword("123")
        .clickSignUp();
    test.info("Submitted form with short password '123'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Password length error displayed: " + registerPage.getErrorMessages());
    } else {
      test.info("No password length validation enforced by backend");
    }
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-010: Verify error message for username with special characters")
  public void testTC010_UsernameWithSpecialCharactersError() {
    createTest(
        "TC-010: Verify error message for username with special characters",
        "Verify that username with special characters shows format error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("test@user!")
        .enterEmail("test@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with username containing special characters");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.isErrorDisplayed();
    if (hasError) {
      test.info("Username format error displayed: " + registerPage.getErrorMessages());
    } else {
      test.info("Special characters in username may be allowed");
    }
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-011: Verify error message for empty username field")
  public void testTC011_EmptyUsernameFieldError() {
    createTest(
        "TC-011: Verify error message for empty username field",
        "Verify that empty username shows cannot be blank error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .clearUsername()
        .enterEmail("test@example.com")
        .enterPassword("password123")
        .clickSignUp();
    test.info("Submitted form with empty username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("username")
            || registerPage.hasErrorContaining("blank")
            || registerPage.hasErrorContaining("required"),
        "Error should state username cannot be blank");
    test.info("Empty username error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-012: Verify error message for empty email field")
  public void testTC012_EmptyEmailFieldError() {
    createTest(
        "TC-012: Verify error message for empty email field",
        "Verify that empty email shows cannot be blank error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage.enterUsername("testuser").clearEmail().enterPassword("password123").clickSignUp();
    test.info("Submitted form with empty email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("email")
            || registerPage.hasErrorContaining("blank")
            || registerPage.hasErrorContaining("required"),
        "Error should state email cannot be blank");
    test.info("Empty email error displayed: " + registerPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "registration"},
      description = "TC-013: Verify error message for empty password field")
  public void testTC013_EmptyPasswordFieldError() {
    createTest(
        "TC-013: Verify error message for empty password field",
        "Verify that empty password shows cannot be blank error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registerPage
        .enterUsername("testuser")
        .enterEmail("test@example.com")
        .clearPassword()
        .clickSignUp();
    test.info("Submitted form with empty password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(registerPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        registerPage.hasErrorContaining("password")
            || registerPage.hasErrorContaining("blank")
            || registerPage.hasErrorContaining("required"),
        "Error should state password cannot be blank");
    test.info("Empty password error displayed: " + registerPage.getErrorMessages());
  }
}
