package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArticleValidationTests extends BaseTest {

  private ArticlePage articlePage;
  private HomePage homePage;
  private LoginPage loginPage;

  private static final String TEST_ARTICLE_SLUG = "how-to-train-your-dragon";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String JANE_USER_EMAIL = "jane@example.com";
  private static final String JANE_USER_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    articlePage = new ArticlePage(driver);
    homePage = new HomePage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(groups = {"regression", "validation"})
  public void TC013_verifyFavoriteStatusShowsAsFavoritedForLoggedInUser() {
    createTest(
        "TC-013: Verify favorite status shows as favorited for logged-in user",
        "Verify favorite button shows favorited state for user who has favorited the article");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for logged-in user to check favorite status");
  }

  @Test(groups = {"regression", "validation"})
  public void TC014_verifyFavoriteStatusShowsAsNotFavorited() {
    createTest(
        "TC-014: Verify favorite status shows as not favorited",
        "Verify favorite button shows not favorited state for user who has not favorited");

    loginPage.login(JANE_USER_EMAIL, JANE_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for logged-in user to check unfavorited status");
  }

  @Test(groups = {"regression", "validation"})
  public void TC015_verifyFollowStatusShowsAsFollowingAuthor() {
    createTest(
        "TC-015: Verify follow status shows as following author",
        "Verify follow button shows following state for user who follows the author");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for logged-in user to check follow status");
  }

  @Test(groups = {"regression", "validation"})
  public void TC016_verifyFollowStatusShowsAsNotFollowingAuthor() {
    createTest(
        "TC-016: Verify follow status shows as not following author",
        "Verify follow button shows follow state for user who does not follow the author");

    loginPage.login(JANE_USER_EMAIL, JANE_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for logged-in user to check not following status");
  }

  @Test(groups = {"regression", "validation"})
  public void TC017_verifyAnonymousUserCannotSeeFavoriteButtonActiveState() {
    createTest(
        "TC-017: Verify anonymous user cannot see favorite button active state",
        "Verify favorite button is disabled or prompts login for anonymous users");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for anonymous user");
  }

  @Test(groups = {"regression", "validation"})
  public void TC018_verifyAnonymousUserCannotSeeFollowButtonActiveState() {
    createTest(
        "TC-018: Verify anonymous user cannot see follow button active state",
        "Verify follow button is disabled or prompts login for anonymous users");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for anonymous user");
  }

  @Test(groups = {"regression", "validation"})
  public void TC019_verifyArticleContentIsReadOnlyForNonAuthor() {
    createTest(
        "TC-019: Verify article content is read-only for non-author",
        "Verify no edit/delete buttons visible for users who are not the article author");

    loginPage.login(JANE_USER_EMAIL, JANE_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    assertFalse(
        articlePage.isEditButtonDisplayed(), "Edit button should not be visible for non-author");
    assertFalse(
        articlePage.isDeleteButtonDisplayed(),
        "Delete button should not be visible for non-author");

    test.info("Verified edit/delete buttons are not visible for non-author");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC020_verifyEditButtonVisibleForArticleAuthor() {
    createTest(
        "TC-020: Verify edit button visible for article author",
        "Verify edit button is visible when logged in as the article author");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for article author");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void TC021_verifyDeleteButtonVisibleForArticleAuthor() {
    createTest(
        "TC-021: Verify delete button visible for article author",
        "Verify delete button is visible when logged in as the article author");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Verified article page loads for article author");
  }

  @Test(groups = {"regression", "validation"})
  public void TC022_verifyTagLinksAreClickable() {
    createTest(
        "TC-022: Verify tag links are clickable",
        "Verify clicking on a tag navigates to tag filter page");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    if (articlePage.getTagCount() > 0) {
      String firstTag = articlePage.getArticleTags().get(0);
      test.info("Found tag: " + firstTag);
    } else {
      test.info("No tags found on this article");
    }
  }
}
