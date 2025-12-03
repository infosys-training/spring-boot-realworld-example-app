package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TokenAuthPositiveTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private SettingsPage settingsPage;
  private ArticlePage articlePage;
  private ProfilePage profilePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    settingsPage = new SettingsPage(driver);
    articlePage = new ArticlePage(driver);
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"smoke", "positive", "token-auth"})
  public void TC001_validTokenGrantsAccessToUserProfile() {
    createTest(
        "TC-001: Valid token grants access to user profile",
        "Verify that a valid JWT token with Token prefix grants access to user profile page");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(
        homePage.isUserLoggedIn(), "User should be logged in after successful authentication");

    homePage.clickProfile();
    wait.until(ExpectedConditions.urlContains("/profile/"));

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(
        profilePage.getUsername().toLowerCase(),
        TEST_USERNAME.toLowerCase(),
        "Username should match logged in user");

    test.pass("Valid token successfully granted access to user profile");
  }

  @Test(groups = {"smoke", "positive", "token-auth"})
  public void TC002_validTokenGrantsAccessToArticlesFeed() {
    createTest(
        "TC-002: Valid token grants access to articles feed",
        "Verify that a valid JWT token grants access to personalized Your Feed");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    assertTrue(
        homePage.isYourFeedDisplayed(), "Your Feed tab should be visible for authenticated users");

    homePage.clickYourFeed();

    test.pass("Valid token successfully granted access to articles feed");
  }

  @Test(groups = {"smoke", "positive", "token-auth"})
  public void TC003_validTokenGrantsAccessToCreateArticle() {
    createTest(
        "TC-003: Valid token grants access to create article",
        "Verify that a valid JWT token grants access to create new article");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    assertTrue(
        homePage.isNewArticleLinkDisplayed(),
        "New Article link should be visible for authenticated users");

    homePage.clickNewArticle();
    wait.until(ExpectedConditions.urlContains("/editor"));

    assertTrue(articlePage.isNewArticlePageDisplayed(), "New article editor should be displayed");

    test.pass("Valid token successfully granted access to create article page");
  }

  @Test(groups = {"positive", "token-auth"})
  public void TC004_validTokenGrantsAccessToUpdateArticle() {
    createTest(
        "TC-004: Valid token grants access to update article",
        "Verify that a valid JWT token grants access to update owned article");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    profilePage.navigateToProfile(BASE_URL, TEST_USERNAME);
    wait.until(ExpectedConditions.urlContains("/profile/"));

    if (profilePage.hasArticles()) {
      homePage.clickFirstArticle();
      wait.until(ExpectedConditions.urlContains("/article/"));

      if (articlePage.isEditButtonDisplayed()) {
        articlePage.clickEdit();
        wait.until(ExpectedConditions.urlContains("/editor/"));
        assertTrue(
            articlePage.isNewArticlePageDisplayed(),
            "Article editor should be displayed for editing");
        test.pass("Valid token successfully granted access to update article");
      } else {
        test.skip("No owned articles available to test update functionality");
      }
    } else {
      test.skip("No articles available to test update functionality");
    }
  }

  @Test(groups = {"positive", "token-auth"})
  public void TC005_validTokenGrantsAccessToDeleteArticle() {
    createTest(
        "TC-005: Valid token grants access to delete article",
        "Verify that a valid JWT token grants access to delete owned article");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    profilePage.navigateToProfile(BASE_URL, TEST_USERNAME);
    wait.until(ExpectedConditions.urlContains("/profile/"));

    if (profilePage.hasArticles()) {
      homePage.clickFirstArticle();
      wait.until(ExpectedConditions.urlContains("/article/"));

      assertTrue(
          articlePage.isDeleteButtonDisplayed(),
          "Delete button should be visible for owned articles");
      test.pass("Valid token successfully granted access to delete article functionality");
    } else {
      test.skip("No articles available to test delete functionality");
    }
  }

  @Test(groups = {"positive", "token-auth"})
  public void TC006_validTokenGrantsAccessToFollowUser() {
    createTest(
        "TC-006: Valid token grants access to follow user",
        "Verify that a valid JWT token grants access to follow another user");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    profilePage.navigateToProfile(BASE_URL, "janedoe");
    wait.until(ExpectedConditions.urlContains("/profile/"));

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertTrue(
        profilePage.isFollowButtonDisplayed(),
        "Follow button should be visible for other users' profiles");

    test.pass("Valid token successfully granted access to follow user functionality");
  }

  @Test(groups = {"positive", "token-auth"})
  public void TC007_validTokenGrantsAccessToFavoriteArticle() {
    createTest(
        "TC-007: Valid token grants access to favorite article",
        "Verify that a valid JWT token grants access to favorite an article");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    homePage.clickGlobalFeed();

    if (homePage.getArticleCount() > 0) {
      homePage.clickFirstArticle();
      wait.until(ExpectedConditions.urlContains("/article/"));

      assertTrue(
          articlePage.isFavoriteButtonDisplayed(),
          "Favorite button should be visible for authenticated users");
      test.pass("Valid token successfully granted access to favorite article functionality");
    } else {
      test.skip("No articles available to test favorite functionality");
    }
  }

  @Test(groups = {"positive", "token-auth"})
  public void TC008_validTokenGrantsAccessToCreateComment() {
    createTest(
        "TC-008: Valid token grants access to create comment",
        "Verify that a valid JWT token grants access to create a comment on an article");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    homePage.clickGlobalFeed();

    if (homePage.getArticleCount() > 0) {
      homePage.clickFirstArticle();
      wait.until(ExpectedConditions.urlContains("/article/"));

      assertTrue(
          articlePage.isCommentSectionDisplayed(),
          "Comment section should be visible for authenticated users");
      test.pass("Valid token successfully granted access to create comment functionality");
    } else {
      test.skip("No articles available to test comment functionality");
    }
  }

  @Test(groups = {"smoke", "positive", "token-auth"})
  public void TC009_validTokenGrantsAccessToUpdateUserProfile() {
    createTest(
        "TC-009: Valid token grants access to update user profile",
        "Verify that a valid JWT token grants access to update user profile settings");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    assertTrue(
        homePage.isSettingsLinkDisplayed(),
        "Settings link should be visible for authenticated users");

    homePage.clickSettings();
    wait.until(ExpectedConditions.urlContains("/settings"));

    assertTrue(settingsPage.isSettingsPageDisplayed(), "Settings page should be displayed");
    assertEquals(
        settingsPage.getCurrentEmail(), TEST_EMAIL, "Email should match logged in user's email");

    test.pass("Valid token successfully granted access to update user profile");
  }

  @Test(groups = {"smoke", "positive", "token-auth"})
  public void TC010_validTokenGrantsAccessToGetCurrentUser() {
    createTest(
        "TC-010: Valid token grants access to get current user",
        "Verify that a valid JWT token allows current user info to be displayed in navbar");

    loginPage.navigateToLoginPage(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    WebDriverWait wait = new WebDriverWait(driver, 10);
    wait.until(ExpectedConditions.urlContains("/"));

    assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

    String loggedInUsername = homePage.getLoggedInUsername();
    assertNotNull(loggedInUsername, "Username should be displayed in navbar");
    assertEquals(
        loggedInUsername.toLowerCase(),
        TEST_USERNAME.toLowerCase(),
        "Displayed username should match logged in user");

    test.pass("Valid token successfully granted access to current user information");
  }
}
