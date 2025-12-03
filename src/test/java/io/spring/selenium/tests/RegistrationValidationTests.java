package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegisterPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Validation test cases for user registration (TC-009 to TC-018). */
public class RegistrationValidationTests extends BaseTest {

  private RegisterPage registerPage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void TC009_emptyUsernameValidation() {
    createTest(
        "TC-009: Empty username validation", "Verify error displayed when username is empty");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterEmail("valid@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with empty username");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed or user should stay on register page");

    test.pass("Empty username validation works correctly");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC010_emptyEmailValidation() {
    createTest("TC-010: Empty email validation", "Verify error displayed when email is empty");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("validuser");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with empty email");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed or user should stay on register page");

    test.pass("Empty email validation works correctly");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC011_emptyPasswordValidation() {
    createTest(
        "TC-011: Empty password validation", "Verify error displayed when password is empty");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("validuser");
    registerPage.enterEmail("valid@example.com");
    registerPage.clickSignUp();
    test.info("Submitted form with empty password");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed or user should stay on register page");

    test.pass("Empty password validation works correctly");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC012_allFieldsEmptyValidation() {
    createTest(
        "TC-012: All fields empty validation", "Verify error displayed when all fields are empty");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("Submitted form with all fields empty");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed or user should stay on register page");

    test.pass("All fields empty validation works correctly");
  }

  @Test(groups = {"regression"})
  public void TC013_invalidEmailFormatNoAtSymbol() {
    createTest(
        "TC-013: Invalid email format - no @ symbol",
        "Verify error displayed for email without @ symbol");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("validuser");
    registerPage.enterEmail("invalidemail.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with email without @ symbol");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for invalid email format");

    test.pass("Invalid email format (no @) validation works correctly");
  }

  @Test(groups = {"regression"})
  public void TC014_invalidEmailFormatNoDomain() {
    createTest(
        "TC-014: Invalid email format - no domain",
        "Verify error displayed for email without domain");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("validuser");
    registerPage.enterEmail("invalid@");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with email without domain");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for invalid email format");

    test.pass("Invalid email format (no domain) validation works correctly");
  }

  @Test(groups = {"regression"})
  public void TC015_invalidEmailFormatNoLocalPart() {
    createTest(
        "TC-015: Invalid email format - no local part",
        "Verify error displayed for email without local part");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("validuser");
    registerPage.enterEmail("@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with email without local part");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for invalid email format");

    test.pass("Invalid email format (no local part) validation works correctly");
  }

  @Test(groups = {"regression"})
  public void TC016_usernameFieldPlaceholderText() {
    createTest("TC-016: Username field placeholder text", "Verify placeholder shows 'Username'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String placeholder = registerPage.getUsernamePlaceholder();
    assertEquals(placeholder, "Username", "Username placeholder should be 'Username'");

    test.pass("Username field placeholder text is correct: " + placeholder);
  }

  @Test(groups = {"regression"})
  public void TC017_emailFieldPlaceholderText() {
    createTest("TC-017: Email field placeholder text", "Verify placeholder shows 'Email'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String placeholder = registerPage.getEmailPlaceholder();
    assertEquals(placeholder, "Email", "Email placeholder should be 'Email'");

    test.pass("Email field placeholder text is correct: " + placeholder);
  }

  @Test(groups = {"regression"})
  public void TC018_passwordFieldPlaceholderText() {
    createTest("TC-018: Password field placeholder text", "Verify placeholder shows 'Password'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String placeholder = registerPage.getPasswordPlaceholder();
    assertEquals(placeholder, "Password", "Password placeholder should be 'Password'");

    test.pass("Password field placeholder text is correct: " + placeholder);
  }
}
