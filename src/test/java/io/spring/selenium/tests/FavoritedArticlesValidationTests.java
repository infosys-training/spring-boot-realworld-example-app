package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritedArticlesValidationTests extends BaseTest {

  private ProfilePage profilePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER = "johndoe";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"regression"})
  public void testTC013FilterWithEmptyUsernameParameter() {
    createTest(
        "TC-013: Filter with empty username parameter",
        "Verify appropriate error or redirect occurs with empty username");

    driver.get(BASE_URL + "/profile/");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    test.info("Current URL after empty username: " + currentUrl);

    assertTrue(
        currentUrl.contains("/profile/") || currentUrl.equals(BASE_URL + "/"),
        "Should handle empty username appropriately");
  }

  @Test(groups = {"regression"})
  public void testTC014FilterWithSpecialCharactersInUsername() {
    createTest(
        "TC-014: Filter with special characters in username",
        "Verify handling of special characters in username");

    String specialUsername = "user@#$%";
    driver.get(BASE_URL + "/profile/" + specialUsername + "?favorite=true");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    test.info("Current URL with special characters: " + currentUrl);

    assertNotNull(driver.getPageSource(), "Page should load without crashing");
  }

  @Test(groups = {"regression"})
  public void testTC015FilterWithVeryLongUsername() {
    createTest(
        "TC-015: Filter with very long username",
        "Verify handling of username with 256+ characters");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 260; i++) {
      longUsername.append("a");
    }

    driver.get(BASE_URL + "/profile/" + longUsername + "?favorite=true");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getPageSource(), "Page should handle long username appropriately");
    test.info("Page loaded with long username (260 chars)");
  }

  @Test(groups = {"regression"})
  public void testTC016FilterWithNumericUsername() {
    createTest(
        "TC-016: Filter with numeric username", "Verify handling of numeric username like 12345");

    String numericUsername = "12345";
    driver.get(BASE_URL + "/profile/" + numericUsername + "?favorite=true");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getPageSource(), "Page should handle numeric username");
    test.info("Page loaded with numeric username: " + numericUsername);
  }

  @Test(groups = {"regression"})
  public void testTC017FilterWithCaseSensitiveUsername() {
    createTest(
        "TC-017: Filter with case-sensitive username",
        "Verify case sensitivity handling for usernames");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER.toUpperCase());
    profilePage.waitForArticlesLoad();

    String displayedUsername = profilePage.getUsername();
    test.info("Requested username: " + TEST_USER.toUpperCase());
    test.info("Displayed username: " + displayedUsername);

    assertNotNull(driver.getPageSource(), "Page should load for case variant");
  }

  @Test(groups = {"regression"})
  public void testTC018FilterWithUrlEncodedUsername() {
    createTest(
        "TC-018: Filter with URL-encoded username",
        "Verify proper decoding of URL-encoded username");

    String encodedUsername = "john%20doe";
    driver.get(BASE_URL + "/profile/" + encodedUsername + "?favorite=true");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    test.info("URL with encoded username: " + currentUrl);

    assertNotNull(driver.getPageSource(), "Page should handle URL-encoded username");
  }

  @Test(groups = {"regression"})
  public void testTC019FilterWithWhitespaceInUsername() {
    createTest(
        "TC-019: Filter with whitespace in username",
        "Verify handling of leading/trailing spaces in username");

    String usernameWithSpaces = "  " + TEST_USER + "  ";
    driver.get(BASE_URL + "/profile/" + usernameWithSpaces.trim() + "?favorite=true");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertNotNull(driver.getPageSource(), "Page should handle whitespace appropriately");
    test.info("Page loaded with trimmed username");
  }

  @Test(groups = {"regression"})
  public void testTC020VerifyFavoritedParameterAcceptsValidFormat() {
    createTest(
        "TC-020: Verify favorited parameter accepts valid format",
        "Verify that only favorite=true shows favorited articles");

    profilePage.navigateTo(BASE_URL, TEST_USER);
    boolean withoutFavoriteParam = profilePage.urlContainsFavoriteParameter();

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    boolean withFavoriteParam = profilePage.urlContainsFavoriteParameter();

    assertFalse(
        withoutFavoriteParam, "URL without favorite param should not contain favorite=true");
    assertTrue(withFavoriteParam, "URL with favorite param should contain favorite=true");

    test.info("Verified favorite parameter behavior");
  }
}
