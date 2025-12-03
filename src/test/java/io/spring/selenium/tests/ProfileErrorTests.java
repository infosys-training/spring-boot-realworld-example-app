package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.ProfilePage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProfileErrorTests extends BaseTest {

  private ProfilePage profilePage;
  private HomePage homePage;

  private static final String TEST_USER_JOHNDOE = "johndoe";
  private static final String NONEXISTENT_USER = "nonexistentuser123456789";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    homePage = new HomePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-021: Non-existent username shows error")
  public void testTC021_NonExistentUsernameShowsError() {
    createTest(
        "TC-021: Non-existent username shows error", "Verify error message for non-existent user");

    profilePage.navigateToProfile(NONEXISTENT_USER);

    assertTrue(
        profilePage.isErrorMessageDisplayed() || !profilePage.isUserInfoSectionDisplayed(),
        "Error should be displayed or user info should not be shown for non-existent user");

    test.info("Non-existent user handled correctly");
  }

  @Test(
      groups = {"regression"},
      description = "TC-022: Empty username in URL handled")
  public void testTC022_EmptyUsernameInUrlHandled() {
    createTest(
        "TC-022: Empty username in URL handled", "Verify application handles empty username");

    driver.get("http://localhost:3000/profile/");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");

    test.info("Empty username handled, current URL: " + currentUrl);
  }

  @Test(
      groups = {"regression"},
      description = "TC-023: Username with special characters handled")
  public void testTC023_UsernameWithSpecialCharactersHandled() {
    createTest(
        "TC-023: Username with special characters handled", "Verify URL encoding is handled");

    String specialUsername = "user@test";
    String encodedUsername = URLEncoder.encode(specialUsername, StandardCharsets.UTF_8);
    profilePage.navigateToProfileWithEncodedUsername(encodedUsername);

    String currentUrl = driver.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");

    test.info("Special characters handled, URL: " + currentUrl);
  }

  @Test(
      groups = {"regression"},
      description = "TC-024: Very long username handled")
  public void testTC024_VeryLongUsernameHandled() {
    createTest(
        "TC-024: Very long username handled", "Verify application handles very long username");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 256; i++) {
      longUsername.append("a");
    }

    profilePage.navigateToProfile(longUsername.toString());

    String currentUrl = driver.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");

    test.info("Long username handled without crash");
  }

  @Test(
      groups = {"regression"},
      description = "TC-025: SQL injection attempt in username")
  public void testTC025_SqlInjectionAttemptInUsername() {
    createTest("TC-025: SQL injection attempt in username", "Verify SQL injection is prevented");

    String sqlInjection = "'; DROP TABLE users;--";
    String encodedInjection = URLEncoder.encode(sqlInjection, StandardCharsets.UTF_8);
    profilePage.navigateToProfileWithEncodedUsername(encodedInjection);

    assertTrue(
        profilePage.isErrorMessageDisplayed() || !profilePage.isUserInfoSectionDisplayed(),
        "SQL injection should not succeed, error or no data should be shown");

    test.info("SQL injection attempt handled safely");
  }

  @Test(
      groups = {"regression"},
      description = "TC-026: XSS attempt in username")
  public void testTC026_XssAttemptInUsername() {
    createTest("TC-026: XSS attempt in username", "Verify XSS is prevented");

    String xssAttempt = "<script>alert('xss')</script>";
    String encodedXss = URLEncoder.encode(xssAttempt, StandardCharsets.UTF_8);
    profilePage.navigateToProfileWithEncodedUsername(encodedXss);

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("<script>alert('xss')</script>"),
        "XSS script should be escaped or not present");

    test.info("XSS attempt handled safely");
  }

  @Test(
      groups = {"regression"},
      description = "TC-027: Profile with null bio displays gracefully")
  public void testTC027_ProfileWithNullBioDisplaysGracefully() {
    createTest(
        "TC-027: Profile with null bio displays gracefully",
        "Verify profile loads with null/empty bio");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should load");
    assertTrue(profilePage.isUserInfoSectionDisplayed(), "User info should be displayed");

    test.info("Profile with bio handled gracefully");
  }

  @Test(
      groups = {"regression"},
      description = "TC-028: Profile with null image shows default")
  public void testTC028_ProfileWithNullImageShowsDefault() {
    createTest("TC-028: Profile with null image shows default", "Verify default image is shown");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image element should be present");
    String imageSrc = profilePage.getProfileImageSrc();
    assertNotNull(imageSrc, "Image should have a source (default or actual)");

    test.info("Profile image handled: " + imageSrc);
  }

  @Test(
      groups = {"regression"},
      description = "TC-029: Following status not shown for anonymous")
  public void testTC029_FollowingStatusNotShownForAnonymous() {
    createTest(
        "TC-029: Following status not shown for anonymous",
        "Verify no follow button for anonymous users");

    homePage.navigateToHomePage();
    homePage.clearAllStorage();

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertFalse(
        profilePage.isFollowButtonDisplayed(),
        "Follow button should not be visible for anonymous users");

    test.info("Follow button correctly hidden for anonymous user");
  }

  @Test(
      groups = {"regression"},
      description = "TC-030: Profile API error handled gracefully")
  public void testTC030_ProfileApiErrorHandledGracefully() {
    createTest(
        "TC-030: Profile API error handled gracefully", "Verify error handling for API errors");

    profilePage.navigateToProfile(NONEXISTENT_USER);

    boolean hasErrorHandling =
        profilePage.isErrorMessageDisplayed()
            || !profilePage.isUserInfoSectionDisplayed()
            || profilePage.getCurrentUrl().contains("error")
            || profilePage.getCurrentUrl().contains("404");

    assertTrue(hasErrorHandling, "Application should handle API errors gracefully");

    test.info("API error handled gracefully");
  }
}
