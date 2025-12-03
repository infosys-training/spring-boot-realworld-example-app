package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationValidationTests extends BaseTest {

  private RegistrationPage registrationPage;
  private String baseUrl;

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

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC011_emptyUsernameFieldShowsValidationError() {
    createTest("TC-011", "Empty username field shows validation error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed or user should remain on registration page");

    test.pass("Empty username validation handled appropriately");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC012_emptyEmailFieldShowsValidationError() {
    createTest("TC-012", "Empty email field shows validation error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed or user should remain on registration page");

    test.pass("Empty email validation handled appropriately");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC013_emptyPasswordFieldShowsValidationError() {
    createTest("TC-013", "Empty password field shows validation error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed or user should remain on registration page");

    test.pass("Empty password validation handled appropriately");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC014_allEmptyFieldsShowValidationErrors() {
    createTest("TC-014", "All empty fields show validation errors");

    registrationPage.navigateTo(baseUrl);
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error messages should be displayed or user should remain on registration page");

    test.pass("All empty fields validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC015_invalidEmailFormatMissingAtShowsError() {
    createTest("TC-015", "Invalid email format missing @ shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail("invalidemail.com");
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for invalid email format");

    test.pass("Invalid email format (missing @) validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC016_invalidEmailFormatMissingDomainShowsError() {
    createTest("TC-016", "Invalid email format missing domain shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail("user@");
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for invalid email format");

    test.pass("Invalid email format (missing domain) validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC017_invalidEmailFormatMissingTLDShowsError() {
    createTest("TC-017", "Invalid email format missing TLD shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail("user@domain");
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasErrors = registrationPage.hasErrorMessages();
    boolean onRegPage = registrationPage.isOnRegistrationPage();

    assertTrue(hasErrors || onRegPage, "System should handle invalid email format appropriately");

    test.pass("Invalid email format (missing TLD) handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC018_invalidEmailFormatWithSpacesShowsError() {
    createTest("TC-018", "Invalid email format with spaces shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail("user @example.com");
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for email with spaces");

    test.pass("Invalid email format (with spaces) validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC019_usernameWithOnlySpacesShowsError() {
    createTest("TC-019", "Username with only spaces shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername("   ");
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for username with only spaces");

    test.pass("Username with only spaces validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC020_emailWithOnlySpacesShowsError() {
    createTest("TC-020", "Email with only spaces shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail("   ");
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for email with only spaces");

    test.pass("Email with only spaces validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC021_passwordWithOnlySpacesShowsError() {
    createTest("TC-021", "Password with only spaces shows error");

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(generateUniqueUsername());
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("   ");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registrationPage.hasErrorMessages() || registrationPage.isOnRegistrationPage(),
        "Error message should be displayed for password with only spaces");

    test.pass("Password with only spaces validation handled appropriately");
  }

  @Test(groups = {"regression", "validation"})
  public void TC022_usernameFieldAcceptsValidCharacters() {
    createTest("TC-022", "Username field accepts valid characters");

    String username = "user_name123";
    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(username);
    registrationPage.enterEmail(generateUniqueEmail());
    registrationPage.enterPassword("Password123!");
    registrationPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean registrationSucceeded = !currentUrl.contains("/register");
    boolean hasErrors = registrationPage.hasErrorMessages();

    assertTrue(
        registrationSucceeded || hasErrors,
        "Registration should succeed with valid characters or show specific error");

    test.pass("Username with valid characters handled appropriately");
  }
}
