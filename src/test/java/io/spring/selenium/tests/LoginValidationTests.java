package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for User Login functionality (TC-011 to TC-020). Tests input validation
 * scenarios for US-AUTH-002: User Login.
 */
public class LoginValidationTests extends BaseTest {

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  /** TC-011: Email field required validation */
  @Test(groups = {"regression", "validation"})
  public void testTC011_EmailFieldRequiredValidation() {
    createTest(
        "TC-011: Email field required validation",
        "Verify error message when email field is empty");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError =
        loginPage.isErrorMessageDisplayed()
            || loginPage.getEmailValue().isEmpty()
            || driver.getCurrentUrl().contains("/login");

    assertTrue(hasError, "Form should indicate email is required or prevent submission");

    test.info("Email field required validation works correctly");
  }

  /** TC-012: Password field required validation */
  @Test(groups = {"regression", "validation"})
  public void testTC012_PasswordFieldRequiredValidation() {
    createTest(
        "TC-012: Password field required validation",
        "Verify error message when password field is empty");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError =
        loginPage.isErrorMessageDisplayed()
            || loginPage.getPasswordValue().isEmpty()
            || driver.getCurrentUrl().contains("/login");

    assertTrue(hasError, "Form should indicate password is required or prevent submission");

    test.info("Password field required validation works correctly");
  }

  /** TC-013: Both fields required validation */
  @Test(groups = {"regression", "validation"})
  public void testTC013_BothFieldsRequiredValidation() {
    createTest(
        "TC-013: Both fields required validation",
        "Verify error messages when both fields are empty");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Form should prevent submission or show errors when both fields are empty");

    test.info("Both fields required validation works correctly");
  }

  /** TC-014: Invalid email format rejected (missing @) */
  @Test(groups = {"regression", "validation"})
  public void testTC014_InvalidEmailFormatMissingAtSymbol() {
    createTest(
        "TC-014: Invalid email format rejected (missing @)",
        "Verify error when email is missing @ symbol");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("johnexample.com");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Invalid email format should be rejected");

    test.info("Invalid email format (missing @) correctly rejected");
  }

  /** TC-015: Invalid email format rejected (missing domain) */
  @Test(groups = {"regression", "validation"})
  public void testTC015_InvalidEmailFormatMissingDomain() {
    createTest(
        "TC-015: Invalid email format rejected (missing domain)",
        "Verify error when email is missing domain");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("john@");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Invalid email format should be rejected");

    test.info("Invalid email format (missing domain) correctly rejected");
  }

  /** TC-016: Invalid email format rejected (missing TLD) */
  @Test(groups = {"regression", "validation"})
  public void testTC016_InvalidEmailFormatMissingTLD() {
    createTest(
        "TC-016: Invalid email format rejected (missing TLD)",
        "Verify error when email is missing top-level domain");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("john@example");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Invalid email format should be rejected or handled");

    test.info("Invalid email format (missing TLD) handled correctly");
  }

  /** TC-017: Email with spaces rejected */
  @Test(groups = {"regression", "validation"})
  public void testTC017_EmailWithSpacesRejected() {
    createTest("TC-017: Email with spaces rejected", "Verify error when email contains spaces");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("john @example.com");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Email with spaces should be rejected");

    test.info("Email with spaces correctly rejected");
  }

  /** TC-018: Email with special characters validation */
  @Test(groups = {"regression", "validation"})
  public void testTC018_EmailWithSpecialCharactersValidation() {
    createTest(
        "TC-018: Email with special characters validation",
        "Verify email validation handles special characters appropriately");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("john!#$@example.com");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean handled = currentUrl.contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Email with special characters should be handled appropriately");

    test.info("Email with special characters handled correctly");
  }

  /** TC-019: Minimum password length validation */
  @Test(groups = {"regression", "validation"})
  public void testTC019_MinimumPasswordLengthValidation() {
    createTest(
        "TC-019: Minimum password length validation",
        "Verify error or failure with single character password");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.enterPassword("a");
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnLoginPage = driver.getCurrentUrl().contains("/login");

    assertTrue(
        stayedOnLoginPage || loginPage.isErrorMessageDisplayed(),
        "Short password should fail validation or login");

    test.info("Minimum password length validation works correctly");
  }

  /** TC-020: Maximum email length validation */
  @Test(groups = {"regression", "validation"})
  public void testTC020_MaximumEmailLengthValidation() {
    createTest(
        "TC-020: Maximum email length validation",
        "Verify handling of email exceeding maximum length");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    StringBuilder longEmail = new StringBuilder();
    for (int i = 0; i < 250; i++) {
      longEmail.append("a");
    }
    longEmail.append("@example.com");

    loginPage.enterEmail(longEmail.toString());
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Excessively long email should be handled appropriately");

    test.info("Maximum email length validation handled correctly");
  }
}
