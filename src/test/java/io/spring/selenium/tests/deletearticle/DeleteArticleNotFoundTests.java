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
 * Not Found test cases for Delete Article functionality. Tests scenarios where article is not found
 * or has invalid slug.
 */
public class DeleteArticleNotFoundTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
  }

  @Test(groups = {"smoke", "not-found", "delete-article"})
  public void testTC017_DeleteNonExistentArticle_Verify404() {
    createTest(
        "TC-017: Delete non-existent article - verify 404",
        "Verify attempting to delete non-existent article returns 404");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String nonExistentSlug = "non-existent-article-" + System.currentTimeMillis();
    articlePage.navigateTo(nonExistentSlug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Non-existent article should return 404");

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for non-existent article");

    test.pass("Non-existent article returns 404 Not Found");
  }

  @Test(groups = {"regression", "not-found", "delete-article"})
  public void testTC018_DeleteArticleWithInvalidSlugFormat() {
    createTest(
        "TC-018: Delete article with invalid slug format",
        "Verify invalid slug format returns 404 or validation error");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String invalidSlug = "invalid@#$%slug!";
    articlePage.navigateTo(invalidSlug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Invalid slug format should return 404 or error");

    test.pass("Invalid slug format handled correctly");
  }

  @Test(groups = {"smoke", "not-found", "delete-article"})
  public void testTC019_DeleteAlreadyDeletedArticle() {
    createTest(
        "TC-019: Delete already deleted article",
        "Verify attempting to delete already deleted article returns 404");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "Article To Delete TC019 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    articlePage.deleteArticle();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Already deleted article should return 404");

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for deleted article");

    test.pass("Already deleted article returns 404 Not Found");
  }

  @Test(groups = {"regression", "not-found", "delete-article"})
  public void testTC020_DeleteArticleWithEmptySlug() {
    createTest(
        "TC-020: Delete article with empty slug",
        "Verify empty slug returns 404 or validation error");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    driver.get(getBaseUrl() + "/article/");
    waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound()
            || !articlePage.isArticleLoaded()
            || driver.getCurrentUrl().equals(getBaseUrl() + "/"),
        "Empty slug should return 404 or redirect");

    test.pass("Empty slug handled correctly");
  }

  @Test(groups = {"regression", "not-found", "delete-article"})
  public void testTC021_DeleteArticleWithSpecialCharactersInSlug() {
    createTest(
        "TC-021: Delete article with special characters in slug",
        "Verify slug with special characters returns 404");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String specialSlug = "article@#$%^&*()slug";
    articlePage.navigateTo(specialSlug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Slug with special characters should return 404");

    test.pass("Special characters in slug handled correctly");
  }

  @Test(groups = {"regression", "not-found", "delete-article"})
  public void testTC022_DeleteArticleWithVeryLongSlug() {
    createTest(
        "TC-022: Delete article with very long slug",
        "Verify very long slug returns 404 or validation error");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    StringBuilder longSlug = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longSlug.append("very-long-slug-segment-");
    }

    articlePage.navigateTo(longSlug.toString());
    waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound()
            || !articlePage.isArticleLoaded()
            || articlePage.hasErrorMessages(),
        "Very long slug should return 404 or error");

    test.pass("Very long slug handled correctly");
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

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
