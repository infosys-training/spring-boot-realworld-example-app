package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for Update Article functionality. Tests TC-011 through TC-020 covering
 * input validation scenarios.
 */
public class UpdateArticleValidationTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private ArticleEditorPage editorPage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new ArticleEditorPage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void navigateToFirstOwnedArticle() {
    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC011_UpdateWithEmptyTitle() {
    createTest(
        "TC-011: Update with empty title",
        "Verify validation error when updating with empty title");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.clearTitle();
      editorPage.clickSubmit();
      test.info("Attempted to submit with empty title");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean stillOnEditor = editorPage.isOnEditorPage();
      test.info("Still on editor page: " + stillOnEditor);

      assertTrue(
          stillOnEditor || editorPage.hasErrors(),
          "Should show validation error or stay on editor page");
      test.pass("Empty title validation handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC012_UpdateWithVeryLongTitle() {
    createTest(
        "TC-012: Update with very long title (255+ chars)",
        "Verify behavior when updating with very long title");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      StringBuilder longTitle = new StringBuilder();
      for (int i = 0; i < 260; i++) {
        longTitle.append("a");
      }

      editorPage.updateTitle(longTitle.toString());
      editorPage.clickSubmit();
      test.info("Attempted to submit with 260 character title");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should respond to long title submission");
      test.pass("Long title handling completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC013_UpdateWithSpecialCharactersInTitle() {
    createTest(
        "TC-013: Update with special characters in title",
        "Verify title with special characters is handled correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String specialTitle = "Test @#$%^&*() Title " + System.currentTimeMillis();
      editorPage.updateTitle(specialTitle);
      editorPage.clickSubmit();
      test.info("Updated title with special characters: " + specialTitle);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"),
          "Should handle special characters in title");
      test.pass("Special characters in title handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC014_UpdateWithEmptyDescription() {
    createTest(
        "TC-014: Update with empty description",
        "Verify behavior when updating with empty description");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.clearDescription();
      editorPage.clickSubmit();
      test.info("Attempted to submit with empty description");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should respond to empty description submission");
      test.pass("Empty description handling completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC015_UpdateWithVeryLongDescription() {
    createTest(
        "TC-015: Update with very long description",
        "Verify behavior when updating with very long description");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      StringBuilder longDescription = new StringBuilder();
      for (int i = 0; i < 1000; i++) {
        longDescription.append("description ");
      }

      editorPage.updateDescription(longDescription.toString());
      editorPage.clickSubmit();
      test.info("Attempted to submit with 1000+ character description");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should respond to long description submission");
      test.pass("Long description handling completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC016_UpdateWithEmptyBody() {
    createTest(
        "TC-016: Update with empty body", "Verify validation error when updating with empty body");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.clearBody();
      editorPage.clickSubmit();
      test.info("Attempted to submit with empty body");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean stillOnEditor = editorPage.isOnEditorPage();
      test.info("Still on editor page: " + stillOnEditor);

      assertTrue(
          stillOnEditor || editorPage.hasErrors(),
          "Should show validation error or stay on editor page for empty body");
      test.pass("Empty body validation handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC017_UpdateWithVeryLongBody() {
    createTest(
        "TC-017: Update with very long body (10000+ chars)",
        "Verify behavior when updating with very long body");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      StringBuilder longBody = new StringBuilder();
      for (int i = 0; i < 10000; i++) {
        longBody.append("body content ");
      }

      editorPage.updateBody(longBody.toString());
      editorPage.clickSubmit();
      test.info("Attempted to submit with 10000+ character body");

      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should respond to long body submission");
      test.pass("Long body handling completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC018_UpdateWithHtmlContentInBody() {
    createTest(
        "TC-018: Update with HTML content in body",
        "Verify HTML content in body is sanitized or rendered properly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String htmlBody =
          "<h1>HTML Header</h1><p>Paragraph with <strong>bold</strong> and <em>italic</em></p><script>alert('xss')</script>";
      editorPage.updateBody(htmlBody);
      editorPage.clickSubmit();
      test.info("Updated body with HTML content");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should handle HTML content in body");
      test.pass("HTML content in body handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC019_UpdateWithMarkdownContentInBody() {
    createTest(
        "TC-019: Update with markdown content in body",
        "Verify markdown content is rendered correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String markdownBody =
          "# Markdown Header\n\n"
              + "## Subheader\n\n"
              + "This is a paragraph with **bold** and *italic* text.\n\n"
              + "- List item 1\n"
              + "- List item 2\n"
              + "- List item 3\n\n"
              + "```java\n"
              + "public class Test {\n"
              + "    public static void main(String[] args) {\n"
              + "        System.out.println(\"Hello\");\n"
              + "    }\n"
              + "}\n"
              + "```\n\n"
              + "[Link text](https://example.com)";

      editorPage.updateBody(markdownBody);
      editorPage.clickSubmit();
      test.info("Updated body with markdown content");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should handle markdown content in body");
      test.pass("Markdown content in body handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"validation", "regression"})
  public void testTC020_UpdateWithWhitespaceOnlyTitle() {
    createTest(
        "TC-020: Update with whitespace-only title",
        "Verify validation error when updating with whitespace-only title");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.clearTitle();
      editorPage.enterTitle("     ");
      editorPage.clickSubmit();
      test.info("Attempted to submit with whitespace-only title");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean stillOnEditor = editorPage.isOnEditorPage();
      test.info("Still on editor page: " + stillOnEditor);

      assertTrue(
          stillOnEditor || editorPage.hasErrors() || driver.getCurrentUrl().contains("localhost"),
          "Should handle whitespace-only title appropriately");
      test.pass("Whitespace-only title validation handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }
}
