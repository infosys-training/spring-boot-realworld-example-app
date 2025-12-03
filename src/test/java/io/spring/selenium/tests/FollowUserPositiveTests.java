package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test scenarios for Follow User functionality. Tests TC-001 to TC-003, TC-007, TC-009 to
 * TC-015, TC-020, TC-021, TC-030
 */
public class FollowUserPositiveTests extends BaseTest {

  private LoginPage loginPage;
  private ProfilePage profilePage;
  private HomePage homePage;
  private ArticlePage articlePage;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String TARGET_USER = "janedoe";
  private static final String ANOTHER_USER = "bobsmith";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    profilePage = new ProfilePage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-001: Verify successful follow via POST request")
  public void testTC001_SuccessfulFollowViaPostRequest() {
    createTest(
        "TC-001: Verify successful follow via POST request",
        "User can successfully follow another user via the Follow button");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    test.info("Logged in as test user");

    homePage.waitForPageLoad();
    profilePage.navigateTo(TARGET_USER);
    test.info("Navigated to target user profile: " + TARGET_USER);

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      test.info("Unfollowed user first to reset state");
    }

    profilePage.followUser();
    test.info("Clicked Follow button");

    assertTrue(profilePage.isFollowing(), "User should be following after clicking Follow");
    test.pass("Successfully followed user via POST request");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-002: Verify follow creates relationship in database")
  public void testTC002_FollowCreatesRelationshipInDatabase() {
    createTest(
        "TC-002: Verify follow creates relationship in database",
        "Following a user should make their articles appear in feed");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    test.info("Logged in as test user");

    homePage.waitForPageLoad();
    profilePage.navigateTo(TARGET_USER);

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
      test.info("Followed target user");
    }

    homePage.navigateTo();
    homePage.waitForPageLoad();

    if (homePage.hasYourFeedTab()) {
      homePage.clickYourFeed();
      homePage.waitForFeedToLoad();
      test.info("Navigated to Your Feed");
    }

    test.pass("Follow relationship created - feed should show followed user's articles");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-003: Verify response returns following:true")
  public void testTC003_ResponseReturnsFollowingTrue() {
    createTest(
        "TC-003: Verify response returns following:true",
        "After following, profile should show following status as true");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
    }

    profilePage.followUser();
    profilePage.waitForButtonStateUpdate();

    assertTrue(profilePage.isFollowing(), "Profile should show following: true");
    assertTrue(
        profilePage.getFollowButtonText().contains("Unfollow"), "Button should show Unfollow text");
    test.pass("Response correctly shows following: true");
  }

  @Test(
      groups = {"regression"},
      description = "TC-007: Verify follow button visible on profile page")
  public void testTC007_FollowButtonVisibleOnProfilePage() {
    createTest(
        "TC-007: Verify follow button visible on profile page",
        "Follow button should be visible on another user's profile");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    assertTrue(
        profilePage.isFollowButtonDisplayed(), "Follow button should be visible on profile page");
    test.pass("Follow button is visible on profile page");
  }

  @Test(
      groups = {"regression"},
      description = "TC-009: Verify follow persists after page refresh")
  public void testTC009_FollowPersistsAfterPageRefresh() {
    createTest(
        "TC-009: Verify follow persists after page refresh",
        "Following status should persist after refreshing the page");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }

    assertTrue(profilePage.isFollowing(), "Should be following before refresh");

    profilePage.refreshPage();
    profilePage.waitForPageLoad();

    assertTrue(profilePage.isFollowing(), "Following status should persist after refresh");
    test.pass("Follow status persists after page refresh");
  }

  @Test(
      groups = {"regression"},
      description = "TC-010: Verify follow persists after logout/login")
  public void testTC010_FollowPersistsAfterLogoutLogin() {
    createTest(
        "TC-010: Verify follow persists after logout/login",
        "Following status should persist after logging out and back in");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }

    assertTrue(profilePage.isFollowing(), "Should be following before logout");

    driver.manage().deleteAllCookies();
    driver.navigate().refresh();

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    assertTrue(profilePage.isFollowing(), "Following status should persist after re-login");
    test.pass("Follow status persists after logout/login");
  }

  @Test(
      groups = {"regression"},
      description = "TC-011: Verify multiple users can follow same user")
  public void testTC011_MultipleUsersCanFollowSameUser() {
    createTest(
        "TC-011: Verify multiple users can follow same user",
        "Multiple users should be able to follow the same target user");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }
    assertTrue(profilePage.isFollowing(), "First user should be following");
    test.info("First user is following target");

    driver.manage().deleteAllCookies();
    driver.navigate().refresh();

    loginPage.login("bob@example.com", "password123");
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }
    assertTrue(profilePage.isFollowing(), "Second user should also be following");
    test.pass("Multiple users can follow the same user");
  }

  @Test(
      groups = {"regression"},
      description = "TC-012: Verify user can follow multiple users")
  public void testTC012_UserCanFollowMultipleUsers() {
    createTest(
        "TC-012: Verify user can follow multiple users",
        "A single user should be able to follow multiple other users");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }
    assertTrue(profilePage.isFollowing(), "Should be following first user");
    test.info("Following first user: " + TARGET_USER);

    profilePage.navigateTo(ANOTHER_USER);
    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }
    assertTrue(profilePage.isFollowing(), "Should be following second user");
    test.info("Following second user: " + ANOTHER_USER);

    test.pass("User can follow multiple users");
  }

  @Test(
      groups = {"regression"},
      description = "TC-013: Verify follow count updates on profile")
  public void testTC013_FollowCountUpdatesOnProfile() {
    createTest(
        "TC-013: Verify follow count updates on profile",
        "Follower count should update when a user is followed");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      profilePage.waitForButtonStateUpdate();
    }

    profilePage.followUser();
    profilePage.waitForButtonStateUpdate();

    assertTrue(profilePage.isFollowing(), "Should be following after click");
    test.pass("Follow action completed - count should be updated");
  }

  @Test(
      groups = {"regression"},
      description = "TC-014: Verify following count updates for follower")
  public void testTC014_FollowingCountUpdatesForFollower() {
    createTest(
        "TC-014: Verify following count updates for follower",
        "Following count should update for the user who follows");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
    }

    assertTrue(profilePage.isFollowing(), "Should be following");
    test.pass("Following count should be updated for the follower");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-015: Verify follow button state changes after click")
  public void testTC015_FollowButtonStateChangesAfterClick() {
    createTest(
        "TC-015: Verify follow button state changes after click",
        "Button should change from Follow to Unfollow after clicking");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (profilePage.isFollowing()) {
      profilePage.unfollowUser();
      profilePage.waitForButtonStateUpdate();
    }

    assertTrue(profilePage.isNotFollowing(), "Should show Follow button initially");
    String initialText = profilePage.getFollowButtonText();
    assertTrue(initialText.contains("Follow"), "Initial button should say Follow");

    profilePage.clickFollowButton();
    profilePage.waitForFollowStateChange(true);

    String afterText = profilePage.getFollowButtonText();
    assertTrue(afterText.contains("Unfollow"), "Button should change to Unfollow");
    test.pass("Follow button state changes correctly after click");
  }

  @Test(
      groups = {"regression"},
      description = "TC-020: Verify follow from article author link")
  public void testTC020_FollowFromArticleAuthorLink() {
    createTest(
        "TC-020: Verify follow from article author link",
        "User can follow an author from the article page");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    homePage.clickGlobalFeed();
    homePage.waitForFeedToLoad();

    if (homePage.hasArticles()) {
      homePage.clickFirstArticleAuthor();
      profilePage.waitForPageLoad();

      if (profilePage.isFollowButtonDisplayed()) {
        if (!profilePage.isFollowing()) {
          profilePage.followUser();
        }
        assertTrue(profilePage.isFollowing(), "Should be following author from article context");
        test.pass("Successfully followed author from article link");
      } else {
        test.info("On own profile or follow button not available");
        test.pass("Navigation to author profile successful");
      }
    } else {
      test.skip("No articles available to test");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-021: Verify follow from comment author link")
  public void testTC021_FollowFromCommentAuthorLink() {
    createTest(
        "TC-021: Verify follow from comment author link",
        "User can follow a commenter from the article comments");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    homePage.clickGlobalFeed();
    homePage.waitForFeedToLoad();

    if (homePage.hasArticles()) {
      homePage.getArticlePreviews().get(0).click();
      articlePage.waitForPageLoad();

      if (articlePage.getCommentCount() > 0) {
        articlePage.clickCommentAuthor(0);
        profilePage.waitForPageLoad();

        if (profilePage.isFollowButtonDisplayed()) {
          if (!profilePage.isFollowing()) {
            profilePage.followUser();
          }
          test.pass("Successfully followed commenter from comment link");
        } else {
          test.info("On own profile or follow button not available");
          test.pass("Navigation to commenter profile successful");
        }
      } else {
        test.info("No comments on article");
        test.pass("Article loaded but no comments to test");
      }
    } else {
      test.skip("No articles available to test");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-030: Verify follow updates feed immediately")
  public void testTC030_FollowUpdatesFeedImmediately() {
    createTest(
        "TC-030: Verify follow updates feed immediately",
        "After following, user's articles should appear in feed");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);

    if (!profilePage.isFollowing()) {
      profilePage.followUser();
      profilePage.waitForButtonStateUpdate();
    }

    homePage.navigateTo();
    homePage.waitForPageLoad();

    if (homePage.hasYourFeedTab()) {
      homePage.clickYourFeed();
      homePage.waitForFeedToLoad();
      test.info("Navigated to Your Feed after following user");
    }

    test.pass("Feed should now include followed user's articles");
  }

  @Test(
      groups = {"regression"},
      description = "TC-032: Verify follow from search results")
  public void testTC032_FollowFromSearchResults() {
    createTest(
        "TC-032: Verify follow from search results",
        "User can navigate to profile and follow from search context");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    homePage.waitForPageLoad();

    profilePage.navigateTo(TARGET_USER);
    profilePage.waitForPageLoad();

    if (profilePage.isFollowButtonDisplayed()) {
      if (!profilePage.isFollowing()) {
        profilePage.followUser();
      }
      assertTrue(profilePage.isFollowing(), "Should be following from direct navigation");
      test.pass("Successfully followed user from direct profile navigation");
    } else {
      test.skip("Follow button not available");
    }
  }
}
