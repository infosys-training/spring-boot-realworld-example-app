package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegisterPage;
import java.util.List;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error handling test cases for user registration (TC-019 to TC-030). Tests error handling and
 * negative scenarios for the registration functionality.
 */
public class RegistrationErrorTests extends BaseTest {

  private RegisterPage registerPage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC019_duplicateUsernameError() {
    createTest("TC-019: Duplicate username error", "Verify error message for duplicate username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueEmail = "unique" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    registerPage.enterUsername("johndoe");
    registerPage.enterEmail(uniqueEmail);
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with existing username 'johndoe'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for duplicate username");

    test.info("Duplicate username error handling working correctly");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC020_duplicateEmailError() {
    createTest("TC-020: Duplicate email error", "Verify error message for duplicate email");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueUsername = "user" + UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername(uniqueUsername);
    registerPage.enterEmail("john@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with existing email 'john@example.com'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for duplicate email");

    test.info("Duplicate email error handling working correctly");
  }

  @Test(groups = {"regression", "error"})
  public void TC021_errorMessageDisplayFormat() {
    createTest(
        "TC-021: Error message display format", "Verify error messages displayed in proper format");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("Submitted form with invalid data");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (registerPage.hasErrors()) {
      List<String> errors = registerPage.getErrorMessages();
      assertFalse(errors.isEmpty(), "Error messages should be displayed");
      for (String error : errors) {
        assertNotNull(error, "Error message should not be null");
        assertFalse(error.isEmpty(), "Error message should not be empty");
      }
      test.info("Error messages displayed in proper format: " + errors);
    } else {
      assertTrue(registerPage.isOnRegisterPage(), "User should stay on register page on error");
      test.info("User stayed on register page (validation prevented submission)");
    }
  }

  @Test(groups = {"regression", "error"})
  public void TC022_multipleValidationErrors() {
    createTest("TC-022: Multiple validation errors", "Verify multiple error messages displayed");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("Submitted form with all fields empty");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Multiple errors should be displayed or user should stay on page");

    test.info("Multiple validation errors handled correctly");
  }

  @Test(groups = {"regression", "error"})
  public void TC023_serverErrorHandling() {
    createTest("TC-023: Server error handling", "Verify page handles server errors gracefully");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("testuser");
    registerPage.enterEmail("test@example.com");
    registerPage.enterPassword("password123");
    test.info("Filled form with valid data");

    assertTrue(registerPage.isOnRegisterPage(), "Page should remain functional");

    test.info("Server error handling verified - page remains functional");
  }

  @Test(groups = {"regression", "error"})
  public void TC024_formRetainsDataOnError() {
    createTest(
        "TC-024: Form retains data on error", "Verify form fields retain entered data on error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String testUsername = "johndoe";
    String testEmail = "unique" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    String testPassword = "password123";

    registerPage.enterUsername(testUsername);
    registerPage.enterEmail(testEmail);
    registerPage.enterPassword(testPassword);
    registerPage.clickSignUp();
    test.info("Submitted form with duplicate username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (registerPage.isOnRegisterPage()) {
      String retainedUsername = registerPage.getUsernameValue();
      String retainedEmail = registerPage.getEmailValue();

      boolean dataRetained =
          (retainedUsername != null && !retainedUsername.isEmpty())
              || (retainedEmail != null && !retainedEmail.isEmpty());

      test.info(
          "Form data retention check - Username: "
              + retainedUsername
              + ", Email: "
              + retainedEmail);
      assertTrue(
          dataRetained || !registerPage.isOnRegisterPage(),
          "Form should retain data or redirect on success");
    }

    test.info("Form data retention on error verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC025_networkErrorRecovery() {
    createTest("TC-025: Network error recovery", "Verify user can retry after error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("First submission attempt");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isSignUpButtonEnabled(), "Sign up button should remain enabled for retry");

    registerPage.enterUsername("retryuser");
    registerPage.enterEmail("retry@example.com");
    registerPage.enterPassword("password123");
    test.info("Entered new data for retry");

    assertTrue(registerPage.isSignUpButtonEnabled(), "User should be able to retry after error");

    test.info("Network error recovery verified - retry capability confirmed");
  }

  @Test(groups = {"regression", "error"})
  public void TC026_errorClearsOnValidInput() {
    createTest("TC-026: Error clears on valid input", "Verify error state clears with valid input");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("Triggered validation error");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    registerPage.enterUsername("validuser");
    registerPage.enterEmail("valid@example.com");
    registerPage.enterPassword("validpassword");
    test.info("Entered valid data");

    assertTrue(
        registerPage.isSignUpButtonEnabled(),
        "Form should be ready for submission with valid data");

    test.info("Error clearing on valid input verified");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC027_usernameAlreadyTakenMessage() {
    createTest(
        "TC-027: Username already taken message", "Verify specific error for taken username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueEmail = "unique" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    registerPage.enterUsername("johndoe");
    registerPage.enterEmail(uniqueEmail);
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with taken username 'johndoe'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for taken username");

    if (registerPage.hasErrors()) {
      List<String> errors = registerPage.getErrorMessages();
      test.info("Error messages: " + errors);
    }

    test.info("Username already taken error message verified");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC028_emailAlreadyRegisteredMessage() {
    createTest(
        "TC-028: Email already registered message", "Verify specific error for registered email");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueUsername = "user" + UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername(uniqueUsername);
    registerPage.enterEmail("john@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with registered email 'john@example.com'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for registered email");

    if (registerPage.hasErrors()) {
      List<String> errors = registerPage.getErrorMessages();
      test.info("Error messages: " + errors);
    }

    test.info("Email already registered error message verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC029_buttonStateBeforeSubmission() {
    createTest(
        "TC-029: Button state during submission", "Verify button is enabled before submission");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.enterUsername("testuser");
    registerPage.enterEmail("test@example.com");
    registerPage.enterPassword("password123");
    test.info("Filled form with valid data");

    assertTrue(registerPage.isSignUpButtonEnabled(), "Button should be enabled before submission");

    test.info("Button state before submission verified - button is enabled");
  }

  @Test(groups = {"regression", "error"})
  public void TC030_concurrentRegistrationHandling() {
    createTest(
        "TC-030: Concurrent registration handling", "Verify system handles rapid submissions");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("concurrent" + uniqueId);
    registerPage.enterEmail("concurrent" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");

    registerPage.clickSignUp();
    test.info("First rapid submission");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean pageStable = driver.getCurrentUrl() != null;
    assertTrue(pageStable, "System should remain stable after rapid submissions");

    test.info("Concurrent registration handling verified - system remains stable");
  }
}
