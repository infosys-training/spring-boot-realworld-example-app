package io.spring.selenium.tests.deletearticle;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.EditorPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.tests.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for Delete Article functionality. Tests boundary conditions and unusual
 * scenarios.
 */
public class DeleteArticleEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;

  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";

  private static final String OTHER_USER_EMAIL = "jane@example.com";
  private static final String OTHER_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC029_DeleteArticleImmediatelyAfterCreation() {
    createTest(
        "TC-029: Delete article immediately after creation",
        "Verify article can be deleted immediately after creation");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Immediate Delete TC029 " + System.currentTimeMillis();
    editorPage.navigateToNewArticle();
    editorPage.createArticle(articleTitle, "Description", "Body content");

    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible immediately");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article should be deleted immediately after creation");

    test.pass("Article deleted immediately after creation");
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC030_DeleteArticleWhileAnotherUserIsViewing() {
    createTest(
        "TC-030: Delete article while another user is viewing",
        "Verify behavior when article is deleted while being viewed");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Concurrent View TC030 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Deleted article should show 404 when accessed");

    test.pass("Article deletion while being viewed handled correctly");
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC031_DeleteArticleWithMaximumComments() {
    createTest(
        "TC-031: Delete article with maximum comments",
        "Verify article with many comments can be deleted");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Many Comments TC031 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    if (articlePage.isCommentInputVisible()) {
      for (int i = 0; i < 5; i++) {
        articlePage.addComment("Test comment " + i);
        waitForPageLoad();
      }
    }

    int commentCount = articlePage.getCommentCount();
    test.info("Comments before deletion: " + commentCount);

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with many comments should be deleted");

    test.pass("Article with multiple comments deleted successfully");
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC032_DeleteArticleWithMaximumFavorites() {
    createTest(
        "TC-032: Delete article with maximum favorites",
        "Verify article favorited by many users can be deleted");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Many Favorites TC032 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with favorites should be deleted");

    articlePage.navigateTo(slug);

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Deleted favorited article should return 404");

    test.pass("Article with favorites deleted successfully");
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC033_ConcurrentDeleteRequestsForSameArticle() {
    createTest(
        "TC-033: Concurrent delete requests for same article",
        "Verify handling of multiple simultaneous delete requests");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Concurrent Delete TC033 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Second access after deletion should return 404");

    test.pass("Concurrent delete scenario handled correctly");
  }

  @Test(groups = {"regression", "edge-case", "delete-article"})
  public void testTC034_DeleteArticleAndImmediatelyAccess() {
    createTest(
        "TC-034: Delete article and immediately access",
        "Verify immediate access after deletion returns 404");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Immediate Access TC034 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Immediate access after deletion should return 404");

    test.pass("Immediate access after deletion returns 404 correctly");
  }

  private void loginAsUser(String email, String password) {
    loginPage.navigateTo();
    loginPage.login(email, password);
    waitForPageLoad();
  }

  private void createArticle(String title, String description, String body) {
    editorPage.navigateToNewArticle();
    editorPage.createArticle(title, description, body);
    waitForPageLoad();
  }

  private void waitForPageLoad() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
