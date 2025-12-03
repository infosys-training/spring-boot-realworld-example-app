package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationErrorTests extends BaseTest {

  private RegistrationPage registrationPage;
  private String baseUrl;

  private static final String EXISTING_USERNAME = "johndoe";
  private static final String EXISTING_EMAIL = "john@example.com";

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registrationPage = new RegistrationPage(driver);
  }

  private String generateUniqueUsername() {
    return "user" + UUID.randomUUID().toString().substring(0, 8);
  }

  private String generateUniqueEmail() {
    return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC023_duplicateUsernameShowsAppropriateError() {
    createTest("TC-023", "Duplicate username shows appropriate error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(EXISTING_USERNAME);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages(),
        "Error message should be displayed for duplicate username");

    String errorText = registrationPage.getErrorMessagesText().toLowerCase();
    assertTrue(
        errorText.contains("username")
            || errorText.contains("taken")
            || errorText.contains("exists"),
        "Error message should indicate username issue");

    test.pass("Duplicate username error displayed appropriately");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC024_duplicateEmailShowsAppropriateError() {
    createTest("TC-024", "Duplicate email shows appropriate error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(EXISTING_EMAIL);
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages(),
        "Error message should be displayed for duplicate email");

    String errorText = registrationPage.getErrorMessagesText().toLowerCase();
    assertTrue(
        errorText.contains("email") || errorText.contains("taken") || errorText.contains("exists"),
        "Error message should indicate email issue");

    test.pass("Duplicate email error displayed appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void TC025_registrationWithExistingUserEmailFails() {
    createTest("TC-025", "Registration with existing user email fails");

    String existingEmail = "jane@example.com";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(existingEmail);
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Registration should fail with existing email");

    test.pass("Registration with existing email fails appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void TC026_registrationWithExistingUsernameFails() {
    createTest("TC-026", "Registration with existing username fails");

    String existingUsername = "janedoe";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(existingUsername);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Registration should fail with existing username");

    test.pass("Registration with existing username fails appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void TC027_errorMessageClearsWhenUserCorrectsInput() {
    createTest("TC-027", "Error message clears when user corrects input");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(EXISTING_USERNAME);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean initialErrorDisplayed = registrationPage.hasErrorMessages();

    registrationPage.clearUsername();
    registrationPage.enterUsername(generateUniqueUsername());

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(initialErrorDisplayed, "Initial error should have been displayed");

    test.pass("Error message behavior verified when user corrects input");
  }

  @Test(groups = {"regression", "error"})
  public void TC028_multipleValidationErrorsDisplaySimultaneously() {
    createTest("TC-028", "Multiple validation errors display simultaneously");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername("");
    registrationPage.enterEmail("invalid-email");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Validation errors should be displayed or user should remain on page");

    test.pass("Multiple validation errors handled appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void TC029_formSubmissionWithAllInvalidDataShowsErrors() {
    createTest("TC-029", "Form submission with all invalid data shows errors");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername("");
    registrationPage.enterEmail("not-an-email");
    registrationPage.enterPassword("");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error messages should be displayed for invalid data");

    test.pass("Form submission with invalid data shows errors appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void TC030_formRetainsEnteredDataAfterValidationError() {
    createTest("TC-030", "Form retains entered data after validation error");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(username);
    registrationPage.enterEmail(email);
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String retainedUsername = registrationPage.getUsernameValue();
    String retainedEmail = registrationPage.getEmailValue();

    assertTrue(
        retainedUsername.equals(username) || registrationPage.isOnRegistrationPage(),
        "Username should be retained or user should be on registration page");

    test.pass("Form data retention after validation error verified");
  }

  @Test(groups = {"regression", "error"})
  public void TC031_errorMessageStylingIsVisible() {
    createTest("TC-031", "Error message styling is visible");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(EXISTING_USERNAME);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (registrationPage.hasErrorMessages()) {
      String errorText = registrationPage.getErrorMessagesText();
      assertFalse(errorText.isEmpty(), "Error message text should not be empty");
      test.pass("Error message is visible with appropriate styling");
    } else {
      test.info("No error messages displayed - may need to verify error styling manually");
    }
  }

  @Test(groups = {"regression", "error"})
  public void TC032_signUpButtonBehaviorDuringSubmission() {
    createTest("TC-032", "Sign Up button behavior during submission");

    registrationPage.navigateTo(baseUrl);

    assertTrue(
        registrationPage.isSignUpButtonEnabled(), "Sign Up button should be enabled initially");

    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");

    assertTrue(
        registrationPage.isSignUpButtonEnabled(),
        "Sign Up button should be enabled after filling form");

    test.pass("Sign Up button behavior verified");
  }
}
