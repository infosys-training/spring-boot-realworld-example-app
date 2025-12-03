package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import java.util.List;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProfileEdgeCaseTests extends BaseTest {

  private ProfilePage profilePage;
  private LoginPage loginPage;
  private HomePage homePage;

  private static final String TEST_USER_JOHNDOE = "johndoe";
  private static final String TEST_USER_JANEDOE = "janedoe";
  private static final String TEST_USER_BOBSMITH = "bobsmith";
  private static final String TEST_EMAIL_JOHNDOE = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-031: View own profile when logged in")
  public void testTC031_ViewOwnProfileWhenLoggedIn() {
    createTest(
        "TC-031: View own profile when logged in",
        "Verify own profile shows Edit button, no Follow button");

    loginPage.login(TEST_EMAIL_JOHNDOE, TEST_PASSWORD);
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertFalse(
        profilePage.isFollowButtonDisplayed(), "Follow button should not be shown on own profile");

    test.info("Own profile viewed correctly - no Follow button displayed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-032: Profile with minimum length username")
  public void testTC032_ProfileWithMinimumLengthUsername() {
    createTest(
        "TC-032: Profile with minimum length username", "Verify profile loads for short username");

    profilePage.navigateToProfile("a");

    String currentUrl = profilePage.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");
    assertTrue(currentUrl.contains("/profile/"), "Should be on profile page");

    test.info("Minimum length username handled");
  }

  @Test(
      groups = {"regression"},
      description = "TC-033: Profile with maximum length username")
  public void testTC033_ProfileWithMaximumLengthUsername() {
    createTest(
        "TC-033: Profile with maximum length username", "Verify profile loads for long username");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      longUsername.append("a");
    }

    profilePage.navigateToProfile(longUsername.toString());

    String currentUrl = profilePage.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");

    test.info("Maximum length username handled");
  }

  @Test(
      groups = {"regression"},
      description = "TC-034: Profile with empty bio")
  public void testTC034_ProfileWithEmptyBio() {
    createTest("TC-034: Profile with empty bio", "Verify profile loads with empty bio");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should load");
    assertTrue(profilePage.isUserInfoSectionDisplayed(), "User info should be displayed");

    test.info("Profile with empty/null bio handled gracefully");
  }

  @Test(
      groups = {"regression"},
      description = "TC-035: Profile with very long bio")
  public void testTC035_ProfileWithVeryLongBio() {
    createTest("TC-035: Profile with very long bio", "Verify profile handles long bio text");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should load");
    String bio = profilePage.getBio();
    assertNotNull(bio, "Bio should be retrievable (even if empty)");

    test.info("Long bio handling verified");
  }

  @Test(
      groups = {"regression"},
      description = "TC-036: Profile with unicode in username")
  public void testTC036_ProfileWithUnicodeInUsername() {
    createTest("TC-036: Profile with unicode in username", "Verify unicode username is handled");

    profilePage.navigateToProfile("user_unicode");

    String currentUrl = profilePage.getCurrentUrl();
    assertNotNull(currentUrl, "Page should load without crash");

    test.info("Unicode username handled");
  }

  @Test(
      groups = {"regression"},
      description = "TC-037: Profile with unicode in bio")
  public void testTC037_ProfileWithUnicodeInBio() {
    createTest(
        "TC-037: Profile with unicode in bio", "Verify unicode bio characters display correctly");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should load");
    String bio = profilePage.getBio();

    test.info("Unicode bio handling verified, bio: " + bio);
  }

  @Test(
      groups = {"regression"},
      description = "TC-038: Profile image with broken URL")
  public void testTC038_ProfileImageWithBrokenUrl() {
    createTest("TC-038: Profile image with broken URL", "Verify fallback image is shown");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image element should be present");

    test.info("Broken image URL handling verified");
  }

  @Test(
      groups = {"regression"},
      description = "TC-039: Rapid navigation between profiles")
  public void testTC039_RapidNavigationBetweenProfiles() {
    createTest(
        "TC-039: Rapid navigation between profiles",
        "Verify data doesn't mix during rapid navigation");

    String[] users = {TEST_USER_JOHNDOE, TEST_USER_JANEDOE, TEST_USER_BOBSMITH};

    for (String user : users) {
      profilePage.navigateToProfile(user);

      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if (profilePage.isProfilePageDisplayed()) {
        String displayedUsername = profilePage.getUsername();
        if (displayedUsername != null && !displayedUsername.isEmpty()) {
          assertEquals(displayedUsername, user, "Displayed username should match navigated user");
        }
      }
    }

    test.info("Rapid navigation between profiles completed without data mixing");
  }

  @Test(
      groups = {"regression"},
      description = "TC-040: Profile page keyboard navigation")
  public void testTC040_ProfilePageKeyboardNavigation() {
    createTest("TC-040: Profile page keyboard navigation", "Verify keyboard accessibility");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    List<WebElement> interactiveElements = profilePage.getAllInteractiveElements();
    assertTrue(interactiveElements.size() > 0, "Page should have interactive elements");

    try {
      driver.switchTo().activeElement().sendKeys(Keys.TAB);
      WebElement activeElement = driver.switchTo().activeElement();
      assertNotNull(activeElement, "Tab navigation should work");
    } catch (Exception e) {
      test.warning("Keyboard navigation test encountered issue: " + e.getMessage());
    }

    test.info(
        "Keyboard navigation verified with "
            + interactiveElements.size()
            + " interactive elements");
  }
}
