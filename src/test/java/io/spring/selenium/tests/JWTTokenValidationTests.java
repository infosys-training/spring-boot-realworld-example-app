package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.RegisterPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class JWTTokenValidationTests extends BaseTest {

  private LoginPage loginPage;
  private RegisterPage registerPage;
  private String baseUrl;

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";
  private static final String EXISTING_USERNAME = "johndoe";
  private static final String EXISTING_EMAIL = "john@example.com";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    registerPage = new RegisterPage(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC011_LoginFailsWithEmptyEmailField() {
    createTest(
        "TC-011: Login Fails with Empty Email",
        "Verify login fails with validation error when email field is empty");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isPageLoaded() || loginPage.isErrorDisplayed(),
        "Should remain on login page or show error");

    test.pass("Login correctly fails with empty email field");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC012_LoginFailsWithEmptyPasswordField() {
    createTest(
        "TC-012: Login Fails with Empty Password",
        "Verify login fails with validation error when password field is empty");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isPageLoaded() || loginPage.isErrorDisplayed(),
        "Should remain on login page or show error");

    test.pass("Login correctly fails with empty password field");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC013_LoginFailsWithInvalidEmailFormat() {
    createTest(
        "TC-013: Login Fails with Invalid Email Format",
        "Verify login fails when email format is invalid");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.enterEmail("invalid-email-format");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isPageLoaded() || loginPage.isErrorDisplayed(),
        "Should remain on login page or show error for invalid email format");

    test.pass("Login correctly fails with invalid email format");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC014_RegistrationFailsWithEmptyUsername() {
    createTest(
        "TC-014: Registration Fails with Empty Username",
        "Verify registration fails when username field is empty");

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.enterEmail("newuser@test.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isPageLoaded() || registerPage.isErrorDisplayed(),
        "Should remain on register page or show error");

    test.pass("Registration correctly fails with empty username");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC015_RegistrationFailsWithEmptyEmail() {
    createTest(
        "TC-015: Registration Fails with Empty Email",
        "Verify registration fails when email field is empty");

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.enterUsername("newuser123");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isPageLoaded() || registerPage.isErrorDisplayed(),
        "Should remain on register page or show error");

    test.pass("Registration correctly fails with empty email");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC016_RegistrationFailsWithEmptyPassword() {
    createTest(
        "TC-016: Registration Fails with Empty Password",
        "Verify registration fails when password field is empty");

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.enterUsername("newuser456");
    registerPage.enterEmail("newuser456@test.com");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isPageLoaded() || registerPage.isErrorDisplayed(),
        "Should remain on register page or show error");

    test.pass("Registration correctly fails with empty password");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC017_RegistrationFailsWithDuplicateEmail() {
    createTest(
        "TC-017: Registration Fails with Duplicate Email",
        "Verify registration fails when email already exists");

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.enterUsername("uniqueuser123");
    registerPage.enterEmail(EXISTING_EMAIL);
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isErrorDisplayed(), "Error message should be displayed for duplicate email");

    test.pass("Registration correctly fails with duplicate email");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC018_RegistrationFailsWithDuplicateUsername() {
    createTest(
        "TC-018: Registration Fails with Duplicate Username",
        "Verify registration fails when username already exists");

    registerPage.navigateTo(baseUrl);
    assertTrue(registerPage.isPageLoaded(), "Register page should be loaded");

    registerPage.enterUsername(EXISTING_USERNAME);
    registerPage.enterEmail("uniqueemail@test.com");
    registerPage.enterPassword("password123");
    registerPage.clickSignUp();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isErrorDisplayed(),
        "Error message should be displayed for duplicate username");

    test.pass("Registration correctly fails with duplicate username");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC019_LoginFailsWithNonExistentEmail() {
    createTest(
        "TC-019: Login Fails with Non-Existent Email",
        "Verify login fails when email does not exist in system");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.enterEmail("nonexistent@example.com");
    loginPage.enterPassword("anypassword");
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorDisplayed(), "Error message should be displayed for non-existent email");

    test.pass("Login correctly fails with non-existent email");
  }

  @Test(groups = {"validation", "jwt"})
  public void testTC020_LoginFailsWithIncorrectPassword() {
    createTest(
        "TC-020: Login Fails with Incorrect Password",
        "Verify login fails when password is incorrect");

    loginPage.navigateTo(baseUrl);
    assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.enterPassword("wrongpassword");
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorDisplayed(), "Error message should be displayed for incorrect password");

    test.pass("Login correctly fails with incorrect password");
  }
}
