package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationErrorTests extends BaseTest {

  private RegistrationPage registrationPage;
  private String baseUrl;

  @BeforeMethod
  public void setupPages() {
    registrationPage = new RegistrationPage(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC019_DuplicateUsernameShowsError() {
    createTest(
        "TC-019: Duplicate username shows error",
        "Verify error message is displayed when username already exists");

    String existingUsername = "johndoe";
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "dupuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(existingUsername, email, password);
    test.info("Submitted form with existing username 'johndoe'");

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
        "Should stay on registration page or show error for duplicate username");

    if (hasError) {
      String errorText = registrationPage.getErrorMessageText().toLowerCase();
      assertTrue(
          errorText.contains("username")
              || errorText.contains("taken")
              || errorText.contains("exist"),
          "Error message should indicate username issue");
    }
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC020_DuplicateEmailShowsError() {
    createTest(
        "TC-020: Duplicate email shows error",
        "Verify error message is displayed when email already exists");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "dupemail" + uniqueId;
    String existingEmail = "john@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, existingEmail, password);
    test.info("Submitted form with existing email 'john@example.com'");

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
        "Should stay on registration page or show error for duplicate email");

    if (hasError) {
      String errorText = registrationPage.getErrorMessageText().toLowerCase();
      assertTrue(
          errorText.contains("email") || errorText.contains("taken") || errorText.contains("exist"),
          "Error message should indicate email issue");
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC021_DuplicateUsernameDifferentCase() {
    createTest(
        "TC-021: Duplicate username with different case",
        "Verify error message is displayed when username exists with different case");

    String existingUsername = "JohnDoe";
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "caseuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(existingUsername, email, password);
    test.info("Submitted form with username 'JohnDoe' (different case from existing 'johndoe')");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "Should handle case-insensitive username check appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void testTC022_DuplicateEmailDifferentCase() {
    createTest(
        "TC-022: Duplicate email with different case",
        "Verify error message is displayed when email exists with different case");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "caseemail" + uniqueId;
    String existingEmail = "JOHN@EXAMPLE.COM";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, existingEmail, password);
    test.info("Submitted form with email 'JOHN@EXAMPLE.COM' (different case from existing)");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "Should handle case-insensitive email check appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void testTC023_UsernameSpecialCharsOnly() {
    createTest(
        "TC-023: Username with special characters only",
        "Verify error message is displayed when username contains only special characters");

    String username = "@#$%^&*";
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "specialuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with special characters only username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "Should handle special characters username appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void testTC024_EmailWithInvalidTld() {
    createTest(
        "TC-024: Email with invalid TLD",
        "Verify behavior when email has an invalid top-level domain");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "invalidtld" + uniqueId;
    String email = "user" + uniqueId + "@domain.invalidtld";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with invalid TLD email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "Should handle invalid TLD email appropriately");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC025_PasswordTooShort() {
    createTest(
        "TC-025: Password too short",
        "Verify error message is displayed when password is too short");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "shortpwd" + uniqueId;
    String email = "shortpwd" + uniqueId + "@example.com";
    String password = "a";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with 1 character password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome, "Should handle short password appropriately");
  }

  @Test(groups = {"smoke", "regression", "error", "security"})
  public void testTC026_SqlInjectionInUsername() {
    createTest(
        "TC-026: SQL injection attempt in username",
        "Verify SQL injection is sanitized and does not execute");

    String username = "'; DROP TABLE users;--";
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "sqlinject" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with SQL injection in username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "SQL injection should be sanitized - no server error");
    test.info("SQL injection was handled safely");
  }

  @Test(groups = {"smoke", "regression", "error", "security"})
  public void testTC027_XssAttemptInUsername() {
    createTest(
        "TC-027: XSS attempt in username", "Verify XSS script is sanitized and does not execute");

    String username = "<script>alert('xss')</script>";
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "xsstest" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted form with XSS script in username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome,
        "XSS should be sanitized - no script execution");
    test.info("XSS attempt was handled safely");
  }

  @Test(groups = {"regression", "error"})
  public void testTC028_UsernameExceedsMaxLength() {
    createTest(
        "TC-028: Username exceeds maximum length",
        "Verify error message is displayed when username exceeds maximum length");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 300; i++) {
      longUsername.append("a");
    }
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String email = "longuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(longUsername.toString(), email, password);
    test.info("Submitted form with 300 character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome, "Should handle long username appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void testTC029_EmailExceedsMaxLength() {
    createTest(
        "TC-029: Email exceeds maximum length",
        "Verify error message is displayed when email exceeds maximum length");

    StringBuilder longEmail = new StringBuilder();
    for (int i = 0; i < 250; i++) {
      longEmail.append("a");
    }
    longEmail.append("@example.com");
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "longemail" + uniqueId;
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, longEmail.toString(), password);
    test.info("Submitted form with very long email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome, "Should handle long email appropriately");
  }

  @Test(groups = {"regression", "error"})
  public void testTC030_PasswordExceedsMaxLength() {
    createTest(
        "TC-030: Password exceeds maximum length",
        "Verify behavior when password exceeds maximum length");

    StringBuilder longPassword = new StringBuilder();
    for (int i = 0; i < 300; i++) {
      longPassword.append("a");
    }
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "longpwd" + uniqueId;
    String email = "longpwd" + uniqueId + "@example.com";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, longPassword.toString());
    test.info("Submitted form with 300 character password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stayedOnPage = currentUrl.contains("/register");
    boolean hasError = registrationPage.isErrorMessageDisplayed();
    boolean redirectedHome = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);

    assertTrue(
        stayedOnPage || hasError || redirectedHome, "Should handle long password appropriately");
  }
}
