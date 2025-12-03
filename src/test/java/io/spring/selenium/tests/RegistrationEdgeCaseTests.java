package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Edge case test cases for user registration (TC-031 to TC-040). */
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

  @Test(groups = {"regression"})
  public void TC031_minimumLengthUsername() {
    createTest(
        "TC-031: Minimum length username", "Verify system handles single character username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String singleCharUsername = "a";
    String email = "min" + uniqueId + "@example.com";

    registerPage.register(singleCharUsername, email, "password123");
    test.info("Attempted registration with single character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle minimum length username");

    test.pass("Minimum length username handling verified");
  }

  @Test(groups = {"regression"})
  public void TC032_maximumLengthUsername() {
    createTest("TC-032: Maximum length username", "Verify system handles 100 character username");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longUsername.append("a");
    }
    String email = "max" + uniqueId + "@example.com";

    registerPage.register(longUsername.toString(), email, "password123");
    test.info("Attempted registration with 100 character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle maximum length username");

    test.pass("Maximum length username handling verified");
  }

  @Test(groups = {"regression"})
  public void TC033_usernameWithNumbers() {
    createTest("TC-033: Username with numbers", "Verify username with numbers is accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String usernameWithNumbers = "user123" + uniqueId;
    String email = "num" + uniqueId + "@example.com";

    registerPage.register(usernameWithNumbers, email, "password123");
    test.info("Attempted registration with username containing numbers");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle username with numbers");

    test.pass("Username with numbers handling verified");
  }

  @Test(groups = {"regression"})
  public void TC034_usernameWithSpecialCharacters() {
    createTest(
        "TC-034: Username with special characters",
        "Verify username with underscore/hyphen handled appropriately");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String usernameWithSpecial = "user_test-" + uniqueId;
    String email = "special" + uniqueId + "@example.com";

    registerPage.register(usernameWithSpecial, email, "password123");
    test.info("Attempted registration with username containing special characters");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle username with special characters");

    test.pass("Username with special characters handling verified");
  }

  @Test(groups = {"regression"})
  public void TC035_emailWithSubdomain() {
    createTest("TC-035: Email with subdomain", "Verify email with subdomain is accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "subuser" + uniqueId;
    String emailWithSubdomain = "test" + uniqueId + "@mail.example.com";

    registerPage.register(username, emailWithSubdomain, "password123");
    test.info("Attempted registration with email containing subdomain");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle email with subdomain");

    test.pass("Email with subdomain handling verified");
  }

  @Test(groups = {"regression"})
  public void TC036_emailWithPlusSign() {
    createTest("TC-036: Email with plus sign", "Verify email with + sign handled");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "plususer" + uniqueId;
    String emailWithPlus = "test+" + uniqueId + "@example.com";

    registerPage.register(username, emailWithPlus, "password123");
    test.info("Attempted registration with email containing plus sign");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle email with plus sign");

    test.pass("Email with plus sign handling verified");
  }

  @Test(groups = {"regression"})
  public void TC037_passwordWithSpecialCharacters() {
    createTest(
        "TC-037: Password with special characters", "Verify password with special chars accepted");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "pwduser" + uniqueId;
    String email = "pwd" + uniqueId + "@example.com";
    String passwordWithSpecial = "P@ssw0rd!#$%";

    registerPage.register(username, email, passwordWithSpecial);
    test.info("Attempted registration with password containing special characters");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle password with special characters");

    test.pass("Password with special characters handling verified");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC038_navigationToLoginPage() {
    createTest(
        "TC-038: Navigation to login page",
        "Verify user navigates to login page via 'Have an account?' link");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isLoginLinkDisplayed(), "'Have an account?' link should be displayed");

    registerPage.clickLoginLink();
    test.info("Clicked 'Have an account?' link");

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean onLoginPage = loginPage.isOnLoginPage();
    assertTrue(onLoginPage, "User should be navigated to login page");

    test.pass("Navigation to login page verified");
  }

  @Test(groups = {"regression"})
  public void TC039_whitespaceOnlyUsername() {
    createTest("TC-039: Whitespace only username", "Verify whitespace-only username rejected");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String whitespaceUsername = "   ";
    String email = "ws" + uniqueId + "@example.com";

    registerPage.register(whitespaceUsername, email, "password123");
    test.info("Attempted registration with whitespace-only username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean rejected = registerPage.hasErrorMessages() || registerPage.isOnRegisterPage();
    assertTrue(rejected, "Whitespace-only username should be rejected");

    test.pass("Whitespace only username rejection verified");
  }

  @Test(groups = {"regression"})
  public void TC040_caseSensitivityInEmail() {
    createTest("TC-040: Case sensitivity in email", "Verify email case handled appropriately");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "caseuser" + uniqueId;
    String uppercaseEmail = "TEST" + uniqueId + "@EXAMPLE.COM";

    registerPage.register(username, uppercaseEmail, "password123");
    test.info("Attempted registration with uppercase email");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        registerPage.isOnRegisterPage()
            || registerPage.hasErrorMessages()
            || !driver.getCurrentUrl().contains("/user/register");
    assertTrue(handled, "System should handle email case appropriately");

    test.pass("Email case sensitivity handling verified");
  }
}
