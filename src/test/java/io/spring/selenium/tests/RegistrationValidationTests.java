package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationValidationTests extends BaseTest {

  private RegisterPage registerPage;
  private HomePage homePage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
    homePage = new HomePage(driver);
    homePage.navigateTo(baseUrl);
    homePage.clearLocalStorage();
  }

  @Test(groups = {"regression", "validation"})
  public void TC009_testEmptyUsernameValidation() {
    createTest(
        "TC-009: Empty username validation", "Verify error when submitting with empty username");

    registerPage.navigateTo(baseUrl);
    registerPage.enterEmail("test@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page or show error for empty username");

    test.info("Empty username validation works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC010_testEmptyEmailValidation() {
    createTest("TC-010: Empty email validation", "Verify error when submitting with empty email");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page or show error for empty email");

    test.info("Empty email validation works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC011_testEmptyPasswordValidation() {
    createTest(
        "TC-011: Empty password validation", "Verify error when submitting with empty password");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser");
    registerPage.enterEmail("test@example.com");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page or show error for empty password");

    test.info("Empty password validation works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC012_testAllFieldsEmptyValidation() {
    createTest(
        "TC-012: All fields empty validation",
        "Verify error when submitting with all fields empty");

    registerPage.navigateTo(baseUrl);
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page or show error for all empty fields");

    test.info("All fields empty validation works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC013_testInvalidEmailFormatNoAtSymbol() {
    createTest(
        "TC-013: Invalid email format - no @ symbol", "Verify error for email without @ symbol");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser");
    registerPage.enterEmail("invalidemail.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page for invalid email format");

    test.info("Invalid email format validation (no @) works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC014_testInvalidEmailFormatNoDomain() {
    createTest("TC-014: Invalid email format - no domain", "Verify error for email without domain");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser");
    registerPage.enterEmail("test@");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page for invalid email format");

    test.info("Invalid email format validation (no domain) works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC015_testInvalidEmailFormatNoLocalPart() {
    createTest(
        "TC-015: Invalid email format - no local part",
        "Verify error for email without local part");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser");
    registerPage.enterEmail("@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should stay on register page for invalid email format");

    test.info("Invalid email format validation (no local part) works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void TC016_testUsernamePlaceholderText() {
    createTest(
        "TC-016: Username field placeholder text", "Verify username field has correct placeholder");

    registerPage.navigateTo(baseUrl);

    String placeholder = registerPage.getUsernamePlaceholder();
    assertEquals(placeholder, "Username", "Username placeholder should be 'Username'");

    test.info("Username placeholder is correct: " + placeholder);
  }

  @Test(groups = {"regression", "validation"})
  public void TC017_testEmailPlaceholderText() {
    createTest(
        "TC-017: Email field placeholder text", "Verify email field has correct placeholder");

    registerPage.navigateTo(baseUrl);

    String placeholder = registerPage.getEmailPlaceholder();
    assertEquals(placeholder, "Email", "Email placeholder should be 'Email'");

    test.info("Email placeholder is correct: " + placeholder);
  }

  @Test(groups = {"regression", "validation"})
  public void TC018_testPasswordPlaceholderText() {
    createTest(
        "TC-018: Password field placeholder text", "Verify password field has correct placeholder");

    registerPage.navigateTo(baseUrl);

    String placeholder = registerPage.getPasswordPlaceholder();
    assertEquals(placeholder, "Password", "Password placeholder should be 'Password'");

    test.info("Password placeholder is correct: " + placeholder);
  }
}
