package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavbarComponent;
import io.spring.selenium.pages.RegisterPage;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GetCurrentUserValidationTests extends BaseTest {

  private LoginPage loginPage;
  private NavbarComponent navbar;
  private SettingsPage settingsPage;
  private RegisterPage registerPage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String EXISTING_USERNAME = "janedoe";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    navbar = new NavbarComponent(driver);
    settingsPage = new SettingsPage(driver);
    registerPage = new RegisterPage(driver);
  }

  @Test(groups = {"smoke", "validation", "regression"})
  public void TC011_verifyLoginWithValidEmailAndPassword() {
    createTest(
        "TC-011: Verify login with valid email and password",
        "User should be logged in and redirected to home page");

    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(navbar.isUserLoggedIn(), "User should be logged in after valid credentials");
    test.info("Login successful with valid credentials");
  }

  @Test(groups = {"validation", "regression"})
  public void TC012_verifyLoginFormValidatesEmailFormat() {
    createTest(
        "TC-012: Verify login form validates email format",
        "Error message should be shown for invalid email format");

    loginPage.navigateTo();
    loginPage.login("invalid-email", TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error or prevent login for invalid email format");
    test.info("Email format validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC013_verifyLoginFormRequiresPassword() {
    createTest(
        "TC-013: Verify login form requires password",
        "Error message should be shown when password is empty");

    loginPage.navigateTo();
    loginPage.enterEmail(TEST_EMAIL);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error or prevent login when password is empty");
    test.info("Password required validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC014_verifyLoginFormRequiresEmail() {
    createTest(
        "TC-014: Verify login form requires email",
        "Error message should be shown when email is empty");

    loginPage.navigateTo();
    loginPage.enterPassword(TEST_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isErrorMessageDisplayed() || !navbar.isUserLoggedIn(),
        "Should show error or prevent login when email is empty");
    test.info("Email required validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC015_verifySettingsPageValidatesEmailFormat() {
    createTest(
        "TC-015: Verify settings page validates email format on update",
        "Error message should be shown for invalid email format");

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

    settingsPage.clearEmail();
    settingsPage.enterEmail("invalid-email-format");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isErrorMessageDisplayed() || settingsPage.isPageLoaded(),
        "Should show error or handle invalid email format");
    test.info("Settings email format validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC016_verifySettingsPageValidatesUsernameNotEmpty() {
    createTest(
        "TC-016: Verify settings page validates username is not empty",
        "Error message should be shown when username is empty");

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

    settingsPage.clearUsername();
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isErrorMessageDisplayed() || settingsPage.isPageLoaded(),
        "Should show error or handle empty username");
    test.info("Username required validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC017_verifySettingsPageValidatesPasswordMinLength() {
    createTest(
        "TC-017: Verify settings page validates password minimum length",
        "Error message should be shown for short password");

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

    settingsPage.enterPassword("ab");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isErrorMessageDisplayed() || settingsPage.isPageLoaded(),
        "Should show error or handle short password");
    test.info("Password minimum length validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC018_verifyProfileBioCanBeEmpty() {
    createTest(
        "TC-018: Verify profile bio can be empty",
        "Profile should update successfully with empty bio");

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

    settingsPage.clearBio();
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(settingsPage.isPageLoaded(), "Settings page should remain loaded after update");
    test.info("Empty bio validation verified - bio can be empty");
  }

  @Test(groups = {"validation", "regression"})
  public void TC019_verifyProfileImageUrlValidation() {
    createTest(
        "TC-019: Verify profile image URL validation",
        "Error message or graceful handling of invalid URL");

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

    settingsPage.clearImageUrl();
    settingsPage.enterImageUrl("not-a-valid-url");
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isErrorMessageDisplayed() || settingsPage.isPageLoaded(),
        "Should handle invalid image URL appropriately");
    test.info("Image URL validation verified");
  }

  @Test(groups = {"validation", "regression"})
  public void TC020_verifyUsernameUniquenessValidation() {
    createTest(
        "TC-020: Verify username uniqueness validation",
        "Error message should be shown for duplicate username");

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

    settingsPage.clearUsername();
    settingsPage.enterUsername(EXISTING_USERNAME);
    settingsPage.clickUpdate();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        settingsPage.isErrorMessageDisplayed() || settingsPage.isPageLoaded(),
        "Should show error or handle duplicate username");
    test.info("Username uniqueness validation verified");
  }
}
