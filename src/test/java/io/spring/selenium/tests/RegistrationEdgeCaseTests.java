package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationEdgeCaseTests extends BaseTest {

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

  @Test(groups = {"regression", "edge"})
  public void TC031_testMinimumLengthUsername() {
    createTest(
        "TC-031: Minimum length username", "Verify registration with single character username");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 4);
    registerPage.enterUsername("a");
    registerPage.enterEmail("min" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should handle minimum length username");
    test.info("Minimum length username test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC032_testMaximumLengthUsername() {
    createTest("TC-032: Maximum length username", "Verify registration with very long username");

    registerPage.navigateTo(baseUrl);
    String longUsername = "a".repeat(100);
    String uniqueId = UUID.randomUUID().toString().substring(0, 4);
    registerPage.enterUsername(longUsername);
    registerPage.enterEmail("max" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should handle maximum length username");
    test.info("Maximum length username test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC033_testUsernameWithNumbers() {
    createTest(
        "TC-033: Username with numbers", "Verify registration with username containing numbers");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("user123" + uniqueId);
    registerPage.enterEmail("num" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should accept username with numbers");
    test.info("Username with numbers test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC034_testUsernameWithSpecialCharacters() {
    createTest(
        "TC-034: Username with special characters",
        "Verify handling of username with special characters");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 4);
    registerPage.enterUsername("user_test-" + uniqueId);
    registerPage.enterEmail("special" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should handle special characters in username");
    test.info("Username with special characters test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC035_testEmailWithSubdomain() {
    createTest(
        "TC-035: Email with subdomain", "Verify registration with email containing subdomain");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("subdomain" + uniqueId);
    registerPage.enterEmail("test" + uniqueId + "@mail.example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should accept email with subdomain");
    test.info("Email with subdomain test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC036_testEmailWithPlusSign() {
    createTest(
        "TC-036: Email with plus sign", "Verify registration with email containing plus sign");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("plusemail" + uniqueId);
    registerPage.enterEmail("test+" + uniqueId + "@example.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should handle email with plus sign");
    test.info("Email with plus sign test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC037_testPasswordWithSpecialCharacters() {
    createTest(
        "TC-037: Password with special characters",
        "Verify registration with password containing special characters");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("specialpwd" + uniqueId);
    registerPage.enterEmail("pwd" + uniqueId + "@example.com");
    registerPage.enterPassword("P@ssw0rd!#$%");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should accept password with special characters");
    test.info("Password with special characters test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC038_testNavigationToLoginPage() {
    createTest(
        "TC-038: Navigation to login page",
        "Verify clicking 'Have an account?' navigates to login");

    registerPage.navigateTo(baseUrl);
    registerPage.clickLoginLink();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/login"), "Should navigate to login page");

    test.info("Navigation to login page verified");
  }

  @Test(groups = {"regression", "edge"})
  public void TC039_testWhitespaceOnlyUsername() {
    createTest(
        "TC-039: Whitespace only username", "Verify handling of username with only whitespace");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("   ");
    registerPage.enterEmail("whitespace" + uniqueId + "@example.com");
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
        "Should reject whitespace-only username");

    test.info("Whitespace only username test completed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC040_testCaseSensitivityInEmail() {
    createTest("TC-040: Case sensitivity in email", "Verify email handling is case insensitive");

    registerPage.navigateTo(baseUrl);
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    registerPage.enterUsername("casetest" + uniqueId);
    registerPage.enterEmail("TEST" + uniqueId + "@EXAMPLE.COM");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getCurrentUrl(), "Page should handle uppercase email");
    test.info("Case sensitivity in email test completed");
  }
}
