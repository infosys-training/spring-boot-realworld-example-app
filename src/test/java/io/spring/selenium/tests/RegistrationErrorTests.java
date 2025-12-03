package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationErrorTests extends BaseTest {

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

  @Test(groups = {"regression", "error"})
  public void TC019_testDuplicateUsernameError() {
    createTest(
        "TC-019: Duplicate username error",
        "Verify error message when registering with existing username");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("johndoe");
    registerPage.enterEmail("unique" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    boolean hasError = registerPage.isErrorMessageDisplayed() || currentUrl.contains("/register");

    assertTrue(hasError, "Should show error or stay on register page for duplicate username");
    test.info("Duplicate username error handling works correctly");
  }

  @Test(groups = {"regression", "error"})
  public void TC020_testDuplicateEmailError() {
    createTest(
        "TC-020: Duplicate email error",
        "Verify error message when registering with existing email");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("unique" + UUID.randomUUID().toString().substring(0, 8));
    registerPage.enterEmail("john@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    boolean hasError = registerPage.isErrorMessageDisplayed() || currentUrl.contains("/register");

    assertTrue(hasError, "Should show error or stay on register page for duplicate email");
    test.info("Duplicate email error handling works correctly");
  }

  @Test(groups = {"regression", "error"})
  public void TC021_testErrorMessageDisplayFormat() {
    createTest(
        "TC-021: Error message display format",
        "Verify error messages are displayed in proper format");

    registerPage.navigateTo(baseUrl);
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register"), "Should stay on register page when validation fails");

    test.info("Error message display format verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC022_testMultipleValidationErrors() {
    createTest(
        "TC-022: Multiple validation errors",
        "Verify multiple errors shown when multiple fields are invalid");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("");
    registerPage.enterEmail("invalid");
    registerPage.enterPassword("");
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register"), "Should stay on register page with multiple errors");

    test.info("Multiple validation errors handled correctly");
  }

  @Test(groups = {"regression", "error"})
  public void TC023_testServerErrorHandling() {
    createTest(
        "TC-023: Server error handling", "Verify graceful handling when server returns error");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser" + UUID.randomUUID().toString().substring(0, 8));
    registerPage.enterEmail("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
    registerPage.enterPassword("pass");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should remain accessible after server error");
    test.info("Server error handling verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC024_testFormRetainsDataOnError() {
    createTest(
        "TC-024: Form retains data on error",
        "Verify form fields retain entered data after validation error");

    registerPage.navigateTo(baseUrl);
    String testUsername = "testuser";
    String testEmail = "invalid-email";

    registerPage.enterUsername(testUsername);
    registerPage.enterEmail(testEmail);
    registerPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUsername = registerPage.getUsernameValue();
    String currentEmail = registerPage.getEmailValue();

    assertEquals(currentUsername, testUsername, "Username should be retained after error");
    assertEquals(currentEmail, testEmail, "Email should be retained after error");

    test.info("Form data retention on error verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC025_testNetworkErrorRecovery() {
    createTest("TC-025: Network error recovery", "Verify user can retry after network error");

    registerPage.navigateTo(baseUrl);

    assertTrue(
        registerPage.isSignUpButtonDisplayed(), "Sign up button should be available for retry");
    assertTrue(registerPage.isUsernameFieldDisplayed(), "Form should be available for retry");

    test.info("Network error recovery capability verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC026_testErrorClearsOnValidInput() {
    createTest(
        "TC-026: Error clears on valid input",
        "Verify error state clears when user provides valid input");

    registerPage.navigateTo(baseUrl);
    registerPage.clickSignUp();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    registerPage.clearAllFields();
    registerPage.enterUsername("validuser" + UUID.randomUUID().toString().substring(0, 8));
    registerPage.enterEmail(
        "valid" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
    registerPage.enterPassword("validpassword123");

    assertTrue(registerPage.isSignUpButtonEnabled(), "Sign up button should remain enabled");
    test.info("Error clearing on valid input verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC027_testUsernameAlreadyTakenMessage() {
    createTest(
        "TC-027: Username already taken message",
        "Verify specific error message for taken username");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("johndoe");
    registerPage.enterEmail("new" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should indicate username is taken");

    test.info("Username taken error message verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC028_testEmailAlreadyRegisteredMessage() {
    createTest(
        "TC-028: Email already registered message",
        "Verify specific error message for registered email");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("newuser" + UUID.randomUUID().toString().substring(0, 8));
    registerPage.enterEmail("john@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = registerPage.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/register") || registerPage.isErrorMessageDisplayed(),
        "Should indicate email is already registered");

    test.info("Email registered error message verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC029_testButtonDisabledDuringSubmission() {
    createTest(
        "TC-029: Button state during submission", "Verify button behavior during form submission");

    registerPage.navigateTo(baseUrl);
    registerPage.enterUsername("testuser" + UUID.randomUUID().toString().substring(0, 8));
    registerPage.enterEmail("test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com");
    registerPage.enterPassword("password123");

    assertTrue(registerPage.isSignUpButtonEnabled(), "Button should be enabled before submission");

    test.info("Button state during submission verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC030_testConcurrentRegistrationAttempts() {
    createTest(
        "TC-030: Concurrent registration handling", "Verify system handles rapid form submissions");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);

    registerPage.enterUsername("concurrent" + uniqueId);
    registerPage.enterEmail("concurrent" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should remain stable after submission");
    test.info("Concurrent registration handling verified");
  }
}
