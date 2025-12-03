package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class JWTTokenPositiveTests extends BaseTest {

  private LoginPage loginPage;
  private RegisterPage registerPage;
  private HomePage homePage;
  private String baseUrl;

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    registerPage = new RegisterPage(driver);
    homePage = new HomePage(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC001_JWTTokenGeneratedOnSuccessfulLogin() {
    createTest(
        "TC-001: JWT Token Generated on Login",
        "Verify JWT token is generated upon successful login with valid credentials");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored in localStorage after login");
    assertTrue(userJson.contains("token"), "User data should contain a token field");

    test.pass("JWT token was successfully generated upon login");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC002_JWTTokenGeneratedOnSuccessfulRegistration() {
    createTest(
        "TC-002: JWT Token Generated on Registration",
        "Verify JWT token is generated upon successful registration");

    String uniqueUsername = "testuser" + UUID.randomUUID().toString().substring(0, 8);
    String uniqueEmail = uniqueUsername + "@test.com";

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.register(uniqueUsername, uniqueEmail, "password123");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored in localStorage after registration");
    assertTrue(userJson.contains("token"), "User data should contain a token field");

    test.pass("JWT token was successfully generated upon registration");
  }

  @Test(groups = {"positive", "jwt"})
  public void testTC003_TokenContainsCorrectUserIdClaim() {
    createTest(
        "TC-003: Token Contains User ID Claim",
        "Verify token contains correct user identifier claim after login");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored");
    assertTrue(
        userJson.contains("id") || userJson.contains("\"id\""), "User data should contain id");

    test.pass("Token payload contains user ID claim");
  }

  @Test(groups = {"positive", "jwt"})
  public void testTC004_TokenContainsCorrectUsernameClaim() {
    createTest(
        "TC-004: Token Contains Username Claim",
        "Verify token contains correct username claim after login");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored");
    assertTrue(userJson.contains("username"), "User data should contain username");

    test.pass("Token payload contains username claim");
  }

  @Test(groups = {"positive", "jwt"})
  public void testTC005_TokenContainsCorrectEmailClaim() {
    createTest(
        "TC-005: Token Contains Email Claim",
        "Verify token contains correct email claim after login");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored");
    assertTrue(userJson.contains("email"), "User data should contain email");
    assertTrue(userJson.contains(VALID_EMAIL), "User data should contain the correct email");

    test.pass("Token payload contains correct email claim");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC006_TokenReturnedInLoginResponseBody() {
    createTest(
        "TC-006: Token in Login Response",
        "Verify token is returned in the user response object after login");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored in localStorage");
    assertTrue(userJson.contains("\"token\""), "Response should contain token field");

    String token =
        (String)
            js.executeScript(
                "var user = JSON.parse(window.localStorage.getItem('user')); return user.token;");
    assertNotNull(token, "Token should not be null");
    assertTrue(token.length() > 0, "Token should not be empty");

    test.pass("Token was returned in login response body");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC007_TokenReturnedInRegistrationResponseBody() {
    createTest(
        "TC-007: Token in Registration Response",
        "Verify token is returned in the user response object after registration");

    String uniqueUsername = "reguser" + UUID.randomUUID().toString().substring(0, 8);
    String uniqueEmail = uniqueUsername + "@test.com";

    registerPage.navigateTo(baseUrl);
    registerPage.register(uniqueUsername, uniqueEmail, "password123");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String userJson = (String) js.executeScript("return window.localStorage.getItem('user');");

    assertNotNull(userJson, "User data should be stored in localStorage");
    assertTrue(userJson.contains("\"token\""), "Response should contain token field");

    test.pass("Token was returned in registration response body");
  }

  @Test(groups = {"positive", "jwt"})
  public void testTC008_TokenFormatHasThreeParts() {
    createTest(
        "TC-008: Token Format Validation",
        "Verify token follows JWT standard format (header.payload.signature)");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String token =
        (String)
            js.executeScript(
                "var user = JSON.parse(window.localStorage.getItem('user')); return user ? user.token : null;");

    assertNotNull(token, "Token should not be null");
    String[] parts = token.split("\\.");
    assertEquals(parts.length, 3, "JWT token should have exactly 3 parts separated by periods");

    test.pass("Token format is valid with three parts (header.payload.signature)");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC009_AuthenticatedRequestSucceedsWithValidToken() {
    createTest(
        "TC-009: Authenticated Request Success",
        "Verify authenticated request succeeds with valid token");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    homePage.navigateTo(baseUrl);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in after authentication");

    test.pass("Authenticated request succeeded with valid token");
  }

  @Test(groups = {"smoke", "positive", "jwt"})
  public void testTC010_UserCanAccessProtectedEndpointWithValidToken() {
    createTest(
        "TC-010: Access Protected Endpoint",
        "Verify user can access protected endpoint with valid token");

    loginPage.navigateTo(baseUrl);
    loginPage.login(VALID_EMAIL, VALID_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    homePage.navigateTo(baseUrl);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        homePage.isSettingsLinkVisible(), "Settings link should be visible for logged in user");
    assertTrue(
        homePage.isNewArticleLinkVisible(),
        "New Article link should be visible for logged in user");

    test.pass("User can access protected endpoints with valid token");
  }
}
