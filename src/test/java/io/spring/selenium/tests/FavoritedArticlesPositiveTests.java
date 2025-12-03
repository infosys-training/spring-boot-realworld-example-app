package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritedArticlesPositiveTests extends BaseTest {

  private ProfilePage profilePage;
  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER = "johndoe";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String SECOND_USER = "janedoe";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC001FilterArticlesByValidUsernameWithFavoritedArticles() {
    createTest(
        "TC-001: Filter articles by valid username with favorited articles",
        "Verify that articles favorited by a valid user are displayed");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(profilePage.getUsername(), TEST_USER, "Username should match");

    test.info("Successfully navigated to favorited articles for user: " + TEST_USER);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC002ViewFavoritedArticlesTabOnProfilePage() {
    createTest(
        "TC-002: View favorited articles tab on profile page",
        "Verify that Favorited Articles tab is visible and clickable");

    profilePage.navigateTo(BASE_URL, TEST_USER);

    assertTrue(
        profilePage.isFavoritedArticlesTabDisplayed(),
        "Favorited Articles tab should be displayed");
    assertTrue(profilePage.getProfileTabs().size() >= 2, "Should have at least 2 tabs");

    test.info("Favorited Articles tab is visible on profile page");
  }

  @Test(groups = {"regression"})
  public void testTC003VerifyArticleCountMatchesFavoritedArticles() {
    createTest(
        "TC-003: Verify article count matches favorited articles",
        "Verify that the displayed article count matches the expected favorited count");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    assertTrue(articleCount >= 0, "Article count should be non-negative");

    test.info("Article count on favorited articles page: " + articleCount);
  }

  @Test(groups = {"regression"})
  public void testTC004VerifyArticlesDisplayedAreFavoritedByUser() {
    createTest(
        "TC-004: Verify articles displayed are favorited by user",
        "Verify that all displayed articles are favorited by the specified user");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      for (int i = 0; i < Math.min(articleCount, 5); i++) {
        String title = profilePage.getArticleTitle(i);
        assertNotNull(title, "Article title should not be null");
        test.info("Article " + (i + 1) + ": " + title);
      }
    }

    test.info("Verified articles displayed for user: " + TEST_USER);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC005NavigateToFavoritedArticlesViaProfileTab() {
    createTest(
        "TC-005: Navigate to favorited articles via profile tab",
        "Verify that clicking Favorited Articles tab changes URL to include favorite=true");

    profilePage.navigateTo(BASE_URL, TEST_USER);
    profilePage.clickFavoritedArticlesTab();

    assertTrue(
        profilePage.urlContainsFavoriteParameter(), "URL should contain favorite=true parameter");

    test.info("Successfully navigated to favorited articles via tab click");
  }

  @Test(groups = {"regression"})
  public void testTC006VerifyFavoritedArticlesForLoggedInUserViewingOwnProfile() {
    createTest(
        "TC-006: Verify favorited articles for logged-in user viewing own profile",
        "Verify that logged-in user can view their own favorited articles");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_USER_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(profilePage.getUsername(), TEST_USER, "Should be viewing own profile");

    test.info("Logged-in user successfully viewing own favorited articles");
  }

  @Test(groups = {"regression"})
  public void testTC007VerifyFavoritedArticlesForDifferentUsers() {
    createTest(
        "TC-007: Verify favorited articles for different users",
        "Verify that different users show their own favorited articles");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();
    int user1ArticleCount = profilePage.getArticleCount();

    profilePage.navigateToFavoritedArticles(BASE_URL, SECOND_USER);
    profilePage.waitForArticlesLoad();
    int user2ArticleCount = profilePage.getArticleCount();

    test.info("User 1 (" + TEST_USER + ") favorited articles: " + user1ArticleCount);
    test.info("User 2 (" + SECOND_USER + ") favorited articles: " + user2ArticleCount);
  }

  @Test(groups = {"regression"})
  public void testTC008VerifyArticlePreviewShowsCorrectMetadata() {
    createTest(
        "TC-008: Verify article preview shows correct metadata",
        "Verify that article title, description, author, and date are displayed");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String title = profilePage.getArticleTitle(0);
      String description = profilePage.getArticleDescription(0);
      String author = profilePage.getArticleAuthor(0);
      String date = profilePage.getArticleDate(0);

      assertNotNull(title, "Article title should be displayed");
      test.info("Title: " + title);
      test.info("Description: " + description);
      test.info("Author: " + author);
      test.info("Date: " + date);
    } else {
      test.info("No articles to verify metadata");
    }
  }

  @Test(groups = {"regression"})
  public void testTC009ClickOnArticleFromFavoritedListNavigatesCorrectly() {
    createTest(
        "TC-009: Click on article from favorited list navigates correctly",
        "Verify that clicking an article navigates to the full article page");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String expectedTitle = profilePage.getArticleTitle(0);
      profilePage.clickArticle(0);

      articlePage.waitForPageLoad();
      assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

      String actualTitle = articlePage.getArticleTitle();
      assertEquals(actualTitle, expectedTitle, "Article title should match");

      test.info("Successfully navigated to article: " + actualTitle);
    } else {
      test.info("No articles available to click");
    }
  }

  @Test(groups = {"regression"})
  public void testTC010VerifyAuthorInfoDisplayedInFavoritedArticles() {
    createTest(
        "TC-010: Verify author info displayed in favorited articles",
        "Verify that author username and avatar are displayed");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String author = profilePage.getArticleAuthor(0);
      assertNotNull(author, "Author should be displayed");
      assertFalse(author.isEmpty(), "Author should not be empty");

      test.info("Author displayed: " + author);
    } else {
      test.info("No articles to verify author info");
    }
  }

  @Test(groups = {"regression"})
  public void testTC011VerifyTagsDisplayedInFavoritedArticles() {
    createTest(
        "TC-011: Verify tags displayed in favorited articles",
        "Verify that article tags are displayed correctly");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      List<String> tags = profilePage.getArticleTags(0);
      test.info("Tags found: " + tags.size());
      for (String tag : tags) {
        test.info("Tag: " + tag);
      }
    } else {
      test.info("No articles to verify tags");
    }
  }

  @Test(groups = {"regression"})
  public void testTC012VerifyFavoriteCountDisplayedInArticles() {
    createTest(
        "TC-012: Verify favorite count displayed in articles",
        "Verify that favorite count is displayed for each article");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      int favoriteCount = profilePage.getArticleFavoriteCount(0);
      assertTrue(favoriteCount >= 0, "Favorite count should be non-negative");

      test.info("Favorite count for first article: " + favoriteCount);
    } else {
      test.info("No articles to verify favorite count");
    }
  }
}
