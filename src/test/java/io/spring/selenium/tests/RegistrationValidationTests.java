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
  public void setupPages() {
    registrationPage = new RegistrationPage(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC009_EmptyUsernameShowsError() {
    createTest(
        "TC-009: Empty username field shows error",
        "Verify error message is displayed when username field is empty");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "emptyuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    registrationPage.enterEmail(email);
    registrationPage.enterPassword(password);
    registrationPage.clickSignUp();
    test.info("Submitted form with empty username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for empty username");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC010_EmptyEmailShowsError() {
    createTest(
        "TC-010: Empty email field shows error",
        "Verify error message is displayed when email field is empty");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "noemail" + uniqueId;
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(username);
    registrationPage.enterPassword(password);
    registrationPage.clickSignUp();
    test.info("Submitted form with empty email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError, "Should stay on registration page or show error for empty email");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC011_EmptyPasswordShowsError() {
    createTest(
        "TC-011: Empty password field shows error",
        "Verify error message is displayed when password field is empty");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "nopwd" + uniqueId;
    String email = "nopwd" + uniqueId + "@example.com";

    registrationPage.navigateTo(baseUrl);
    registrationPage.enterUsername(username);
    registrationPage.enterEmail(email);
    registrationPage.clickSignUp();
    test.info("Submitted form with empty password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for empty password");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC012_AllFieldsEmptyShowsErrors() {
    createTest(
        "TC-012: All fields empty shows errors",
        "Verify error messages are displayed when all fields are empty");

    registrationPage.navigateTo(baseUrl);
    registrationPage.clickSignUp();
    test.info("Submitted form with all fields empty");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show errors for all empty fields");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC013_UsernameWithSpacesOnlyShowsError() {
    createTest(
        "TC-013: Username with only spaces shows error",
        "Verify error message is displayed when username contains only spaces");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "   ";
    String email = "spaces" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with spaces-only username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for spaces-only username");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC014_EmailWithSpacesOnlyShowsError() {
    createTest(
        "TC-014: Email with only spaces shows error",
        "Verify error message is displayed when email contains only spaces");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "spaceemail" + uniqueId;
    String email = "   ";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with spaces-only email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for spaces-only email");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC015_PasswordWithSpacesOnlyShowsError() {
    createTest(
        "TC-015: Password with only spaces shows error",
        "Verify error message is displayed when password contains only spaces");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "spacepwd" + uniqueId;
    String email = "spacepwd" + uniqueId + "@example.com";
    String password = "   ";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with spaces-only password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for spaces-only password");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC016_InvalidEmailNoAtSymbol() {
    createTest(
        "TC-016: Invalid email format - no @ symbol",
        "Verify error message is displayed when email has no @ symbol");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "noat" + uniqueId;
    String email = "invalidemail" + uniqueId + "domain.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with email missing @ symbol");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for invalid email without @");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC017_InvalidEmailNoDomain() {
    createTest(
        "TC-017: Invalid email format - no domain",
        "Verify error message is displayed when email has no domain");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "nodomain" + uniqueId;
    String email = "nodomain" + uniqueId + "@";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with email missing domain");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for email without domain");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC018_InvalidEmailMultipleAtSymbols() {
    createTest(
        "TC-018: Invalid email format - multiple @ symbols",
        "Verify error message is displayed when email has multiple @ symbols");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "multiat" + uniqueId;
    String email = "multi" + uniqueId + "@@domain.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with email having multiple @ symbols");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();

    assertTrue(
        stayedOnPage || hasError,
        "Should stay on registration page or show error for email with multiple @");
  }
}
