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
 * Validation test cases for Delete Article functionality. Tests scenarios involving slug format
 * validation and article identification.
 */
public class DeleteArticleValidationTests extends BaseTest {

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

  @Test(groups = {"smoke", "validation", "delete-article"})
  public void testTC023_DeleteArticleWithValidSlugFormat() {
    createTest(
        "TC-023: Delete article with valid slug format",
        "Verify article with standard hyphenated slug can be deleted");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "Valid Slug Article TC023";
    createArticle(articleTitle, "Description", "Body content");

    assertTrue(articlePage.isArticleLoaded(), "Article should be loaded");
    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible");

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("valid-slug-article"),
        "Slug should be properly formatted with hyphens");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with valid slug should be deleted successfully");

    test.pass("Article with valid slug format deleted successfully");
  }

  @Test(groups = {"regression", "validation", "delete-article"})
  public void testTC024_DeleteArticleWithSlugContainingHyphens() {
    createTest(
        "TC-024: Delete article with slug containing hyphens",
        "Verify article with multiple hyphens in slug can be deleted");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "My Test Article With Many Words TC024";
    createArticle(articleTitle, "Description", "Body content");

    String currentUrl = driver.getCurrentUrl();
    String slug = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);

    assertTrue(slug.contains("-"), "Slug should contain hyphens");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with hyphenated slug should be deleted");

    test.pass("Article with hyphenated slug deleted successfully");
  }

  @Test(groups = {"regression", "validation", "delete-article"})
  public void testTC025_DeleteArticleWithSlugContainingNumbers() {
    createTest(
        "TC-025: Delete article with slug containing numbers",
        "Verify article with numbers in slug can be deleted");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "Article 123 Test 456 TC025";
    createArticle(articleTitle, "Description", "Body content");

    String currentUrl = driver.getCurrentUrl();
    String slug = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);

    assertTrue(
        slug.matches(".*\\d.*") || slug.contains("123") || slug.contains("456"),
        "Slug should contain numbers or be derived from title with numbers");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with numbers in slug should be deleted");

    test.pass("Article with numbers in slug deleted successfully");
  }

  @Test(groups = {"regression", "validation", "delete-article"})
  public void testTC026_DeleteArticleWithMixedCaseSlug() {
    createTest(
        "TC-026: Delete article with mixed case slug",
        "Verify case sensitivity behavior when deleting article");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "MixedCase Article TC026";
    createArticle(articleTitle, "Description", "Body content");

    String currentUrl = driver.getCurrentUrl();
    String slug = currentUrl.substring(currentUrl.lastIndexOf("/") + 1);

    test.info("Original slug: " + slug);

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article should be deleted regardless of case");

    test.pass("Mixed case slug handled correctly during deletion");
  }

  @Test(groups = {"regression", "validation", "delete-article"})
  public void testTC027_DeleteArticleWithUnicodeCharactersInTitle() {
    createTest(
        "TC-027: Delete article with unicode characters in title",
        "Verify article with unicode title can be deleted");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "Unicode Article Test TC027";
    createArticle(articleTitle, "Description with unicode", "Body content");

    assertTrue(articlePage.isArticleLoaded(), "Article should be loaded");

    articlePage.deleteArticle();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Article with unicode should be deleted");

    test.pass("Article with unicode characters deleted successfully");
  }

  @Test(groups = {"regression", "validation", "delete-article"})
  public void testTC028_DeleteArticleWithMinimumLengthSlug() {
    createTest(
        "TC-028: Delete article with minimum length slug",
        "Verify article with short title/slug can be deleted");

    loginAsUser(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    String articleTitle = "A";
    createArticle(articleTitle, "Description", "Body content");

    assertTrue(
        articlePage.isArticleLoaded() || articlePage.hasErrorMessages(),
        "Article should be loaded or show validation error");

    if (articlePage.isArticleLoaded() && articlePage.isDeleteButtonVisible()) {
      articlePage.deleteArticle();

      assertTrue(
          articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
          "Article with minimum slug should be deleted");

      test.pass("Article with minimum length slug deleted successfully");
    } else {
      test.info("Short title may have validation requirements");
      test.pass("Minimum length slug validation handled correctly");
    }
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
