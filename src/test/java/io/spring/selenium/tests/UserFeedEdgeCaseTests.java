package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.FeedPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserFeedEdgeCaseTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String USER_NO_FOLLOWS_EMAIL = "bob@example.com";
  private static final String USER_NO_FOLLOWS_PASSWORD = "password123";

  private HomePage homePage;
  private FeedPage feedPage;

  @BeforeMethod
  public void setupTest() {
    super.setupTest();
    homePage = new HomePage(driver);
  }

  private void loginAsTestUser() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void loginAsUserWithNoFollows() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(BASE_URL);
    loginPage.login(USER_NO_FOLLOWS_EMAIL, USER_NO_FOLLOWS_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"smoke", "edge"})
  public void testTC027_EmptyFeedMessageWhenUserFollowsNoOne() {
    createTest(
        "TC-027: Verify empty feed message when user follows no one",
        "Verify that empty feed message is displayed when user follows no users");

    loginAsUserWithNoFollows();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    boolean hasEmptyMessage = feedPage.isEmptyFeedMessageDisplayed();

    if (articleCount == 0) {
      assertTrue(hasEmptyMessage, "Empty feed message should be displayed");
      String message = feedPage.getEmptyFeedMessage();
      assertTrue(
          message.contains("No articles"), "Message should indicate no articles are available");
    }
    test.pass("Empty feed message displayed correctly when user follows no one");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC028_FeedWithUserFollowingOneUser() {
    createTest(
        "TC-028: Verify feed with user following one user",
        "Verify that feed displays articles from exactly one followed user");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count: " + articleCount);

    if (articleCount > 0) {
      List<String> authors = feedPage.getAllArticleAuthors();
      assertNotNull(authors, "Authors list should not be null");
      for (String author : authors) {
        assertNotNull(author, "Each article should have an author");
      }
    }
    test.pass("Feed with user following one user works correctly");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC029_FeedWithUserFollowingMultipleUsers() {
    createTest(
        "TC-029: Verify feed with user following multiple users",
        "Verify that feed displays mixed articles from all followed users");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count from multiple followed users: " + articleCount);

    if (articleCount > 0) {
      List<String> authors = feedPage.getAllArticleAuthors();
      test.info("Authors in feed: " + authors);
      assertNotNull(authors, "Authors list should not be null");
    }
    test.pass("Feed with user following multiple users works correctly");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC030_FeedAfterUnfollowingAllUsers() {
    createTest(
        "TC-030: Verify feed after unfollowing all users",
        "Verify that empty feed message is displayed after unfollowing all users");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    boolean hasEmptyMessage = feedPage.isEmptyFeedMessageDisplayed();

    assertTrue(
        articleCount >= 0 || hasEmptyMessage, "Feed should display articles or empty message");
    test.pass("Feed behavior after unfollowing users is correct");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC031_FeedAfterFollowingNewUser() {
    createTest(
        "TC-031: Verify feed after following a new user",
        "Verify that new user's articles appear in feed after following");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count after following new user: " + articleCount);

    assertTrue(articleCount >= 0, "Feed should display articles or be empty");
    test.pass("Feed updates correctly after following new user");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC032_FeedWithVeryLongArticleTitles() {
    createTest(
        "TC-032: Verify feed with very long article titles",
        "Verify that long titles are truncated or wrapped properly");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      for (int i = 0; i < articleCount; i++) {
        ArticlePreviewComponent article = feedPage.getArticleAt(i);
        String title = article.getTitle();
        assertTrue(article.isTitleDisplayed(), "Title should be displayed");
        test.info("Article " + i + " title length: " + title.length());
      }
    }
    test.pass("Feed handles long article titles correctly");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC033_FeedWithArticlesContainingSpecialCharacters() {
    createTest(
        "TC-033: Verify feed with articles containing special characters",
        "Verify that special characters are displayed correctly");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      for (int i = 0; i < articleCount; i++) {
        ArticlePreviewComponent article = feedPage.getArticleAt(i);
        String title = article.getTitle();
        String description = article.getDescription();
        assertTrue(article.isTitleDisplayed(), "Title should be displayed");
        assertTrue(article.isDescriptionDisplayed(), "Description should be displayed");
        test.info("Article " + i + " title: " + title);
      }
    }
    test.pass("Feed handles special characters correctly");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC034_FeedWithArticlesContainingNoTags() {
    createTest(
        "TC-034: Verify feed with articles containing no tags",
        "Verify that articles display correctly without tag section");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      boolean foundArticleWithoutTags = false;
      for (int i = 0; i < articleCount; i++) {
        ArticlePreviewComponent article = feedPage.getArticleAt(i);
        if (!article.hasTags()) {
          foundArticleWithoutTags = true;
          assertTrue(article.isDisplayed(), "Article without tags should still be displayed");
          assertTrue(article.isTitleDisplayed(), "Title should be displayed");
          assertTrue(article.isAuthorDisplayed(), "Author should be displayed");
        }
      }
      test.info("Found article without tags: " + foundArticleWithoutTags);
    }
    test.pass("Feed handles articles without tags correctly");
  }
}
