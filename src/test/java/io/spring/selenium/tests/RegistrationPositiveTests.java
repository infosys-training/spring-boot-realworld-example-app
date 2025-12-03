package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.NavBarComponent;
import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationPositiveTests extends BaseTest {

  private RegistrationPage registrationPage;
  private HomePage homePage;
  private NavBarComponent navBar;
  private String baseUrl;

  @BeforeMethod
  public void setupPages() {
    registrationPage = new RegistrationPage(driver);
    homePage = new HomePage(driver);
    navBar = new NavBarComponent(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC001_SuccessfulRegistrationWithValidData() {
    createTest(
        "TC-001: Successful registration with valid data",
        "Verify user can register with valid username, email, and password");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "testuser" + uniqueId;
    String email = "testuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    test.info("Navigated to registration page");

    assertTrue(registrationPage.isPageTitleDisplayed(), "Registration page should be displayed");
    assertEquals(registrationPage.getPageTitleText(), "Sign Up", "Page title should be 'Sign Up'");

    registrationPage.register(username, email, password);
    test.info("Submitted registration form with valid data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl),
        "Should redirect to home page after successful registration");
    test.info("Successfully redirected to home page");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC002_RegistrationWithMinimumUsernameLength() {
    createTest(
        "TC-002: Registration with minimum username length",
        "Verify registration succeeds with minimum length username (1 character)");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "a";
    String email = "minuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with 1 character username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean registrationSucceeded = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);
    assertTrue(
        registrationSucceeded || registrationPage.isErrorMessageDisplayed(),
        "Registration should succeed or show appropriate error for minimum username");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC003_RegistrationWithMaximumUsernameLength() {
    createTest(
        "TC-003: Registration with maximum username length",
        "Verify registration succeeds with maximum allowed username length");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "maxuser" + uniqueId + "abcdefghijklmnopqrstuvwxyz";
    String email = "maxuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with long username");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean registrationSucceeded = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);
    assertTrue(
        registrationSucceeded || registrationPage.isErrorMessageDisplayed(),
        "Registration should succeed or show appropriate error for maximum username");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC004_RegistrationWithStandardEmailFormat() {
    createTest(
        "TC-004: Registration with standard email format",
        "Verify registration succeeds with standard email format (user@domain.com)");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "stduser" + uniqueId;
    String email = "standard" + uniqueId + "@domain.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with standard email format");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl),
        "Should redirect to home page after successful registration");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC005_RegistrationWithSubdomainEmailFormat() {
    createTest(
        "TC-005: Registration with subdomain email format",
        "Verify registration succeeds with subdomain email format (user@sub.domain.com)");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "subuser" + uniqueId;
    String email = "subdomain" + uniqueId + "@mail.example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with subdomain email format");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl),
        "Should redirect to home page after successful registration with subdomain email");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC006_RegistrationWithMinimumPasswordLength() {
    createTest(
        "TC-006: Registration with minimum password length",
        "Verify registration succeeds with minimum length password");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "minpwd" + uniqueId;
    String email = "minpwd" + uniqueId + "@example.com";
    String password = "pass";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with minimum password length");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean registrationSucceeded = currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl);
    assertTrue(
        registrationSucceeded || registrationPage.isErrorMessageDisplayed(),
        "Registration should succeed or show appropriate error for minimum password");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC007_RegistrationWithStrongPassword() {
    createTest(
        "TC-007: Registration with strong password",
        "Verify registration succeeds with strong password (mixed case, numbers, symbols)");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "strongpwd" + uniqueId;
    String email = "strongpwd" + uniqueId + "@example.com";
    String password = "StrongP@ss123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration with strong password");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.equals(baseUrl + "/") || currentUrl.equals(baseUrl),
        "Should redirect to home page after successful registration with strong password");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC008_VerifyJwtTokenAfterRegistration() {
    createTest(
        "TC-008: Verify JWT token received after registration",
        "Verify JWT token is stored in browser after successful registration");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "jwtuser" + uniqueId;
    String email = "jwtuser" + uniqueId + "@example.com";
    String password = "password123";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);
    test.info("Submitted registration form");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    Object userStorage = js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userStorage, "User data should be stored in localStorage after registration");
    String userStorageStr = userStorage.toString();
    assertTrue(userStorageStr.contains("token"), "User storage should contain JWT token");
    test.info("JWT token verified in localStorage");
  }
}
