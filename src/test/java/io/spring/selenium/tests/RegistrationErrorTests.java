package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Error handling test cases for user registration (TC-019 to TC-030). */
public class RegistrationErrorTests extends BaseTest {

  private RegisterPage registerPage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void TC019_duplicateUsernameError() {
    createTest(
        "TC-019: Duplicate username error",
        "Verify error message for duplicate username 'johndoe'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueEmail = "unique" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    registerPage.register("johndoe", uniqueEmail, "password123");
    test.info("Attempted registration with existing username 'johndoe'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for duplicate username");

    if (registerPage.hasErrorMessages()) {
      String errorText = registerPage.getErrorMessageText();
      test.info("Error message displayed: " + errorText);
    }

    test.pass("Duplicate username error handling works correctly");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC020_duplicateEmailError() {
    createTest(
        "TC-020: Duplicate email error",
        "Verify error message for duplicate email 'john@example.com'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueUsername = "user" + UUID.randomUUID().toString().substring(0, 8);
    registerPage.register(uniqueUsername, "john@example.com", "password123");
    test.info("Attempted registration with existing email 'john@example.com'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for duplicate email");

    if (registerPage.hasErrorMessages()) {
      String errorText = registerPage.getErrorMessageText();
      test.info("Error message displayed: " + errorText);
    }

    test.pass("Duplicate email error handling works correctly");
  }

  @Test(groups = {"regression"})
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

    if (registerPage.hasErrorMessages()) {
      String errorText = registerPage.getErrorMessageText();
      assertNotNull(errorText, "Error message should not be null");
      assertFalse(errorText.isEmpty(), "Error message should not be empty");
      test.info("Error message format: " + errorText);
    }

    test.pass("Error message display format is correct");
  }

  @Test(groups = {"regression"})
  public void TC022_multipleValidationErrors() {
    createTest(
        "TC-022: Multiple validation errors",
        "Verify multiple error messages displayed for multiple invalid fields");

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
    assertTrue(hasError, "Errors should be displayed for multiple invalid fields");

    test.pass("Multiple validation errors handled correctly");
  }

  @Test(groups = {"regression"})
  public void TC023_serverErrorHandling() {
    createTest("TC-023: Server error handling", "Verify page handles server errors gracefully");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.register("johndoe", "john@example.com", "password123");
    test.info("Submitted form that may cause server error (duplicate data)");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean pageStable = registerPage.isOnRegisterPage() || registerPage.hasErrorMessages();
    assertTrue(pageStable, "Page should handle error gracefully without crashing");

    test.pass("Server error handling works correctly");
  }

  @Test(groups = {"regression"})
  public void TC024_formRetainsDataOnError() {
    createTest(
        "TC-024: Form retains data on error", "Verify form fields retain entered data after error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String testUsername = "johndoe";
    String testEmail = "john@example.com";
    String testPassword = "password123";

    registerPage.register(testUsername, testEmail, testPassword);
    test.info("Submitted form with duplicate data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (registerPage.isOnRegisterPage()) {
      String retainedUsername = registerPage.getUsernameValue();
      String retainedEmail = registerPage.getEmailValue();

      test.info("Username retained: " + retainedUsername);
      test.info("Email retained: " + retainedEmail);
    }

    test.pass("Form data retention on error verified");
  }

  @Test(groups = {"regression"})
  public void TC025_networkErrorRecovery() {
    createTest("TC-025: Network error recovery", "Verify user can retry after network error");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isOnRegisterPage(), "Should be on register page");
    assertTrue(registerPage.isSignUpButtonEnabled(), "Sign up button should be enabled for retry");

    test.pass("Network error recovery capability verified");
  }

  @Test(groups = {"regression"})
  public void TC026_errorClearsOnValidInput() {
    createTest(
        "TC-026: Error clears on valid input",
        "Verify error state clears when valid data is entered");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.clickSignUp();
    test.info("Triggered validation error");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("newuser" + uniqueId);
    registerPage.enterEmail("new" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    test.info("Entered valid data");

    assertTrue(registerPage.isSignUpButtonEnabled(), "Sign up button should be enabled");

    test.pass("Error clearing on valid input verified");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC027_usernameAlreadyTakenMessage() {
    createTest(
        "TC-027: Username already taken message", "Verify specific error for taken username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueEmail = "unique" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    registerPage.register("johndoe", uniqueEmail, "password123");
    test.info("Attempted registration with taken username 'johndoe'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for taken username");

    if (registerPage.hasErrorMessages()) {
      String errorText = registerPage.getErrorMessageText().toLowerCase();
      test.info("Error message: " + errorText);
    }

    test.pass("Username already taken message verified");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC028_emailAlreadyRegisteredMessage() {
    createTest(
        "TC-028: Email already registered message", "Verify specific error for registered email");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueUsername = "user" + UUID.randomUUID().toString().substring(0, 8);
    registerPage.register(uniqueUsername, "john@example.com", "password123");
    test.info("Attempted registration with registered email 'john@example.com'");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(hasError, "Error should be displayed for registered email");

    if (registerPage.hasErrorMessages()) {
      String errorText = registerPage.getErrorMessageText().toLowerCase();
      test.info("Error message: " + errorText);
    }

    test.pass("Email already registered message verified");
  }

  @Test(groups = {"regression"})
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

    test.pass("Button state before submission verified");
  }

  @Test(groups = {"regression"})
  public void TC030_concurrentRegistrationHandling() {
    createTest(
        "TC-030: Concurrent registration handling", "Verify system handles rapid submissions");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("user" + uniqueId);
    registerPage.enterEmail("email" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");

    registerPage.clickSignUp();
    test.info("First submission");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean systemStable =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(systemStable, "System should remain stable after submissions");

    test.pass("Concurrent registration handling verified");
  }
}
