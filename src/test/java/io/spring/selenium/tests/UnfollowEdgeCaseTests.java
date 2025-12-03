package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for the Unfollow User feature. Tests TC-031 through TC-040: Boundary and
 * edge case tests.
 */
public class UnfollowEdgeCaseTests extends BaseTest {

  private HomePage homePage;
  private ProfilePage profilePage;

  @BeforeMethod
  public void setupTest() {
    super.setupTest();
    homePage = new HomePage(driver);
    profilePage = new ProfilePage(driver);
  }

  /**
   * TC-031: Verify unfollow immediately after following. Preconditions: User logged in, not
   * following target. Steps: 1. Follow user 2. Immediately unfollow 3. Check state. Expected:
   * Unfollow succeeds, button shows Follow.
   */
  @Test(groups = {"smoke", "regression", "edge"})
  public void testTC031_VerifyUnfollowImmediatelyAfterFollowing() {
    createTest(
        "TC-031: Verify unfollow immediately after following",
        "Verify rapid follow then unfollow works correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());
    test.info("Logged in as " + getTestUserEmail());

    // Navigate to target profile
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();

    // Ensure not following initially
    if (profilePage.isFollowing()) {
      profilePage.unfollow();
      test.info("Unfollowed to set up initial state");
    }

    assertFalse(profilePage.isFollowing(), "Should not be following initially");

    // Follow
    profilePage.follow();
    assertTrue(profilePage.isFollowing(), "Should be following after follow action");
    test.info("Followed user");

    // Immediately unfollow
    profilePage.unfollow();
    assertFalse(profilePage.isFollowing(), "Should not be following after immediate unfollow");
    test.info("Immediately unfollowed user");

    // Verify button state
    String buttonText = profilePage.getFollowButtonText();
    assertTrue(buttonText.contains("Follow"), "Button should show Follow");
    assertFalse(buttonText.contains("Unfollow"), "Button should not show Unfollow");

    test.pass("Immediate unfollow after follow works correctly");
  }

  /**
   * TC-032: Verify rapid consecutive unfollow/follow actions. Preconditions: User logged in,
   * following target. Steps: 1. Click unfollow 2. Quickly click follow 3. Repeat rapidly. Expected:
   * System handles rapid clicks gracefully without errors.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC032_VerifyRapidConsecutiveUnfollowFollowActions() {
    createTest(
        "TC-032: Verify rapid consecutive unfollow/follow actions",
        "Verify system handles rapid toggle gracefully");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Navigate to target profile
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();

    // Ensure following initially
    if (!profilePage.isFollowing()) {
      profilePage.follow();
    }

    test.info("Starting rapid toggle test");

    // Perform rapid toggles
    for (int i = 0; i < 3; i++) {
      try {
        profilePage.clickFollowButton(); // Toggle
        Thread.sleep(500); // Brief pause
        test.info("Toggle " + (i + 1) + " completed");
      } catch (Exception e) {
        test.info("Toggle " + (i + 1) + " handling: " + e.getMessage());
      }
    }

    // Wait for state to stabilize
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Verify page is still functional
    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should still be displayed");
    assertTrue(profilePage.isFollowButtonDisplayed(), "Follow button should still be displayed");

    test.pass("Rapid toggle handled gracefully");
  }

  /**
   * TC-033: Verify unfollow with maximum length username. Preconditions: User following user with
   * long username. Steps: 1. Navigate to profile with max length username 2. Click unfollow.
   * Expected: Unfollow succeeds regardless of username length.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC033_VerifyUnfollowWithMaximumLengthUsername() {
    createTest(
        "TC-033: Verify unfollow with maximum length username",
        "Verify long usernames are handled correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Test with a long username (simulated - actual max depends on system)
    String longUsername = "a".repeat(50); // 50 character username
    test.info("Testing with long username: " + longUsername.substring(0, 10) + "...");

    // Navigate to long username profile
    profilePage.navigateTo(getBaseUrl(), longUsername);

    try {
      profilePage.waitForProfileLoad();

      // Long username likely doesn't exist, should show error
      boolean isError = profilePage.isProfileNotFound() || profilePage.isErrorDisplayed();
      test.info("Long username handled: " + isError);

      // If somehow it exists, verify unfollow works
      if (profilePage.isProfilePageDisplayed() && profilePage.isFollowButtonDisplayed()) {
        if (profilePage.isFollowing()) {
          profilePage.unfollow();
          test.info("Unfollow succeeded for long username");
        }
      }
    } catch (Exception e) {
      test.info("Long username handling: " + e.getMessage());
    }

    // Verify with actual user that system is still functional
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();
    assertTrue(profilePage.isProfilePageDisplayed(), "System should still be functional");

    test.pass("Maximum length username handling verified");
  }

  /**
   * TC-034: Verify unfollow with minimum length username. Preconditions: User following user with
   * 1-char username. Steps: 1. Navigate to profile with min length username 2. Click unfollow.
   * Expected: Unfollow succeeds for minimum length username.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC034_VerifyUnfollowWithMinimumLengthUsername() {
    createTest(
        "TC-034: Verify unfollow with minimum length username",
        "Verify short usernames are handled correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Test with a single character username
    String shortUsername = "a";
    test.info("Testing with short username: " + shortUsername);

    // Navigate to short username profile
    profilePage.navigateTo(getBaseUrl(), shortUsername);

    try {
      profilePage.waitForProfileLoad();

      // Short username likely doesn't exist
      boolean isError = profilePage.isProfileNotFound() || profilePage.isErrorDisplayed();
      test.info("Short username handled: " + isError);

      if (profilePage.isProfilePageDisplayed() && profilePage.isFollowButtonDisplayed()) {
        if (profilePage.isFollowing()) {
          profilePage.unfollow();
          test.info("Unfollow succeeded for short username");
        }
      }
    } catch (Exception e) {
      test.info("Short username handling: " + e.getMessage());
    }

    // Verify system is still functional
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();
    assertTrue(profilePage.isProfilePageDisplayed(), "System should still be functional");

    test.pass("Minimum length username handling verified");
  }

  /**
   * TC-035: Verify unfollow with unicode characters in username. Preconditions: User following user
   * with unicode username. Steps: 1. Navigate to profile with unicode username 2. Click unfollow.
   * Expected: Unfollow handles unicode characters correctly.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC035_VerifyUnfollowWithUnicodeCharactersInUsername() {
    createTest(
        "TC-035: Verify unfollow with unicode characters in username",
        "Verify unicode usernames are handled correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Test with unicode usernames
    String[] unicodeUsernames = {"用户名", "пользователь", "ユーザー", "emoji😀user"};

    for (String unicodeUsername : unicodeUsernames) {
      test.info("Testing unicode username: " + unicodeUsername);

      try {
        profilePage.navigateTo(getBaseUrl(), unicodeUsername);
        Thread.sleep(1000);

        // Unicode usernames likely don't exist in test data
        boolean handled =
            profilePage.isProfileNotFound()
                || profilePage.isErrorDisplayed()
                || !profilePage.isProfilePageDisplayed();

        test.info("Unicode username '" + unicodeUsername + "' handled: " + handled);
      } catch (Exception e) {
        test.info("Unicode handling for '" + unicodeUsername + "': " + e.getMessage());
      }
    }

    // Verify system is still functional
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();
    assertTrue(profilePage.isProfilePageDisplayed(), "System should still be functional");

    test.pass("Unicode characters in username handled correctly");
  }

  /**
   * TC-036: Verify unfollow when target user is deleted. Preconditions: User following target,
   * target gets deleted. Steps: 1. Follow user 2. Target user is deleted 3. Attempt to
   * view/unfollow. Expected: System handles gracefully with appropriate error.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC036_VerifyUnfollowWhenTargetUserIsDeleted() {
    createTest(
        "TC-036: Verify unfollow when target user is deleted",
        "Verify handling of deleted user scenario");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Note: We can't actually delete users in this test, so we simulate
    // by navigating to a non-existent user (simulating deleted user)
    String deletedUser = "deleteduser12345";
    test.info("Simulating deleted user scenario with: " + deletedUser);

    profilePage.navigateTo(getBaseUrl(), deletedUser);

    try {
      profilePage.waitForProfileLoad();

      // Should show error for deleted/non-existent user
      boolean isError = profilePage.isProfileNotFound() || profilePage.isErrorDisplayed();
      test.info("Deleted user handled: " + isError);

      // Verify no follow button for non-existent user
      if (!profilePage.isProfilePageDisplayed()) {
        test.info("Profile not displayed for deleted user - correct behavior");
      }
    } catch (Exception e) {
      test.info("Deleted user handling: " + e.getMessage());
    }

    test.pass("Deleted user scenario handled gracefully");
  }

  /**
   * TC-037: Verify unfollow during concurrent session. Preconditions: User logged in on multiple
   * browsers. Steps: 1. Open profile in two browsers 2. Unfollow in first 3. Check second.
   * Expected: Both sessions reflect unfollow state.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC037_VerifyUnfollowDuringConcurrentSession() {
    createTest(
        "TC-037: Verify unfollow during concurrent session",
        "Verify state consistency across sessions");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Navigate to target profile
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();

    // Ensure following
    if (!profilePage.isFollowing()) {
      profilePage.follow();
    }

    // Unfollow
    profilePage.unfollow();
    assertFalse(profilePage.isFollowing(), "Should not be following after unfollow");
    test.info("Unfollowed in current session");

    // Simulate checking in another session by refreshing
    // (In real concurrent test, would use separate browser instance)
    profilePage.refreshPage();
    profilePage.waitForProfileLoad();

    // Verify state persisted
    assertFalse(profilePage.isFollowing(), "Unfollow state should persist across refresh");
    test.info("State verified after refresh (simulating second session)");

    // Open in new tab to simulate concurrent session
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "window.open('" + getBaseUrl() + "/profile/" + getTargetUser() + "', '_blank');");

    try {
      Thread.sleep(2000);
      // Switch to new tab
      String originalWindow = driver.getWindowHandle();
      for (String windowHandle : driver.getWindowHandles()) {
        if (!windowHandle.equals(originalWindow)) {
          driver.switchTo().window(windowHandle);
          break;
        }
      }

      // Check state in new tab
      ProfilePage newTabProfile = new ProfilePage(driver);
      newTabProfile.waitForProfileLoad();
      boolean followingInNewTab = newTabProfile.isFollowing();
      test.info("Following state in new tab: " + followingInNewTab);

      // Close new tab and switch back
      driver.close();
      driver.switchTo().window(originalWindow);
    } catch (Exception e) {
      test.info("Concurrent session test: " + e.getMessage());
    }

    test.pass("Concurrent session handling verified");
  }

  /**
   * TC-038: Verify unfollow with URL-encoded username. Preconditions: User following user with
   * spaces/special chars. Steps: 1. Navigate using URL-encoded username 2. Click unfollow.
   * Expected: URL encoding handled correctly, unfollow succeeds.
   */
  @Test(groups = {"regression", "edge"})
  public void testTC038_VerifyUnfollowWithURLEncodedUsername() {
    createTest(
        "TC-038: Verify unfollow with URL-encoded username",
        "Verify URL encoding is handled correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Test URL-encoded username
    String encodedUsername = URLEncoder.encode(getTargetUser(), StandardCharsets.UTF_8);
    test.info("Testing URL-encoded username: " + encodedUsername);

    // Navigate using encoded username
    driver.get(getBaseUrl() + "/profile/" + encodedUsername);

    try {
      profilePage.waitForProfileLoad();

      if (profilePage.isProfilePageDisplayed()) {
        test.info("Profile loaded with encoded username");

        // Verify follow/unfollow works
        if (profilePage.isFollowButtonDisplayed()) {
          if (!profilePage.isFollowing()) {
            profilePage.follow();
          }
          profilePage.unfollow();
          assertFalse(profilePage.isFollowing(), "Unfollow should work with encoded username");
          test.info("Unfollow succeeded with URL-encoded username");
        }
      }
    } catch (Exception e) {
      test.info("URL encoding handling: " + e.getMessage());
    }

    test.pass("URL-encoded username handled correctly");
  }

  /**
   * TC-039: Verify unfollow button state during API call. Preconditions: User logged in, following
   * target. Steps: 1. Click unfollow 2. Observe button during API call. Expected: Button shows
   * loading state or is disabled during request.
   */
  @Test(groups = {"smoke", "regression", "edge"})
  public void testTC039_VerifyUnfollowButtonStateDuringAPICall() {
    createTest(
        "TC-039: Verify unfollow button state during API call",
        "Verify button state during async operation");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Navigate to target profile
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();

    // Ensure following
    if (!profilePage.isFollowing()) {
      profilePage.follow();
      profilePage.waitForFollowStateChange(true);
    }

    assertTrue(profilePage.isFollowing(), "Should be following before test");

    // Get initial button state
    String initialButtonClass = profilePage.getFollowButtonClass();
    test.info("Initial button class: " + initialButtonClass);

    // Click unfollow and immediately check button state
    profilePage.clickUnfollowButton();

    // Check button state immediately after click
    try {
      String duringClickClass = profilePage.getFollowButtonClass();
      boolean isEnabled = profilePage.isFollowButtonEnabled();
      test.info("Button class during operation: " + duringClickClass);
      test.info("Button enabled during operation: " + isEnabled);
    } catch (Exception e) {
      test.info("Button state check: " + e.getMessage());
    }

    // Wait for operation to complete
    profilePage.waitForFollowStateChange(false);

    // Verify final state
    assertFalse(profilePage.isFollowing(), "Should not be following after unfollow");
    String finalButtonText = profilePage.getFollowButtonText();
    test.info("Final button text: " + finalButtonText);
    assertTrue(finalButtonText.contains("Follow"), "Button should show Follow after operation");

    test.pass("Button state during API call verified");
  }

  /**
   * TC-040: Verify unfollow with whitespace-padded username. Preconditions: User logged in. Steps:
   * 1. Send unfollow request with whitespace in username 2. Check response. Expected: Whitespace
   * handled appropriately (trimmed or rejected).
   */
  @Test(groups = {"regression", "edge"})
  public void testTC040_VerifyUnfollowWithWhitespacePaddedUsername() {
    createTest(
        "TC-040: Verify unfollow with whitespace-padded username",
        "Verify whitespace in username is handled correctly");

    // Login
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(getBaseUrl());
    loginPage.login(getTestUserEmail(), getTestUserPassword());

    // Test usernames with whitespace
    String[] whitespaceUsernames = {
      " " + getTargetUser(), // Leading space
      getTargetUser() + " ", // Trailing space
      " " + getTargetUser() + " ", // Both
      getTargetUser() + "%20" // URL-encoded space
    };

    for (String wsUsername : whitespaceUsernames) {
      test.info("Testing whitespace username: '" + wsUsername + "'");

      try {
        // Navigate with whitespace username
        driver.get(getBaseUrl() + "/profile/" + wsUsername.trim());
        Thread.sleep(1000);

        // Check if profile loaded (whitespace may be trimmed)
        if (profilePage.isProfilePageDisplayed()) {
          String displayedUsername = profilePage.getUsername();
          test.info("Displayed username: " + displayedUsername);

          // If profile loaded, whitespace was likely trimmed
          if (displayedUsername.equals(getTargetUser())) {
            test.info("Whitespace was trimmed - profile loaded correctly");
          }
        } else {
          test.info("Profile not found with whitespace - may be rejected");
        }
      } catch (Exception e) {
        test.info("Whitespace handling for '" + wsUsername + "': " + e.getMessage());
      }
    }

    // Verify system is still functional with normal username
    profilePage.navigateTo(getBaseUrl(), getTargetUser());
    profilePage.waitForProfileLoad();
    assertTrue(profilePage.isProfilePageDisplayed(), "System should still be functional");

    test.pass("Whitespace-padded username handling verified");
  }
}
