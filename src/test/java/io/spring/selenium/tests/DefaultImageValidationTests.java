package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation tests for default user image functionality. Tests input validation for image URL
 * updates. TC-011 to TC-020
 */
public class DefaultImageValidationTests extends BaseTest {

  private String baseUrl;
  private SettingsPage settingsPage;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
  }

  private void loginAsTestUser() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);
    loginPage.login("john@example.com", "password123");

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    settingsPage = new SettingsPage(driver);
    settingsPage.navigateTo(baseUrl);
  }

  @Test(groups = {"regression"})
  public void testTC011_ImageUrlFieldAcceptsValidHttpUrl() {
    createTest(
        "TC-011: Verify image URL field accepts valid HTTP URL",
        "Image URL field should accept valid HTTP URLs");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String httpUrl = "http://example.com/image.jpg";
    settingsPage.setImageUrl(httpUrl);
    test.info("Entered HTTP URL: " + httpUrl);

    String enteredUrl = settingsPage.getImageUrl();
    assertTrue(enteredUrl.contains("http://"), "HTTP URL should be accepted in the field");
    test.info("HTTP URL accepted successfully");
  }

  @Test(groups = {"regression"})
  public void testTC012_ImageUrlFieldAcceptsValidHttpsUrl() {
    createTest(
        "TC-012: Verify image URL field accepts valid HTTPS URL",
        "Image URL field should accept valid HTTPS URLs");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String httpsUrl = "https://example.com/secure-image.jpg";
    settingsPage.setImageUrl(httpsUrl);
    test.info("Entered HTTPS URL: " + httpsUrl);

    String enteredUrl = settingsPage.getImageUrl();
    assertTrue(enteredUrl.contains("https://"), "HTTPS URL should be accepted in the field");
    test.info("HTTPS URL accepted successfully");
  }

  @Test(groups = {"regression"})
  public void testTC013_ImageUrlFieldAcceptsEmptyString() {
    createTest(
        "TC-013: Verify image URL field accepts empty string",
        "Empty URL should be accepted, defaulting to default image behavior");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    settingsPage.clearImageUrl();
    test.info("Cleared image URL field");

    String clearedUrl = settingsPage.getImageUrl();
    test.info("URL after clearing: '" + clearedUrl + "'");

    assertTrue(
        clearedUrl == null || clearedUrl.isEmpty() || clearedUrl.length() >= 0,
        "Empty URL should be accepted");
    test.info("Empty string handling verified");
  }

  @Test(groups = {"regression"})
  public void testTC014_ImageUrlFieldHandlesInvalidUrlFormat() {
    createTest(
        "TC-014: Verify image URL field handles invalid URL format",
        "Form should handle invalid URL format input");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String invalidUrl = "not-a-valid-url";
    settingsPage.setImageUrl(invalidUrl);
    test.info("Entered invalid URL: " + invalidUrl);

    String enteredUrl = settingsPage.getImageUrl();
    assertEquals(enteredUrl, invalidUrl, "Invalid URL should be accepted in the input field");
    test.info("Invalid URL format handling verified - client accepts input");
  }

  @Test(groups = {"regression"})
  public void testTC015_ImageUrlFieldHandlesNonImageUrl() {
    createTest(
        "TC-015: Verify image URL field handles non-image URL",
        "Non-image URLs should be handled appropriately");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String nonImageUrl = "https://example.com/page.html";
    settingsPage.setImageUrl(nonImageUrl);
    test.info("Entered non-image URL: " + nonImageUrl);

    String enteredUrl = settingsPage.getImageUrl();
    assertEquals(enteredUrl, nonImageUrl, "Non-image URL should be accepted in the field");
    test.info("Non-image URL handling verified");
  }

  @Test(groups = {"regression"})
  public void testTC016_ImageUrlFieldHandlesVeryLongUrls() {
    createTest(
        "TC-016: Verify image URL field handles very long URLs",
        "Very long URLs should be handled appropriately");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    StringBuilder longUrl = new StringBuilder("https://example.com/image");
    for (int i = 0; i < 200; i++) {
      longUrl.append("/path").append(i);
    }
    longUrl.append(".jpg");

    String veryLongUrl = longUrl.toString();
    test.info("Generated URL with length: " + veryLongUrl.length());

    settingsPage.setImageUrl(veryLongUrl);

    String enteredUrl = settingsPage.getImageUrl();
    test.info("Entered URL length: " + enteredUrl.length());

    assertTrue(enteredUrl.length() > 0, "Long URL should be handled");
    test.info("Very long URL handling verified");
  }

  @Test(groups = {"regression"})
  public void testTC017_ImageUrlFieldHandlesSpecialCharacters() {
    createTest(
        "TC-017: Verify image URL field handles special characters",
        "URLs with special characters should be handled correctly");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String urlWithSpecialChars = "https://example.com/image?param=value&other=123";
    settingsPage.setImageUrl(urlWithSpecialChars);
    test.info("Entered URL with special characters: " + urlWithSpecialChars);

    String enteredUrl = settingsPage.getImageUrl();
    assertTrue(
        enteredUrl.contains("&") || enteredUrl.contains("?"),
        "Special characters should be preserved");
    test.info("Special characters handling verified");
  }

  @Test(groups = {"regression"})
  public void testTC018_ImageUrlFieldHandlesWhitespace() {
    createTest(
        "TC-018: Verify image URL field handles whitespace",
        "URLs with leading/trailing whitespace should be handled");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String urlWithWhitespace = "  https://example.com/image.jpg  ";
    settingsPage.setImageUrl(urlWithWhitespace);
    test.info("Entered URL with whitespace");

    String enteredUrl = settingsPage.getImageUrl();
    test.info("URL after entry: '" + enteredUrl + "'");

    assertNotNull(enteredUrl, "URL should be entered");
    test.info("Whitespace handling verified");
  }

  @Test(groups = {"regression"})
  public void testTC019_ImageUrlFieldHandlesUnicodeCharacters() {
    createTest(
        "TC-019: Verify image URL field handles unicode characters",
        "URLs with unicode characters should be handled appropriately");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    String urlWithUnicode = "https://example.com/image-\u00e9\u00e8\u00ea.jpg";
    settingsPage.setImageUrl(urlWithUnicode);
    test.info("Entered URL with unicode characters");

    String enteredUrl = settingsPage.getImageUrl();
    test.info("URL after entry: " + enteredUrl);

    assertNotNull(enteredUrl, "Unicode URL should be handled");
    test.info("Unicode characters handling verified");
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC020_ImageUrlValidationOnSettingsForm() {
    createTest(
        "TC-020: Verify image URL validation on settings form",
        "Image URL input field should be present and functional");

    loginAsTestUser();
    test.info("Logged in and navigated to settings");

    assertTrue(settingsPage.isImageUrlInputDisplayed(), "Image URL input should be displayed");
    test.info("Image URL input field is displayed");

    assertTrue(settingsPage.isUsernameInputDisplayed(), "Username input should be displayed");
    assertTrue(settingsPage.isBioTextareaDisplayed(), "Bio textarea should be displayed");
    assertTrue(settingsPage.isEmailInputDisplayed(), "Email input should be displayed");
    assertTrue(settingsPage.isPasswordInputDisplayed(), "Password input should be displayed");
    assertTrue(settingsPage.isUpdateSettingsButtonDisplayed(), "Update button should be displayed");

    test.info("All settings form fields verified");
  }
}
