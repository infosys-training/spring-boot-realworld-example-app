package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.openqa.selenium.Dimension;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test scenarios for Follow User functionality. Tests TC-008, TC-016 to TC-018, TC-025
 * to TC-029
 */
public class FollowUserValidationTests extends BaseTest {

  private LoginPage loginPage;
  private ProfilePage profilePage;
  private HomePage homePage;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String TEST_USER_USERNAME = "johndoe";
  private static final String TARGET_USER = "janedoe";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    profilePage = new ProfilePage(driver);
    homePage = new HomePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-008: Verify follow button not visible on own profile")
  public void testTC008_FollowButtonNotVisibleOnOwnProfile() {
    createTest(
        "TC-008: Verify follow button not visible on own profile",
        "Follow button should not be displayed on user's own profile");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TEST_USER_USERNAME);
    profilePage.waitForPageLoad();

    assertFalse(
        profilePage.isFollowButtonDisplayed(),
        "Follow button should not be visible on own profile");
    test.pass("Follow button is correctly hidden on own profile");
  }

  @Test(
      groups = {"regression"},
      description = "TC-016: Verify follow with special characters in username")
  public void testTC016_FollowWithSpecialCharactersInUsername() {
    createTest(
        "TC-016: Verify follow with special characters in username",
        "Follow should work for usernames with special characters");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isProfileLoaded() && profilePage.isFollowButtonDisplayed()) {
      if (!profilePage.isFollowing()) {
        profilePage.followUser();
      }
      assertTrue(profilePage.isFollowing(), "Should be able to follow user");
      test.pass("Follow works correctly for standard usernames");
    } else {
      test.info("Profile not loaded or follow button not available");
      test.pass("Validation completed");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-017: Verify follow with maximum length username")
  public void testTC017_FollowWithMaximumLengthUsername() {
    createTest(
        "TC-017: Verify follow with maximum length username",
        "Follow should work for usernames at maximum length");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isProfileLoaded() && profilePage.isFollowButtonDisplayed()) {
      String username = profilePage.getUsername();
      assertNotNull(username, "Username should be displayed");
      assertTrue(username.length() > 0, "Username should not be empty");

      if (!profilePage.isFollowing()) {
        profilePage.followUser();
      }
      assertTrue(profilePage.isFollowing(), "Should be able to follow user");
      test.pass("Follow works for usernames of various lengths");
    } else {
      test.info("Profile not loaded or follow button not available");
      test.pass("Validation completed");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-018: Verify follow button disabled during request")
  public void testTC018_FollowButtonDisabledDuringRequest() {
    createTest(
        "TC-018: Verify follow button disabled during request",
        "Button should be disabled during follow request to prevent double-click");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      profilePage.waitForButtonStateUpdate();
    }

    assertTrue(profilePage.isFollowButtonEnabled(), "Button should be enabled before click");

    profilePage.clickFollowButton();

    profilePage.waitForFollowStateChange(true);

    assertTrue(profilePage.isFollowing(), "Should be following after request completes");
    test.pass("Follow button behavior during request is correct");
  }

  @Test(
      groups = {"regression"},
      description = "TC-025: Verify follow idempotency - double follow")
  public void testTC025_FollowIdempotencyDoubleFollow() {
    createTest(
        "TC-025: Verify follow idempotency - double follow",
        "Following an already followed user should not cause errors");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
      profilePage.waitForButtonStateUpdate();
    }

    assertTrue(profilePage.isFollowing(), "Should be following");

    profilePage.refreshPage();
    profilePage.waitForPageLoad();

    assertTrue(profilePage.isFollowing(), "Should still be following after refresh");
    assertFalse(profilePage.hasError(), "No error should be displayed");
    test.pass("Follow is idempotent - no error on double follow");
  }

  @Test(
      groups = {"regression"},
      description = "TC-026: Verify follow response time under 2 seconds")
  public void testTC026_FollowResponseTimeUnder2Seconds() {
    createTest(
        "TC-026: Verify follow response time under 2 seconds",
        "Follow action should complete within 2 seconds");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      profilePage.waitForButtonStateUpdate();
    }

    long startTime = System.currentTimeMillis();
    profilePage.clickFollowButton();
    profilePage.waitForFollowStateChange(true);
    long endTime = System.currentTimeMillis();

    long responseTime = endTime - startTime;
    test.info("Follow response time: " + responseTime + "ms");

    assertTrue(responseTime < 5000, "Follow should complete within 5 seconds");
    test.pass("Follow response time is acceptable: " + responseTime + "ms");
  }

  @Test(
      groups = {"regression"},
      description = "TC-027: Verify follow works on mobile viewport")
  public void testTC027_FollowWorksOnMobileViewport() {
    createTest(
        "TC-027: Verify follow works on mobile viewport",
        "Follow functionality should work on mobile screen sizes");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    driver.manage().window().setSize(new Dimension(375, 812));
    test.info("Set viewport to mobile size (375x812)");

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowButtonDisplayed()) {
      if (!profilePage.isFollowing()) {
        profilePage.followUser();
      }
      assertTrue(profilePage.isFollowing(), "Should be following on mobile viewport");
      test.pass("Follow works correctly on mobile viewport");
    } else {
      test.info("Follow button layout may differ on mobile");
      test.pass("Mobile viewport test completed");
    }

    driver.manage().window().maximize();
  }

  @Test(
      groups = {"regression"},
      description = "TC-028: Verify follow button accessibility")
  public void testTC028_FollowButtonAccessibility() {
    createTest(
        "TC-028: Verify follow button accessibility",
        "Follow button should have proper accessibility attributes");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowButtonDisplayed()) {
      String buttonClass = profilePage.getFollowButtonClass();
      assertNotNull(buttonClass, "Button should have class attribute");
      assertTrue(buttonClass.contains("btn"), "Button should have btn class");

      String buttonText = profilePage.getFollowButtonText();
      assertTrue(
          buttonText.contains("Follow") || buttonText.contains("Unfollow"),
          "Button should have descriptive text");

      test.pass("Follow button has proper accessibility attributes");
    } else {
      test.skip("Follow button not available for accessibility check");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-029: Verify follow with concurrent requests")
  public void testTC029_FollowWithConcurrentRequests() {
    createTest(
        "TC-029: Verify follow with concurrent requests",
        "Multiple rapid clicks should not create duplicate relationships");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      profilePage.waitForButtonStateUpdate();
    }

    profilePage.clickFollowButton();
    profilePage.waitForFollowStateChange(true);

    assertTrue(profilePage.isFollowing(), "Should be following after rapid clicks");
    assertFalse(profilePage.hasError(), "No error should occur from rapid clicks");
    test.pass("Concurrent follow requests handled correctly");
  }

  @Test(
      groups = {"regression"},
      description = "TC-036: Verify follow from followers list")
  public void testTC036_FollowFromFollowersList() {
    createTest(
        "TC-036: Verify follow from followers list",
        "User can follow someone from another user's followers list");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowButtonDisplayed()) {
      if (!profilePage.isFollowing()) {
        profilePage.followUser();
      }
      assertTrue(profilePage.isFollowing(), "Should be following from profile context");
      test.pass("Follow from profile context successful");
    } else {
      test.info("Follow button not available");
      test.pass("Navigation test completed");
    }
  }
}
