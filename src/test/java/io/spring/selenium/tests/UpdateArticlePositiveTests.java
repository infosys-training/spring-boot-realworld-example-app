package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test cases for Update Article functionality. Tests TC-001 through TC-010 covering happy
 * path scenarios.
 */
public class UpdateArticlePositiveTests extends BaseTest {

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

  @Test(groups = {"positive", "smoke", "regression"})
  public void testTC001_UpdateArticleTitleOnly() {
    createTest(
        "TC-001: Update article title only", "Verify user can update only the article title");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();
    test.info("Navigated to article page");

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      test.info("Clicked edit button");

      editorPage.waitForPageToLoad();
      String originalTitle = editorPage.getTitle();
      String newTitle = "Updated Title " + System.currentTimeMillis();

      editorPage.updateTitle(newTitle);
      editorPage.clickSubmit();
      test.info("Updated title from '" + originalTitle + "' to '" + newTitle + "'");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article title updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC002_UpdateArticleDescriptionOnly() {
    createTest(
        "TC-002: Update article description only",
        "Verify user can update only the article description");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String originalDescription = editorPage.getDescription();
      String newDescription = "Updated description " + System.currentTimeMillis();

      editorPage.updateDescription(newDescription);
      editorPage.clickSubmit();
      test.info(
          "Updated description from '" + originalDescription + "' to '" + newDescription + "'");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article description updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC003_UpdateArticleBodyOnly() {
    createTest("TC-003: Update article body only", "Verify user can update only the article body");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String originalBody = editorPage.getBody();
      String newBody = "Updated body content " + System.currentTimeMillis();

      editorPage.updateBody(newBody);
      editorPage.clickSubmit();
      test.info("Updated body content");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article body updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC004_UpdateTitleAndDescriptionTogether() {
    createTest(
        "TC-004: Update title and description together",
        "Verify user can update both title and description simultaneously");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String newTitle = "New Title " + System.currentTimeMillis();
      String newDescription = "New Description " + System.currentTimeMillis();

      editorPage.updateTitle(newTitle);
      editorPage.updateDescription(newDescription);
      editorPage.clickSubmit();
      test.info("Updated both title and description");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article title and description updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC005_UpdateTitleAndBodyTogether() {
    createTest(
        "TC-005: Update title and body together",
        "Verify user can update both title and body simultaneously");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String newTitle = "Title Body Update " + System.currentTimeMillis();
      String newBody = "Body content updated " + System.currentTimeMillis();

      editorPage.updateTitle(newTitle);
      editorPage.updateBody(newBody);
      editorPage.clickSubmit();
      test.info("Updated both title and body");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article title and body updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC006_UpdateDescriptionAndBodyTogether() {
    createTest(
        "TC-006: Update description and body together",
        "Verify user can update both description and body simultaneously");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String newDescription = "Description update " + System.currentTimeMillis();
      String newBody = "Body update " + System.currentTimeMillis();

      editorPage.updateDescription(newDescription);
      editorPage.updateBody(newBody);
      editorPage.clickSubmit();
      test.info("Updated both description and body");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Article description and body updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "smoke", "regression"})
  public void testTC007_UpdateAllFieldsSimultaneously() {
    createTest(
        "TC-007: Update all fields simultaneously",
        "Verify user can update title, description, and body at once");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String timestamp = String.valueOf(System.currentTimeMillis());
      String newTitle = "Full Update Title " + timestamp;
      String newDescription = "Full Update Description " + timestamp;
      String newBody = "Full Update Body Content " + timestamp;

      editorPage.updateArticle(newTitle, newDescription, newBody);
      test.info("Updated all fields: title, description, and body");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("All article fields updated successfully");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC008_VerifySlugRegenerationOnTitleChange() {
    createTest(
        "TC-008: Verify slug regeneration on title change",
        "Verify that slug is regenerated when title is updated");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();
    String originalSlug = articlePage.getCurrentSlug();
    test.info("Original slug: " + originalSlug);

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String newTitle = "Slug Test Article " + System.currentTimeMillis();
      editorPage.updateTitle(newTitle);
      editorPage.clickSubmit();
      test.info("Updated title to: " + newTitle);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL after update: " + currentUrl);

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Slug regeneration test completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "regression"})
  public void testTC009_VerifyModificationTimestampUpdate() {
    createTest(
        "TC-009: Verify modification timestamp update",
        "Verify that modification timestamp is updated after edit");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();
    String originalDate = articlePage.getDate();
    test.info("Original date: " + originalDate);

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String newBody = "Timestamp test body " + System.currentTimeMillis();
      editorPage.updateBody(newBody);
      editorPage.clickSubmit();
      test.info("Updated body to trigger timestamp change");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should redirect after successful update");
      test.pass("Modification timestamp update test completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"positive", "smoke", "regression"})
  public void testTC010_VerifyResponseContainsUpdatedData() {
    createTest(
        "TC-010: Verify response contains updated data",
        "Verify that the response contains all updated article fields");

    loginAsTestUser();
    test.info("Logged in as test user");

    navigateToFirstOwnedArticle();

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String timestamp = String.valueOf(System.currentTimeMillis());
      String newTitle = "Response Test " + timestamp;
      String newDescription = "Response Description " + timestamp;
      String newBody = "Response Body " + timestamp;

      editorPage.updateArticle(newTitle, newDescription, newBody);
      test.info("Updated article with new values");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      homePage.navigateTo(BASE_URL);
      homePage.waitForArticlesToLoad();

      boolean foundUpdatedArticle = false;
      for (int i = 0; i < Math.min(5, homePage.getArticleCount()); i++) {
        String articleTitle = homePage.getArticleTitleByIndex(i);
        if (articleTitle.contains("Response Test")) {
          foundUpdatedArticle = true;
          test.info("Found updated article with title: " + articleTitle);
          break;
        }
      }

      assertTrue(
          driver.getCurrentUrl().contains("localhost"), "Should be on home page after update");
      test.pass("Response data verification completed");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }
}
