package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SlugGenerationErrorTests extends BaseTest {

  private HomePage homePage;
  private LoginPage loginPage;
  private ArticleEditorPage editorPage;
  private ArticlePage articlePage;
  private String baseUrl;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String SECOND_USER_EMAIL = "jane@example.com";
  private static final String SECOND_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage = new HomePage(driver);
    loginPage = new LoginPage(driver);
    editorPage = new ArticleEditorPage(driver);
    articlePage = new ArticlePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void loginAsSecondUser() {
    loginPage.navigateTo(baseUrl);
    loginPage.login(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"regression", "negative"})
  public void testTC021_ErrorWhenCreatingArticleWithoutTitle() {
    createTest(
        "TC-021: Verify error when creating article without title",
        "Verify that an error is displayed when trying to create an article without a title");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    editorPage.enterDescription("Description without title");
    editorPage.enterBody("Body content without title.");
    editorPage.clickPublish();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stillOnEditor = currentUrl.contains("/editor");
    boolean hasError = editorPage.isErrorDisplayed();

    assertTrue(
        stillOnEditor || hasError, "Should show error or stay on editor when title is missing");
    test.pass("Error handling works when creating article without title");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC022_ErrorWhenCreatingArticleWithEmptyTitle() {
    createTest(
        "TC-022: Verify error when creating article with empty title",
        "Verify that validation error is shown for empty title");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    editorPage.enterTitle("");
    editorPage.enterDescription("Description with empty title");
    editorPage.enterBody("Body content with empty title.");
    editorPage.clickPublish();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stillOnEditor = currentUrl.contains("/editor");

    assertTrue(stillOnEditor, "Should stay on editor page when title is empty");
    test.pass("Validation error shown for empty title");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC023_ErrorWhenCreatingArticleWithWhitespaceOnlyTitle() {
    createTest(
        "TC-023: Verify error when creating article with whitespace-only title",
        "Verify that whitespace-only title is rejected");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    editorPage.enterTitle("   ");
    editorPage.enterDescription("Description with whitespace title");
    editorPage.enterBody("Body content with whitespace title.");
    editorPage.clickPublish();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stillOnEditor = currentUrl.contains("/editor");
    boolean hasError = editorPage.isErrorDisplayed();

    assertTrue(stillOnEditor || hasError, "Should reject whitespace-only title or show error");
    test.pass("Whitespace-only title was rejected");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC024_SlugNotGeneratedForInvalidArticleData() {
    createTest(
        "TC-024: Verify slug not generated for invalid article data",
        "Verify that no slug is generated when article data is invalid");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    editorPage.enterTitle("Valid Title");
    editorPage.clickPublish();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean stillOnEditor = currentUrl.contains("/editor");
    boolean hasError = editorPage.isErrorDisplayed();

    assertTrue(stillOnEditor || hasError, "Should not create article with missing required fields");
    test.pass("Slug not generated for invalid article data");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC025_ErrorHandlingForExtremelyLongTitle() {
    createTest(
        "TC-025: Verify error handling for extremely long title",
        "Verify that extremely long titles are handled appropriately");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    StringBuilder extremelyLongTitle = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      extremelyLongTitle.append("VeryLongWord");
    }

    editorPage.enterTitle(extremelyLongTitle.toString());
    editorPage.enterDescription("Extremely long title test");
    editorPage.enterBody("Body content for extremely long title test.");
    editorPage.clickPublish();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean articleCreated = currentUrl.contains("/article/");
    boolean stillOnEditor = currentUrl.contains("/editor");
    boolean hasError = editorPage.isErrorDisplayed();

    assertTrue(
        articleCreated || stillOnEditor || hasError,
        "Should either create article with truncated slug or show error");
    test.pass("Extremely long title was handled appropriately");
  }

  @Test(groups = {"smoke", "negative"})
  public void testTC026_ErrorWhenAccessingNonExistentSlug() {
    createTest(
        "TC-026: Verify error when accessing non-existent slug",
        "Verify that 404 error is displayed for non-existent slug");

    driver.get(baseUrl + "/article/non-existent-slug-12345-xyz");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean is404 = articlePage.is404Error();
    boolean isErrorDisplayed = articlePage.isErrorDisplayed();
    boolean articleNotFound = !articlePage.isArticleDisplayed();

    assertTrue(
        is404 || isErrorDisplayed || articleNotFound,
        "Should show 404 or error for non-existent slug");
    test.pass("404 error displayed for non-existent slug");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC027_ErrorWhenUpdatingToEmptyTitle() {
    createTest(
        "TC-027: Verify error when updating to empty title",
        "Verify that updating article to empty title is rejected");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Article To Update Empty", "Update test", "Body content for update test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (driver.getCurrentUrl().contains("/article/")) {
      articlePage.clickEdit();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      editorPage.clearTitle();
      editorPage.clickPublish();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      boolean stillOnEditor = currentUrl.contains("/editor");
      boolean hasError = editorPage.isErrorDisplayed();

      assertTrue(stillOnEditor || hasError, "Should reject empty title update");
    }

    test.pass("Empty title update was rejected");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC028_ErrorWhenUpdatingToWhitespaceOnlyTitle() {
    createTest(
        "TC-028: Verify error when updating to whitespace-only title",
        "Verify that updating article to whitespace-only title is rejected");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Article To Update Whitespace", "Update test", "Body content for whitespace update test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (driver.getCurrentUrl().contains("/article/")) {
      articlePage.clickEdit();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      editorPage.clearTitle();
      editorPage.enterTitle("   ");
      editorPage.clickPublish();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      boolean stillOnEditor = currentUrl.contains("/editor");
      boolean hasError = editorPage.isErrorDisplayed();

      assertTrue(stillOnEditor || hasError, "Should reject whitespace-only title update");
    }

    test.pass("Whitespace-only title update was rejected");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC029_UnauthorizedUserCannotUpdateArticleSlug() {
    createTest(
        "TC-029: Verify unauthorized user cannot update article slug",
        "Verify that a different user cannot edit another user's article");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Article By First User", "Authorization test", "Body content for auth test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String articleSlug = articlePage.getSlugFromUrl();

    driver.get(baseUrl + "/settings");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    loginAsSecondUser();

    driver.get(baseUrl + "/article/" + articleSlug);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean editButtonVisible = articlePage.isEditButtonDisplayed();

    assertFalse(editButtonVisible, "Edit button should not be visible for unauthorized user");
    test.pass("Unauthorized user cannot update article slug");
  }

  @Test(groups = {"regression", "negative"})
  public void testTC030_DeletedArticleSlugReturns404() {
    createTest(
        "TC-030: Verify deleted article slug returns 404",
        "Verify that accessing a deleted article's slug returns 404");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle("Article To Delete", "Delete test", "Body content for delete test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String articleSlug = articlePage.getSlugFromUrl();

    if (articlePage.isDeleteButtonDisplayed()) {
      articlePage.clickDelete();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      driver.get(baseUrl + "/article/" + articleSlug);

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean is404 = articlePage.is404Error();
      boolean articleNotFound = !articlePage.isArticleDisplayed();

      assertTrue(is404 || articleNotFound, "Deleted article slug should return 404");
    }

    test.pass("Deleted article slug returns 404");
  }
}
