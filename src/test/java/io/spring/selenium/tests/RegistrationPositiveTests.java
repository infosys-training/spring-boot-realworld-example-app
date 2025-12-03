package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.RegisterPage;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationPositiveTests extends BaseTest {

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

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC001_testSuccessfulRegistrationWithValidData() {
    createTest(
        "TC-001: Successful registration with valid data",
        "Verify user can register with valid username, email, and password");

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String username = "testuser" + uniqueId;
    String email = "test" + uniqueId + "@example.com";
    String password = "Password123!";

    registerPage.navigateTo(baseUrl);
    registerPage.register(username, email, password);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean isRedirected = !currentUrl.contains("/register");

    test.info("Current URL after registration: " + currentUrl);
    assertTrue(isRedirected, "User should be redirected after successful registration");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC002_testRegistrationFormDisplaysAllFields() {
    createTest(
        "TC-002: Registration form displays all required fields",
        "Verify registration form shows username, email, and password fields");

    registerPage.navigateTo(baseUrl);

    assertTrue(registerPage.isUsernameFieldDisplayed(), "Username field should be displayed");
    assertTrue(registerPage.isEmailFieldDisplayed(), "Email field should be displayed");
    assertTrue(registerPage.isPasswordFieldDisplayed(), "Password field should be displayed");
    assertTrue(registerPage.isSignUpButtonDisplayed(), "Sign up button should be displayed");

    test.info("All registration form fields are displayed correctly");
  }

  @Test(groups = {"regression", "positive"})
  public void TC003_testRegistrationPageTitle() {
    createTest(
        "TC-003: Registration page displays correct title",
        "Verify the registration page shows 'Sign Up' title");

    registerPage.navigateTo(baseUrl);
    String pageTitle = registerPage.getPageTitle();

    assertEquals(pageTitle, "Sign Up", "Page title should be 'Sign Up'");
    test.info("Page title is correct: " + pageTitle);
  }

  @Test(groups = {"regression", "positive"})
  public void TC004_testLoginLinkDisplayedOnRegisterPage() {
    createTest(
        "TC-004: Login link displayed on registration page",
        "Verify 'Have an account?' link is displayed");

    registerPage.navigateTo(baseUrl);

    assertTrue(registerPage.isLoginLinkDisplayed(), "Login link should be displayed");
    test.info("Login link is displayed on registration page");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_testSignUpButtonIsEnabled() {
    createTest(
        "TC-005: Sign up button is enabled", "Verify the sign up button is enabled and clickable");

    registerPage.navigateTo(baseUrl);

    assertTrue(registerPage.isSignUpButtonEnabled(), "Sign up button should be enabled");
    test.info("Sign up button is enabled");
  }

  @Test(groups = {"regression", "positive"})
  public void TC006_testUserCanTypeInAllFields() {
    createTest(
        "TC-006: User can type in all form fields",
        "Verify user can enter text in username, email, and password fields");

    registerPage.navigateTo(baseUrl);

    String testUsername = "testuser";
    String testEmail = "test@example.com";
    String testPassword = "password123";

    registerPage.enterUsername(testUsername);
    registerPage.enterEmail(testEmail);
    registerPage.enterPassword(testPassword);

    assertEquals(registerPage.getUsernameValue(), testUsername, "Username should be entered");
    assertEquals(registerPage.getEmailValue(), testEmail, "Email should be entered");
    assertEquals(registerPage.getPasswordValue(), testPassword, "Password should be entered");

    test.info("User can type in all form fields successfully");
  }

  @Test(groups = {"regression", "positive"})
  public void TC007_testPasswordFieldMasksInput() {
    createTest(
        "TC-007: Password field masks input",
        "Verify password field has type='password' to mask input");

    registerPage.navigateTo(baseUrl);

    String inputType = registerPage.getPasswordInputType();
    assertEquals(inputType, "password", "Password field should have type='password'");

    test.info("Password field correctly masks input");
  }

  @Test(groups = {"regression", "positive"})
  public void TC008_testEmailFieldHasCorrectType() {
    createTest(
        "TC-008: Email field has correct input type",
        "Verify email field has type='email' for validation");

    registerPage.navigateTo(baseUrl);

    String inputType = registerPage.getEmailInputType();
    assertEquals(inputType, "email", "Email field should have type='email'");

    test.info("Email field has correct input type");
  }
}
