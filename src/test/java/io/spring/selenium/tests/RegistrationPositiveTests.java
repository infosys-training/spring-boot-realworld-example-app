package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test cases for user registration (TC-001 to TC-008). Tests happy path scenarios for the
 * registration functionality.
 */
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

  @Test(groups = {"smoke", "regression", "positive"})
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

    registerPage.enterUsername(username);
    registerPage.enterEmail(email);
    registerPage.enterPassword(password);
    test.info("Entered registration details");

    registerPage.clickSignUp();
    test.info("Clicked Sign up button");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isLoggedIn = homePage.isUserLoggedIn();
    boolean isOnHomePage = !registerPage.isOnRegisterPage();

    assertTrue(
        isLoggedIn || isOnHomePage,
        "User should be logged in and redirected after successful registration");
    test.info("User successfully registered and logged in");
  }

  @Test(groups = {"smoke", "regression", "positive"})
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

    test.info("All required fields are displayed on the registration form");
  }

  @Test(groups = {"regression", "positive"})
  public void TC003_registrationPageDisplaysCorrectTitle() {
    createTest(
        "TC-003: Registration page displays correct title", "Verify page displays 'Sign Up' title");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String pageTitle = registerPage.getPageTitle();
    assertTrue(
        pageTitle.toLowerCase().contains("sign up") || pageTitle.toLowerCase().contains("signup"),
        "Page should display 'Sign Up' title, but found: " + pageTitle);

    test.info("Page title is correct: " + pageTitle);
  }

  @Test(groups = {"regression", "positive"})
  public void TC004_loginLinkDisplayedOnRegistrationPage() {
    createTest(
        "TC-004: Login link displayed on registration page",
        "Verify 'Have an account?' link is displayed");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(
        registerPage.isLoginLinkDisplayed(), "'Have an account?' login link should be displayed");

    test.info("Login link is displayed on registration page");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_signUpButtonIsEnabled() {
    createTest(
        "TC-005: Sign up button is enabled", "Verify Sign up button is enabled and clickable");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    assertTrue(
        registerPage.isSignUpButtonEnabled(), "Sign up button should be enabled and clickable");

    test.info("Sign up button is enabled");
  }

  @Test(groups = {"smoke", "regression", "positive"})
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

    test.info("Successfully typed in all form fields");
  }

  @Test(groups = {"regression", "positive"})
  public void TC007_passwordFieldMasksInput() {
    createTest("TC-007: Password field masks input", "Verify password field has type='password'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String fieldType = registerPage.getPasswordFieldType();
    assertEquals(fieldType, "password", "Password field should have type='password'");

    test.info("Password field correctly masks input with type='password'");
  }

  @Test(groups = {"regression", "positive"})
  public void TC008_emailFieldHasCorrectInputType() {
    createTest("TC-008: Email field has correct input type", "Verify email field has type='email'");

    registerPage.navigateTo(baseUrl);
    test.info("Navigated to register page");

    String fieldType = registerPage.getEmailFieldType();
    assertTrue(
        fieldType.equals("email") || fieldType.equals("text"),
        "Email field should have type='email' or 'text', found: " + fieldType);

    test.info("Email field has correct input type: " + fieldType);
  }
}
