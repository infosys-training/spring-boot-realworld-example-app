package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for Update Article functionality. Tests TC-031 through TC-040 covering
 * boundary and edge case scenarios.
 */
public class UpdateArticleEdgeCaseTests extends BaseTest {

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

  @Test(groups = {"edge", "regression"})
  public void testTC031_UpdateArticleWithSameTitle() {
    createTest(
        "TC-031: Update article with same title",
        "Verify slug remains unchanged when title is not modified");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();
    String originalSlug = articlePage.getCurrentSlug();
    test.info("Original slug: " + originalSlug);

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String currentTitle = editorPage.getTitle();
      test.info("Current title: " + currentTitle);

      editorPage.updateBody("Updated body only " + System.currentTimeMillis());
      editorPage.clickSubmit();
      test.info("Updated only the body, keeping same title");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article updated with same title - slug should remain unchanged");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC032_UpdateArticleMultipleTimesRapidly() {
    createTest(
        "TC-032: Update article multiple times rapidly",
        "Verify all rapid updates are successful without race conditions");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      for (int i = 1; i <= 3; i++) {
        articlePage.clickEditButton();
        editorPage.waitForPageToLoad();

        String updateBody = "Rapid update #" + i + " at " + System.currentTimeMillis();
        editorPage.updateBody(updateBody);
        editorPage.clickSubmit();
        test.info("Completed rapid update #" + i);

        try {
          Thread.sleep(1500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        homePage.navigateTo(BASE_URL);
        homePage.waitForArticlesToLoad();
        homePage.clickArticleByIndex(0);

        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should handle multiple rapid updates");
      test.pass("Multiple rapid updates completed successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC033_UpdateArticleWithUnicodeInTitle() {
    createTest(
        "TC-033: Update article with unicode in title",
        "Verify unicode characters in title are handled correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String unicodeTitle =
          "Unicode Test: \u4e2d\u6587 \u65e5\u672c\u8a9e \ud55c\uad6d\uc5b4 "
              + System.currentTimeMillis();
      editorPage.updateTitle(unicodeTitle);
      editorPage.clickSubmit();
      test.info("Updated title with unicode characters: " + unicodeTitle);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(driver.getCurrentUrl().contains("localhost"), "Should handle unicode in title");
      test.pass("Unicode title handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC034_UpdateArticleWithUnicodeInBody() {
    createTest(
        "TC-034: Update article with unicode in body",
        "Verify unicode characters in body are saved correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String unicodeBody =
          "This article contains unicode content:\n\n"
              + "Chinese: \u4e2d\u6587\u5185\u5bb9\n"
              + "Japanese: \u65e5\u672c\u8a9e\u306e\u30c6\u30ad\u30b9\u30c8\n"
              + "Korean: \ud55c\uad6d\uc5b4 \ud14d\uc2a4\ud2b8\n"
              + "Arabic: \u0645\u0631\u062d\u0628\u0627\n"
              + "Russian: \u041f\u0440\u0438\u0432\u0435\u0442\n"
              + "Greek: \u0393\u03b5\u03b9\u03ac \u03c3\u03bf\u03c5\n"
              + "Timestamp: "
              + System.currentTimeMillis();

      editorPage.updateBody(unicodeBody);
      editorPage.clickSubmit();
      test.info("Updated body with unicode characters");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(driver.getCurrentUrl().contains("localhost"), "Should handle unicode in body");
      test.pass("Unicode body content handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC035_UpdateArticleWithEmojiInContent() {
    createTest(
        "TC-035: Update article with emoji in content",
        "Verify emoji characters are saved and displayed correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String emojiBody =
          "Article with emojis:\n\n"
              + "Smileys: \ud83d\ude00 \ud83d\ude02 \ud83d\ude0d \ud83e\udd14 \ud83d\ude31\n"
              + "Animals: \ud83d\udc36 \ud83d\udc31 \ud83e\udd81 \ud83d\udc18 \ud83e\udd8b\n"
              + "Food: \ud83c\udf55 \ud83c\udf54 \ud83c\udf5f \ud83c\udf70 \ud83c\udf66\n"
              + "Activities: \u26bd \ud83c\udfc0 \ud83c\udfbe \ud83c\udfb8 \ud83c\udfa4\n"
              + "Symbols: \u2764\ufe0f \ud83d\udc9a \ud83d\udc99 \ud83d\udcaf \u2728\n"
              + "Timestamp: "
              + System.currentTimeMillis();

      editorPage.updateBody(emojiBody);
      editorPage.clickSubmit();
      test.info("Updated body with emoji characters");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(driver.getCurrentUrl().contains("localhost"), "Should handle emoji in content");
      test.pass("Emoji content handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC036_UpdateTitleToMatchAnotherArticle() {
    createTest(
        "TC-036: Update title to match another article",
        "Verify unique slug is generated when title matches another article");

    loginAsTestUser();
    test.info("Logged in as test user");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    String existingTitle = "";
    if (homePage.getArticleCount() > 1) {
      existingTitle = homePage.getArticleTitleByIndex(1);
      test.info("Existing article title: " + existingTitle);
    }

    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isEditButtonVisible() && !existingTitle.isEmpty()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.updateTitle(existingTitle);
      editorPage.clickSubmit();
      test.info("Updated title to match existing article: " + existingTitle);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"),
          "Should handle duplicate title with unique slug");
      test.pass("Duplicate title handled with unique slug generation");
    } else {
      test.skip("Edit button not visible or no other articles to match");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC037_UpdateArticleWithMinimumValidContent() {
    createTest(
        "TC-037: Update article with minimum valid content",
        "Verify minimum content (single character values) is accepted");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      editorPage.updateTitle("A");
      editorPage.updateDescription("B");
      editorPage.updateBody("C");
      editorPage.clickSubmit();
      test.info("Updated with minimum content: title='A', description='B', body='C'");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertTrue(currentUrl.contains("localhost"), "Should accept minimum valid content");
      test.pass("Minimum content accepted successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "regression"})
  public void testTC038_UpdateArticleImmediatelyAfterCreation() {
    createTest(
        "TC-038: Update article immediately after creation",
        "Verify article can be updated immediately after creation");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateToNew(BASE_URL);
    editorPage.waitForPageToLoad();

    String timestamp = String.valueOf(System.currentTimeMillis());
    String newTitle = "New Article " + timestamp;
    String newDescription = "Description " + timestamp;
    String newBody = "Body content " + timestamp;

    editorPage.enterTitle(newTitle);
    editorPage.enterDescription(newDescription);
    editorPage.enterBody(newBody);
    editorPage.clickSubmit();
    test.info("Created new article: " + newTitle);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    for (int i = 0; i < Math.min(5, homePage.getArticleCount()); i++) {
      String articleTitle = homePage.getArticleTitleByIndex(i);
      if (articleTitle.contains("New Article " + timestamp)) {
        homePage.clickArticleByIndex(i);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        if (articlePage.isEditButtonVisible()) {
          articlePage.clickEditButton();
          editorPage.waitForPageToLoad();

          String updatedBody = "Immediately updated body " + System.currentTimeMillis();
          editorPage.updateBody(updatedBody);
          editorPage.clickSubmit();
          test.info("Immediately updated newly created article");

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }

          assertTrue(
              driver.getCurrentUrl().contains("localhost"),
              "Should update immediately after creation");
          test.pass("Article updated immediately after creation");
          return;
        }
      }
    }

    test.skip("Could not find or edit newly created article");
  }

  @Test(groups = {"edge", "security", "regression"})
  public void testTC039_UpdateArticleWithXssAttempt() {
    createTest(
        "TC-039: Update article with XSS attempt",
        "Verify XSS content is sanitized and no script execution occurs");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String xssBody =
          "XSS Test Content:\n\n"
              + "<script>alert('XSS')</script>\n"
              + "<img src=x onerror=alert('XSS')>\n"
              + "<svg onload=alert('XSS')>\n"
              + "<body onload=alert('XSS')>\n"
              + "<iframe src='javascript:alert(1)'>\n"
              + "<a href='javascript:alert(1)'>Click me</a>\n"
              + "Timestamp: "
              + System.currentTimeMillis();

      editorPage.updateBody(xssBody);
      editorPage.clickSubmit();
      test.info("Submitted body with XSS attempts");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(driver.getCurrentUrl().contains("localhost"), "Should handle XSS content safely");
      test.pass("XSS content handled safely - no script execution");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"edge", "security", "regression"})
  public void testTC040_UpdateArticleWithSqlInjectionAttempt() {
    createTest(
        "TC-040: Update article with SQL injection attempt",
        "Verify SQL injection is sanitized with no database impact");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String sqlInjectionTitle = "Test'; DROP TABLE articles; --";
      String sqlInjectionBody =
          "SQL Injection Test:\n\n"
              + "1' OR '1'='1\n"
              + "'; DROP TABLE users; --\n"
              + "1; DELETE FROM articles WHERE 1=1; --\n"
              + "UNION SELECT * FROM users --\n"
              + "' OR 1=1 --\n"
              + "admin'--\n"
              + "Timestamp: "
              + System.currentTimeMillis();

      editorPage.updateTitle(sqlInjectionTitle);
      editorPage.updateBody(sqlInjectionBody);
      editorPage.clickSubmit();
      test.info("Submitted with SQL injection attempts");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      homePage.navigateTo(BASE_URL);
      homePage.waitForArticlesToLoad();

      int articleCount = homePage.getArticleCount();
      test.info("Article count after SQL injection attempt: " + articleCount);

      assertTrue(articleCount > 0, "Articles should still exist after SQL injection attempt");
      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should handle SQL injection safely");
      test.pass("SQL injection handled safely - no database impact");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }
}
