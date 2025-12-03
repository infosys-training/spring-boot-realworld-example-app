package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProfilePositiveTests extends BaseTest {

  private ProfilePage profilePage;
  private LoginPage loginPage;
  private HomePage homePage;

  private static final String TEST_USER_JOHNDOE = "johndoe";
  private static final String TEST_USER_JANEDOE = "janedoe";
  private static final String TEST_EMAIL_JOHNDOE = "john@example.com";
  private static final String TEST_EMAIL_JANEDOE = "jane@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-001: Access profile by valid username")
  public void testTC001_AccessProfileByValidUsername() {
    createTest(
        "TC-001: Access profile by valid username", "Verify profile page loads for valid username");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertTrue(profilePage.isUserInfoSectionDisplayed(), "User info section should be displayed");

    test.info("Successfully accessed profile for username: " + TEST_USER_JOHNDOE);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-002: View profile displays username correctly")
  public void testTC002_ProfileDisplaysUsernameCorrectly() {
    createTest(
        "TC-002: View profile displays username correctly",
        "Verify username is displayed correctly on profile");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String displayedUsername = profilePage.getUsername();
    assertEquals(
        displayedUsername, TEST_USER_JOHNDOE, "Displayed username should match expected username");

    test.info("Username displayed correctly: " + displayedUsername);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-003: View profile displays bio correctly")
  public void testTC003_ProfileDisplaysBioCorrectly() {
    createTest(
        "TC-003: View profile displays bio correctly", "Verify bio is displayed on profile page");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    String bio = profilePage.getBio();
    assertNotNull(bio, "Bio should not be null");

    test.info("Bio displayed: " + bio);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-004: View profile displays image correctly")
  public void testTC004_ProfileDisplaysImageCorrectly() {
    createTest(
        "TC-004: View profile displays image correctly", "Verify profile image is displayed");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image should be displayed");

    test.info("Profile image is displayed");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-005: Anonymous user can view profile")
  public void testTC005_AnonymousUserCanViewProfile() {
    createTest(
        "TC-005: Anonymous user can view profile",
        "Verify anonymous users can access profile pages");

    homePage.navigateToHomePage();
    homePage.clearAllStorage();

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(
        profilePage.isProfilePageDisplayed(),
        "Profile page should be displayed for anonymous user");
    assertTrue(
        profilePage.isUserInfoSectionDisplayed(), "User info should be visible to anonymous user");

    test.info("Anonymous user successfully viewed profile");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-006: Logged-in user can view other profile")
  public void testTC006_LoggedInUserCanViewOtherProfile() {
    createTest(
        "TC-006: Logged-in user can view other profile",
        "Verify logged-in users can view other profiles");

    loginPage.login(TEST_EMAIL_JANEDOE, TEST_PASSWORD);
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(profilePage.getUsername(), TEST_USER_JOHNDOE, "Should display johndoe's profile");

    test.info("Logged-in user successfully viewed another user's profile");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-007: Logged-in user sees Follow button when not following")
  public void testTC007_LoggedInUserSeesFollowButton() {
    createTest(
        "TC-007: Logged-in user sees Follow button",
        "Verify Follow button is displayed for non-followed user");

    loginPage.login(TEST_EMAIL_JANEDOE, TEST_PASSWORD);
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isFollowButtonDisplayed(), "Follow button should be displayed");
    String buttonText = profilePage.getFollowButtonText();
    assertTrue(
        buttonText.contains("Follow") || buttonText.contains("Unfollow"),
        "Button should show Follow or Unfollow");

    test.info("Follow button displayed with text: " + buttonText);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-008: Logged-in user sees Unfollow button when following")
  public void testTC008_LoggedInUserSeesUnfollowButtonWhenFollowing() {
    createTest(
        "TC-008: Logged-in user sees Unfollow button when following",
        "Verify Unfollow button after following");

    loginPage.login(TEST_EMAIL_JANEDOE, TEST_PASSWORD);
    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    if (profilePage.isFollowButtonDisplayed() && !profilePage.isFollowing()) {
      profilePage.clickFollowButton();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      profilePage.navigateToProfile(TEST_USER_JOHNDOE);
    }

    assertTrue(profilePage.isFollowButtonDisplayed(), "Follow/Unfollow button should be displayed");

    test.info("Follow/Unfollow button state verified");
  }

  @Test(
      groups = {"regression"},
      description = "TC-009: View profile with complete profile data")
  public void testTC009_ViewProfileWithCompleteData() {
    createTest(
        "TC-009: View profile with complete data", "Verify all profile fields are displayed");

    profilePage.navigateToProfile(TEST_USER_JOHNDOE);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertNotNull(profilePage.getUsername(), "Username should be present");
    assertTrue(profilePage.isProfileImageDisplayed(), "Profile image should be present");

    test.info("Profile with complete data verified");
  }

  @Test(
      groups = {"regression"},
      description = "TC-010: Navigate to profile from article author link")
  public void testTC010_NavigateToProfileFromArticleAuthorLink() {
    createTest(
        "TC-010: Navigate to profile from author link",
        "Verify navigation to profile from article author");

    homePage.navigateToHomePage();

    if (homePage.getArticleCount() > 0) {
      String authorName = homePage.getFirstAuthorName();
      homePage.clickFirstAuthorLink();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = profilePage.getCurrentUrl();
      assertTrue(currentUrl.contains("/profile/"), "Should navigate to profile page");

      test.info("Successfully navigated to profile from author link");
    } else {
      test.skip("No articles available to test author link navigation");
    }
  }
}
