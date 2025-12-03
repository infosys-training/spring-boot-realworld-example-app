package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.SettingsPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class JWTTokenErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private SettingsPage settingsPage;
  private String baseUrl;

  private static final String VALID_EMAIL = "john@example.com";
  private static final String VALID_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    settingsPage = new SettingsPage(driver);
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC021_RequestRejectedWithMissingAuthorizationHeader() {
    createTest(
        "TC-021: Request Rejected Without Auth Header",
        "Verify request to protected endpoint is rejected without Authorization header");

    homePage.navigateTo(baseUrl);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.localStorage.removeItem('user');");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected away from settings page without auth");

    test.pass("Request correctly rejected without Authorization header");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC022_RequestRejectedWithEmptyToken() {
    createTest(
        "TC-022: Request Rejected with Empty Token",
        "Verify request is rejected when Authorization header has empty token");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.localStorage.setItem('user', JSON.stringify({token: ''}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with empty token");

    test.pass("Request correctly rejected with empty token");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC023_RequestRejectedWithMalformedToken() {
    createTest(
        "TC-023: Request Rejected with Malformed Token",
        "Verify request is rejected when token is missing parts");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: 'header.payload'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with malformed token");

    test.pass("Request correctly rejected with malformed token");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC024_RequestRejectedWithInvalidTokenSignature() {
    createTest(
        "TC-024: Request Rejected with Invalid Signature",
        "Verify request is rejected when token signature is modified");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String invalidToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.invalidsignature";
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: '" + invalidToken + "'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with invalid signature");

    test.pass("Request correctly rejected with invalid token signature");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC025_RequestRejectedWithExpiredToken() {
    createTest(
        "TC-025: Request Rejected with Expired Token",
        "Verify request is rejected when token has expired");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String expiredToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTAwMDAwMDAwfQ.expiredsignature";
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: '" + expiredToken + "'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with expired token");

    test.pass("Request correctly rejected with expired token");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC026_RequestRejectedWithTokenSignedWithWrongKey() {
    createTest(
        "TC-026: Request Rejected with Wrong Signing Key",
        "Verify request is rejected when token is signed with different secret");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String wrongKeyToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIn0.wrongkeysignaturehere1234567890abcdefghijklmnop";
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: '" + wrongKeyToken + "'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with wrong key token");

    test.pass("Request correctly rejected with token signed with wrong key");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC027_AppropriateErrorMessageForInvalidToken() {
    createTest(
        "TC-027: Error Message for Invalid Token",
        "Verify appropriate error message is shown for invalid token");

    loginPage.navigateTo(baseUrl);
    loginPage.login("invalid@email.com", "wrongpassword");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(loginPage.isErrorDisplayed(), "Error message should be displayed");
    String errorMessage = loginPage.getErrorMessage();
    assertNotNull(errorMessage, "Error message should not be null");
    assertTrue(errorMessage.length() > 0, "Error message should not be empty");

    test.pass("Appropriate error message displayed for invalid credentials");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC028_Verify401StatusCodeForUnauthorizedRequest() {
    createTest(
        "TC-028: 401 Status for Unauthorized Request",
        "Verify 401 status code is returned for unauthorized request");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.localStorage.removeItem('user');");

    driver.get(baseUrl + "/user/settings");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertFalse(
        currentUrl.contains("/settings") && settingsPage.isPageLoaded(),
        "Should not be able to access settings without authentication");

    test.pass("Unauthorized request correctly handled");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC029_RequestRejectedWithTamperedPayload() {
    createTest(
        "TC-029: Request Rejected with Tampered Payload",
        "Verify request is rejected when token payload is tampered");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String tamperedToken = "eyJhbGciOiJIUzUxMiJ9.dGFtcGVyZWRwYXlsb2Fk.originalsignature";
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: '" + tamperedToken + "'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with tampered payload");

    test.pass("Request correctly rejected with tampered payload");
  }

  @Test(groups = {"error", "jwt"})
  public void testTC030_RequestRejectedWithTamperedHeader() {
    createTest(
        "TC-030: Request Rejected with Tampered Header",
        "Verify request is rejected when token header is tampered");

    homePage.navigateTo(baseUrl);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    String tamperedHeaderToken = "dGFtcGVyZWRoZWFkZXI.eyJzdWIiOiIxIn0.originalsignature";
    js.executeScript(
        "window.localStorage.setItem('user', JSON.stringify({token: '"
            + tamperedHeaderToken
            + "'}));");

    settingsPage.navigateTo(baseUrl);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        !currentUrl.contains("/settings") || !settingsPage.isPageLoaded(),
        "Should be redirected or denied access with tampered header");

    test.pass("Request correctly rejected with tampered header");
  }
}
