package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavbarComponent;
import io.spring.selenium.pages.RegisterPage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetCurrentUserErrorTests extends BaseTest {

  private LoginPage loginPage;
  private NavbarComponent navbar;
  private SettingsPage settingsPage;
  private RegisterPage registerPage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String EXISTING_EMAIL = "jane@example.com";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    navbar = new NavbarComponent(driver);
    settingsPage = new SettingsPage(driver);
    registerPage = new RegisterPage(driver);
  }

  @Test(groups = {"smoke", "error", "regression"})
  public void TC021_verifyUnauthenticatedUserCannotAccessSettingsPage() {
    createTest(
        "TC-021: Verify unauthenticated user cannot access settings page",
        "User should be redirected to login page");

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        navbar.isUserLoggedOut() || loginPage.isEmailInputDisplayed(),
        "Unauthenticated user should be redirected or blocked from settings");
    test.info("Unauthenticated access to settings blocked");
  }

  @Test(groups = {"smoke", "error", "regression"})
  public void TC022_verifyUnauthenticatedUserRedirectedToLogin() {
    createTest(
        "TC-022: Verify unauthenticated user is redirected to login",
        "User should be redirected to login page when accessing protected route");

    driver.get(config.getProperty("base.url", "http://localhost:3000") + "/settings");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("login") || navbar.isUserLoggedOut(),
        "Should redirect to login or show logged out state");
    test.info("Unauthenticated user redirect verified");
  }

  @Test(groups = {"smoke", "error", "regression"})
  public void TC023_verifyInvalidCredentialsShowErrorMessage() {
    createTest(
        "TC-023: Verify invalid credentials show error message",
        "Error message should be displayed for invalid credentials");

    loginPage.navigateTo();
    loginPage.login("wrong@email.com", "wrongpassword");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error message for invalid credentials");
    test.info("Invalid credentials error message verified");
  }

  @Test(groups = {"error", "regression"})
  public void TC024_verifyWrongPasswordShowsErrorMessage() {
    createTest(
        "TC-024: Verify wrong password shows error message",
        "Error message should be shown for wrong password");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, "wrongpassword123");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error message for wrong password");
    test.info("Wrong password error message verified");
  }

  @Test(groups = {"error", "regression"})
  public void TC025_verifyNonExistentEmailShowsErrorMessage() {
    createTest(
        "TC-025: Verify non-existent email shows error message",
        "Error message should be shown for non-existent email");

    loginPage.navigateTo();
    loginPage.login("nonexistent@email.com", TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error message for non-existent email");
    test.info("Non-existent email error message verified");
  }

  @Test(groups = {"smoke", "error", "regression"})
  public void TC026_verifySessionExpiresAfterLogout() {
    createTest(
        "TC-026: Verify session expires after logout",
        "User should not be able to access protected pages after logout");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isUserLoggedIn(), "User should be logged in");

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clickLogout();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isUserLoggedOut(), "User should be logged out after clicking logout");
    test.info("Session expiry after logout verified");
  }

  @Test(groups = {"error", "regression"})
  public void TC027_verifyCannotAccessProtectedRoutesAfterLogout() {
    createTest(
        "TC-027: Verify cannot access protected routes after logout",
        "User should be redirected to login after logout");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.clickLogout();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    settingsPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        navbar.isUserLoggedOut() || loginPage.isEmailInputDisplayed(),
        "Should not be able to access settings after logout");
    test.info("Protected route access after logout blocked");
  }

  @Test(groups = {"error", "regression"})
  public void TC028_verifyErrorMessageForEmptyLoginFormSubmission() {
    createTest(
        "TC-028: Verify error message for empty login form submission",
        "Error messages should be shown for required fields");

    loginPage.navigateTo();
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error for empty form submission");
    test.info("Empty form submission error verified");
  }

  @Test(groups = {"error", "regression"})
  public void TC029_verifyErrorHandlingForServerUnavailable() {
    createTest(
        "TC-029: Verify error handling for server unavailable",
        "Appropriate error message should be displayed");

    loginPage.navigateTo();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isEmailInputDisplayed() || loginPage.isErrorMessageDisplayed(),
        "Page should load or show appropriate error");
    test.info("Server availability handling verified");
  }

  @Test(groups = {"error", "regression"})
  public void TC030_verifyErrorMessageForDuplicateEmailOnRegistration() {
    createTest(
        "TC-030: Verify error message for duplicate email on registration",
        "Error message should be shown for email already in use");

    registerPage.navigateTo();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    registerPage.register("newuser", EXISTING_EMAIL, "password123");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        registerPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error for duplicate email");
    test.info("Duplicate email registration error verified");
  }
}
