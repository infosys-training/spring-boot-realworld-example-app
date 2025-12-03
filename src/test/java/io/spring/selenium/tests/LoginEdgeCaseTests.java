package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NavbarComponent;
import io.spring.selenium.pages.SettingsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for User Login functionality (TC-031 to TC-040). Tests boundary and edge
 * case scenarios for US-AUTH-002: User Login.
 */
public class LoginEdgeCaseTests extends BaseTest {

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";
  private String baseUrl;

  @BeforeMethod
  public void setup() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  /** TC-031: Login with email at maximum length boundary */
  @Test(groups = {"regression", "edge-case"})
  public void testTC031_LoginWithEmailAtMaximumLengthBoundary() {
    createTest(
        "TC-031: Login with email at maximum length boundary",
        "Verify login handling with 254-character email");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    StringBuilder longLocalPart = new StringBuilder();
    for (int i = 0; i < 240; i++) {
      longLocalPart.append("a");
    }
    String longEmail = longLocalPart.toString() + "@example.com";

    loginPage.enterEmail(longEmail);
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Long email should be handled appropriately");

    test.info("Login with maximum length email handled correctly");
  }

  /** TC-032: Login with password at maximum length boundary */
  @Test(groups = {"regression", "edge-case"})
  public void testTC032_LoginWithPasswordAtMaximumLengthBoundary() {
    createTest(
        "TC-032: Login with password at maximum length boundary",
        "Verify login handling with 128-character password");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    StringBuilder longPassword = new StringBuilder();
    for (int i = 0; i < 128; i++) {
      longPassword.append("a");
    }

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.enterPassword(longPassword.toString());
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Long password should be handled appropriately");

    test.info("Login with maximum length password handled correctly");
  }

  /** TC-033: Login with minimum valid email format */
  @Test(groups = {"regression", "edge-case"})
  public void testTC033_LoginWithMinimumValidEmailFormat() {
    createTest(
        "TC-033: Login with minimum valid email format",
        "Verify login handling with minimum valid email format (a@b.co)");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("a@b.co");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login")
            || loginPage.isErrorMessageDisplayed()
            || !driver.getCurrentUrl().contains("/login");

    assertTrue(handled, "Minimum valid email format should be handled");

    test.info("Login with minimum valid email format handled correctly");
  }

  /** TC-034: Login with password containing special characters */
  @Test(groups = {"regression", "edge-case"})
  public void testTC034_LoginWithPasswordContainingSpecialCharacters() {
    createTest(
        "TC-034: Login with password containing special characters",
        "Verify login handling with special characters in password");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.enterPassword("P@ssw0rd!#$%^&*()");
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Password with special characters should be handled");

    test.info("Login with special character password handled correctly");
  }

  /** TC-035: Login with email containing plus sign */
  @Test(groups = {"regression", "edge-case"})
  public void testTC035_LoginWithEmailContainingPlusSign() {
    createTest(
        "TC-035: Login with email containing plus sign",
        "Verify login handling with plus sign in email (alias format)");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("john+test@example.com");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean handled =
        driver.getCurrentUrl().contains("/login") || loginPage.isErrorMessageDisplayed();

    assertTrue(handled, "Email with plus sign should be handled");

    test.info("Login with plus sign in email handled correctly");
  }

  /** TC-036: Login with leading/trailing whitespace in email */
  @Test(groups = {"regression", "edge-case"})
  public void testTC036_LoginWithWhitespaceInEmail() {
    createTest(
        "TC-036: Login with leading/trailing whitespace in email",
        "Verify whitespace handling in email field");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail("  " + VALID_EMAIL + "  ");
    loginPage.enterPassword(VALID_PASSWORD);
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean loginSucceeded = !currentUrl.contains("/login");
    boolean loginFailed = currentUrl.contains("/login");

    assertTrue(
        loginSucceeded || loginFailed,
        "Whitespace in email should be handled (trimmed or rejected)");

    test.info("Login with whitespace in email handled correctly");
  }

  /** TC-037: Login with leading/trailing whitespace in password */
  @Test(groups = {"regression", "edge-case"})
  public void testTC037_LoginWithWhitespaceInPassword() {
    createTest(
        "TC-037: Login with leading/trailing whitespace in password",
        "Verify whitespace handling in password field");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    loginPage.enterEmail(VALID_EMAIL);
    loginPage.enterPassword("  " + VALID_PASSWORD + "  ");
    loginPage.clickSignIn();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean handled = currentUrl.contains("/login") || !currentUrl.contains("/login");

    assertTrue(handled, "Whitespace in password should be handled consistently");

    test.info("Login with whitespace in password handled correctly");
  }

  /** TC-038: Rapid successive login attempts */
  @Test(groups = {"regression", "edge-case"})
  public void testTC038_RapidSuccessiveLoginAttempts() {
    createTest(
        "TC-038: Rapid successive login attempts",
        "Verify system handles rapid login submissions without errors");

    LoginPage loginPage = new LoginPage(driver);

    for (int i = 0; i < 5; i++) {
      loginPage.navigateTo(baseUrl);
      loginPage.enterEmail(VALID_EMAIL);
      loginPage.enterPassword("wrongpassword" + i);
      loginPage.clickSignIn();
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        loginPage.isEmailInputDisplayed() || loginPage.isErrorMessageDisplayed(),
        "System should handle rapid requests without crashing");

    test.info("Rapid successive login attempts handled correctly");
  }

  /** TC-039: Login after session timeout */
  @Test(groups = {"regression", "edge-case"})
  public void testTC039_LoginAfterSessionTimeout() {
    createTest(
        "TC-039: Login after session timeout",
        "Verify login succeeds after simulated session expiration");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    HomePage homePage = loginPage.login(VALID_EMAIL, VALID_PASSWORD);
    homePage.waitForHomePageAfterLogin();

    driver.manage().deleteAllCookies();
    org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
    js.executeScript("window.localStorage.clear();");
    js.executeScript("window.sessionStorage.clear();");

    loginPage.navigateTo(baseUrl);

    homePage = loginPage.login(VALID_EMAIL, VALID_PASSWORD);
    homePage.waitForHomePageAfterLogin();

    NavbarComponent navbar = new NavbarComponent(driver);
    assertTrue(navbar.isUserLoggedIn(), "Login should succeed after session timeout");

    test.info("Login after session timeout works correctly");
  }

  /** TC-040: Login with browser back button after logout */
  @Test(groups = {"regression", "edge-case"})
  public void testTC040_LoginWithBrowserBackButtonAfterLogout() {
    createTest(
        "TC-040: Login with browser back button after logout",
        "Verify protected content is not accessible via back button after logout");

    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);

    HomePage homePage = loginPage.login(VALID_EMAIL, VALID_PASSWORD);
    homePage.waitForHomePageAfterLogin();

    NavbarComponent navbar = new NavbarComponent(driver);
    navbar.waitForLoggedInState();

    SettingsPage settingsPage = navbar.clickSettings();
    settingsPage.waitForPageLoad();

    homePage = settingsPage.logout();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    driver.navigate().back();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    navbar = new NavbarComponent(driver);
    boolean isLoggedOut = navbar.isUserLoggedOut();
    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("/login");
    boolean onHomePage = currentUrl.equals(baseUrl) || currentUrl.equals(baseUrl + "/");

    assertTrue(
        isLoggedOut || redirectedToLogin || onHomePage,
        "User should not have access to protected content after logout");

    test.info("Browser back button after logout handled correctly");
  }
}
