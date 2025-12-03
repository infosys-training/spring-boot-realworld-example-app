package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Positive test cases for user registration (TC-001 to TC-008). */
public class RegistrationPositiveTests extends BaseTest {

  private RegisterPage registerPage;
  private HomePage homePage;
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    registerPage = new RegisterPage(driver);
    homePage = new HomePage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void TC001_successfulRegistrationWithValidData() {
    createTest(
        "TC-001: Successful registration with valid data",
        "Verify user can register with valid username, email, and password");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "testuser" + uniqueId;
    String email = "test" + uniqueId + "@example.com";
    String password = "password123";

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    registerPage.register(username, email, password);
    test.info("Entered registration details and clicked Sign up");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isOnHomePage = homePage.isOnHomePage() || !registerPage.isOnRegisterPage();
    assertTrue(isOnHomePage, "User should be redirected after successful registration");
    test.pass("User successfully registered and redirected");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC002_registrationFormDisplaysAllRequiredFields() {
    createTest(
        "TC-002: Registration form displays all required fields",
        "Verify username, email, password fields and Sign up button are displayed");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isUsernameFieldDisplayed(), "Username field should be displayed");
    assertTrue(registerPage.isEmailFieldDisplayed(), "Email field should be displayed");
    assertTrue(registerPage.isPasswordFieldDisplayed(), "Password field should be displayed");
    assertTrue(registerPage.isSignUpButtonDisplayed(), "Sign up button should be displayed");

    test.pass("All required fields are displayed on registration form");
  }

  @Test(groups = {"regression"})
  public void TC003_registrationPageDisplaysCorrectTitle() {
    createTest(
        "TC-003: Registration page displays correct title", "Verify page displays 'Sign Up' title");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String pageTitle = registerPage.getPageTitle();
    assertEquals(pageTitle, "Sign Up", "Page title should be 'Sign Up'");

    test.pass("Registration page displays correct title: " + pageTitle);
  }

  @Test(groups = {"regression"})
  public void TC004_loginLinkDisplayedOnRegistrationPage() {
    createTest(
        "TC-004: Login link displayed on registration page",
        "Verify 'Have an account?' link is displayed");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isLoginLinkDisplayed(), "'Have an account?' link should be displayed");

    test.pass("Login link is displayed on registration page");
  }

  @Test(groups = {"regression"})
  public void TC005_signUpButtonIsEnabled() {
    createTest(
        "TC-005: Sign up button is enabled", "Verify Sign up button is enabled and clickable");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(registerPage.isSignUpButtonEnabled(), "Sign up button should be enabled");

    test.pass("Sign up button is enabled and clickable");
  }

  @Test(groups = {"smoke", "regression"})
  public void TC006_userCanTypeInAllFormFields() {
    createTest(
        "TC-006: User can type in all form fields",
        "Verify text can be entered in username, email, password fields");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String testUsername = "testuser";
    String testEmail = "test@example.com";
    String testPassword = "password123";

    registerPage.enterUsername(testUsername);
    registerPage.enterEmail(testEmail);
    registerPage.enterPassword(testPassword);

    assertEquals(
        registerPage.getUsernameValue(),
        testUsername,
        "Username field should contain entered text");
    assertEquals(
        registerPage.getEmailValue(), testEmail, "Email field should contain entered text");
    assertEquals(
        registerPage.getPasswordValue(),
        testPassword,
        "Password field should contain entered text");

    test.pass("User can type in all form fields successfully");
  }

  @Test(groups = {"regression"})
  public void TC007_passwordFieldMasksInput() {
    createTest("TC-007: Password field masks input", "Verify password field has type='password'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String fieldType = registerPage.getPasswordFieldType();
    assertEquals(fieldType, "password", "Password field should have type='password'");

    test.pass("Password field masks input with type='password'");
  }

  @Test(groups = {"regression"})
  public void TC008_emailFieldHasCorrectInputType() {
    createTest("TC-008: Email field has correct input type", "Verify email field has type='email'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String fieldType = registerPage.getEmailFieldType();
    assertEquals(fieldType, "email", "Email field should have type='email'");

    test.pass("Email field has correct input type='email'");
  }
}
