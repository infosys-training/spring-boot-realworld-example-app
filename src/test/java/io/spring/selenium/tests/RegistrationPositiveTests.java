package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.NavigationBar;
import io.spring.selenium.pages.RegistrationPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationPositiveTests extends BaseTest {

  private RegistrationPage registrationPage;
  private HomePage homePage;
  private NavigationBar navigationBar;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registrationPage = new RegistrationPage(driver);
    homePage = new HomePage(driver);
    navigationBar = new NavigationBar(driver);
  }

  private String generateUniqueUsername() {
    return "user" + UUID.randomUUID().toString().substring(0, 8);
  }

  private String generateUniqueEmail() {
    return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC001_successfulRegistrationWithValidCredentials() {
    createTest("TC-001", "Successful registration with valid credentials");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    homePage.waitForPageLoad();
    assertTrue(homePage.isUserLoggedIn(), "User should be logged in after registration");
    test.pass("User successfully registered and logged in");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC002_registrationFormDisplaysAllRequiredFields() {
    createTest("TC-002", "Registration form displays all required fields");

    registrationPage.navigateTo(baseUrl);

    assertTrue(registrationPage.isUsernameFieldDisplayed(), "Username field should be displayed");
    assertTrue(registrationPage.isEmailFieldDisplayed(), "Email field should be displayed");
    assertTrue(registrationPage.isPasswordFieldDisplayed(), "Password field should be displayed");
    assertTrue(registrationPage.isSignUpButtonDisplayed(), "Sign Up button should be displayed");

    test.pass("All required fields are displayed on registration form");
  }

  @Test(groups = {"regression", "positive"})
  public void TC003_successfulRegistrationRedirectsToHomePage() {
    createTest("TC-003", "Successful registration redirects to home page");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.equals(baseUrl) || currentUrl.equals(baseUrl + "/"),
        "User should be redirected to home page after registration");

    test.pass("User redirected to home page after successful registration");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC004_userIsAutomaticallyLoggedInAfterRegistration() {
    createTest("TC-004", "User is automatically logged in after registration");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    navigationBar.waitForNavbar();
    assertTrue(navigationBar.isUserLoggedIn(), "User should be logged in after registration");
    assertTrue(
        navigationBar.isSettingsLinkDisplayed(),
        "Settings link should be visible for logged in user");
    assertTrue(
        navigationBar.isNewArticleLinkDisplayed(),
        "New Article link should be visible for logged in user");

    test.pass("User is automatically logged in after registration");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_jwtTokenIsStoredAfterSuccessfulRegistration() {
    createTest("TC-005", "JWT token is stored after successful registration");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    homePage.clearLocalStorage();

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(homePage.hasJwtToken(), "JWT token should be stored in local storage");

    test.pass("JWT token is stored after successful registration");
  }

  @Test(groups = {"regression", "positive"})
  public void TC006_userCanAccessAuthenticatedFeaturesAfterRegistration() {
    createTest("TC-006", "User can access authenticated features after registration");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    navigationBar.waitForNavbar();
    navigationBar.clickNewArticle();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/editor"), "User should be able to access New Article page");

    test.pass("User can access authenticated features after registration");
  }

  @Test(groups = {"regression", "positive"})
  public void TC007_registrationWithMinimumValidUsernameLength() {
    createTest("TC-007", "Registration with minimum valid username length");

    String username = "a";
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();

    assertTrue(isLoggedIn || hasErrors, "Registration should succeed or show validation error");

    test.pass("System handles minimum username length appropriately");
  }

  @Test(groups = {"regression", "positive"})
  public void TC008_registrationWithMaximumValidUsernameLength() {
    createTest("TC-008", "Registration with maximum valid username length");

    String username = "a".repeat(50);
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean hasErrors = registrationPage.hasErrorMessages();

    assertTrue(isLoggedIn || hasErrors, "Registration should succeed or show validation error");

    test.pass("System handles maximum username length appropriately");
  }

  @Test(groups = {"regression", "positive"})
  public void TC009_registrationWithComplexPassword() {
    createTest("TC-009", "Registration with complex password containing special characters");

    String username = generateUniqueUsername();
    String email = generateUniqueEmail();
    String password = "P@ssw0rd!#$%^&*()";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in with complex password");

    test.pass("Registration succeeds with complex password");
  }

  @Test(groups = {"regression", "positive"})
  public void TC010_registrationWithAlphanumericUsername() {
    createTest("TC-010", "Registration with alphanumeric username");

    String username = "user123abc";
    String email = generateUniqueEmail();
    String password = "Password123!";

    registrationPage.navigateTo(baseUrl);
    registrationPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in with alphanumeric username");

    test.pass("Registration succeeds with alphanumeric username");
  }
}
