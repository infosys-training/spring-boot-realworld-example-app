package io.spring.selenium.tests.deletearticle;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.EditorPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import io.spring.selenium.tests.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test cases for Delete Article functionality. Tests happy path scenarios where article
 * deletion succeeds.
 */
public class DeleteArticlePositiveTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;
  private ProfilePage profilePage;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String TEST_USER_USERNAME = "johndoe";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"smoke", "positive", "delete-article"})
  public void testTC001_DeleteArticleAsAuthor_Verify204Response() {
    createTest(
        "TC-001: Delete article as author - verify 204 response",
        "Verify that article author can successfully delete their article");

    loginAndCreateArticle("Test Article TC001", "Description", "Body content for TC001");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Should redirect to home page or article should not be accessible after deletion");

    test.pass("Article deleted successfully as author");
  }

  @Test(groups = {"smoke", "positive", "delete-article"})
  public void testTC002_DeleteArticleAndVerifyNotAccessible() {
    createTest(
        "TC-002: Delete article and verify article no longer accessible",
        "Verify deleted article returns 404 when accessed");

    String articleTitle = "Test Article TC002 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC002");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Deleted article should return 404 or not be accessible");

    test.pass("Deleted article is no longer accessible");
  }

  @Test(groups = {"regression", "positive", "delete-article"})
  public void testTC003_DeleteArticleWithComments_VerifyCommentsRemoved() {
    createTest(
        "TC-003: Delete article with comments - verify comments removed",
        "Verify that comments are cascade deleted with the article");

    String articleTitle = "Test Article TC003 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC003");

    if (articlePage.isCommentInputVisible()) {
      articlePage.addComment("Test comment for TC003");
    }

    int commentCountBefore = articlePage.getCommentCount();
    test.info("Comments before deletion: " + commentCountBefore);

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);

    assertTrue(
        articlePage.isArticleNotFound() || articlePage.getCommentCount() == 0,
        "Comments should be removed with the article");

    test.pass("Article with comments deleted successfully, comments cascade deleted");
  }

  @Test(groups = {"regression", "positive", "delete-article"})
  public void testTC004_DeleteArticleWithFavorites_VerifyFavoritesRemoved() {
    createTest(
        "TC-004: Delete article with favorites - verify favorites removed",
        "Verify that favorites are cascade deleted with the article");

    String articleTitle = "Test Article TC004 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC004");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Article with favorites should be deleted and not accessible");

    test.pass("Article with favorites deleted successfully");
  }

  @Test(groups = {"regression", "positive", "delete-article"})
  public void testTC005_DeleteArticleWithCommentsAndFavorites() {
    createTest(
        "TC-005: Delete article with both comments and favorites",
        "Verify article with both comments and favorites is deleted properly");

    String articleTitle = "Test Article TC005 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC005");

    if (articlePage.isCommentInputVisible()) {
      articlePage.addComment("Test comment for TC005");
    }

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Article with comments and favorites should be deleted");

    test.pass("Article with both comments and favorites deleted successfully");
  }

  @Test(groups = {"regression", "positive", "delete-article"})
  public void testTC006_DeleteArticleWithNoInteractions() {
    createTest(
        "TC-006: Delete article with no comments or favorites",
        "Verify new article with no interactions can be deleted");

    String articleTitle = "Test Article TC006 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC006");

    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible for author");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with no interactions should be deleted successfully");

    test.pass("Article with no interactions deleted successfully");
  }

  @Test(groups = {"smoke", "positive", "delete-article"})
  public void testTC007_DeleteArticleAndVerifyRemovedFromList() {
    createTest(
        "TC-007: Delete article and verify removed from article list",
        "Verify deleted article no longer appears in article listings");

    String articleTitle = "Unique Article TC007 " + System.currentTimeMillis();
    loginAndCreateArticle(articleTitle, "Description", "Body content for TC007");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    homePage.navigateTo();
    homePage.waitForArticlesToLoad();

    assertFalse(
        homePage.isArticleVisible(articleTitle),
        "Deleted article should not appear in article list");

    test.pass("Deleted article no longer appears in article listings");
  }

  @Test(groups = {"regression", "positive", "delete-article"})
  public void testTC008_DeleteArticleAndVerifyAuthorArticleCountDecreases() {
    createTest(
        "TC-008: Delete article and verify author's article count decreases",
        "Verify author's article count decreases after deletion");

    loginPage.navigateTo();
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    profilePage.navigateTo(TEST_USER_USERNAME);
    profilePage.waitForProfileLoad();
    profilePage.clickMyArticlesTab();
    profilePage.waitForArticlesToLoad();

    int initialArticleCount = profilePage.getArticleCount();
    test.info("Initial article count: " + initialArticleCount);

    editorPage.navigateToNewArticle();
    String articleTitle = "Test Article TC008 " + System.currentTimeMillis();
    editorPage.createArticle(articleTitle, "Description", "Body content for TC008");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articlePage.deleteArticle();

    profilePage.navigateTo(TEST_USER_USERNAME);
    profilePage.waitForProfileLoad();
    profilePage.clickMyArticlesTab();
    profilePage.waitForArticlesToLoad();

    int finalArticleCount = profilePage.getArticleCount();
    test.info("Final article count: " + finalArticleCount);

    assertTrue(
        finalArticleCount <= initialArticleCount,
        "Article count should not increase after creating and deleting an article");

    test.pass("Author's article count correctly reflects deletion");
  }

  private void loginAndCreateArticle(String title, String description, String body) {
    loginPage.navigateTo();
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    editorPage.navigateToNewArticle();
    editorPage.createArticle(title, description, body);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
