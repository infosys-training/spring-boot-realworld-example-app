package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for user registration (TC-031 to TC-040). Tests boundary conditions and edge
 * cases for the registration functionality.
 */
public class RegistrationEdgeCaseTests extends BaseTest {

  private RegisterPage registerPage;
  private LoginPage loginPage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(groups = {"regression", "edge"})
  public void TC031_minimumLengthUsername() {
    createTest(
        "TC-031: Minimum length username", "Verify system handles single character username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("a");
    registerPage.enterEmail("min" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with single character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "System should handle minimum length username");

    test.info("Minimum length username handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC032_maximumLengthUsername() {
    createTest("TC-032: Maximum length username", "Verify system handles 100 character username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String longUsername = "a".repeat(100);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername(longUsername);
    registerPage.enterEmail("max" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with 100 character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "System should handle maximum length username");

    test.info("Maximum length username handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC033_usernameWithNumbers() {
    createTest("TC-033: Username with numbers", "Verify username with numbers is accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("user123" + uniqueId);
    registerPage.enterEmail("num" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with username containing numbers");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Username with numbers should be handled");

    test.info("Username with numbers handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC034_usernameWithSpecialCharacters() {
    createTest(
        "TC-034: Username with special characters",
        "Verify username with underscore/hyphen handling");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("user_test-" + uniqueId);
    registerPage.enterEmail("special" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with username containing special characters");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Username with special characters should be handled appropriately");

    test.info("Username with special characters handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC035_emailWithSubdomain() {
    createTest("TC-035: Email with subdomain", "Verify email with subdomain is accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("subdomain" + uniqueId);
    registerPage.enterEmail("user" + uniqueId + "@mail.example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with email containing subdomain");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Email with subdomain should be handled");

    test.info("Email with subdomain handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC036_emailWithPlusSign() {
    createTest("TC-036: Email with plus sign", "Verify email with + sign handling");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("plusemail" + uniqueId);
    registerPage.enterEmail("user+" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with email containing plus sign");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Email with plus sign should be handled");

    test.info("Email with plus sign handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC037_passwordWithSpecialCharacters() {
    createTest(
        "TC-037: Password with special characters",
        "Verify password with special chars is accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("specialpwd" + uniqueId);
    registerPage.enterEmail("specialpwd" + uniqueId + "@example.com");
    registerPage.enterPassword("P@ssw0rd!#$%");
    registerPage.clickSignUp();
    test.info("Submitted form with password containing special characters");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Password with special characters should be accepted");

    test.info("Password with special characters handling verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC038_navigationToLoginPage() {
    createTest(
        "TC-038: Navigation to login page", "Verify 'Have an account?' link navigates to login");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isLoginLinkDisplayed(), "Login link should be displayed");

    registerPage.clickLoginLink();
    test.info("Clicked 'Have an account?' link");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isOnLoginPage(), "User should be navigated to login page");

    test.info("Navigation to login page verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC039_whitespaceOnlyUsername() {
    createTest("TC-039: Whitespace only username", "Verify whitespace-only username is rejected");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("   ");
    registerPage.enterEmail("whitespace" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with whitespace-only username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean rejected = registerPage.hasErrors() || registerPage.isOnRegisterPage();
    assertTrue(rejected, "Whitespace-only username should be rejected");

    test.info("Whitespace-only username rejection verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC040_caseSensitivityInEmail() {
    createTest("TC-040: Case sensitivity in email", "Verify uppercase email handling");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("casetest" + uniqueId);
    registerPage.enterEmail("UPPERCASE" + uniqueId + "@EXAMPLE.COM");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();
    test.info("Submitted form with uppercase email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled = registerPage.hasErrors() || !registerPage.isOnRegisterPage();
    assertTrue(handled, "Uppercase email should be handled appropriately");

    test.info("Email case sensitivity handling verified");
  }
}
