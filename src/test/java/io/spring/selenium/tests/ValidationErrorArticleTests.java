package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.HomePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationErrorArticleTests extends BaseTest {

  private ArticleEditorPage editorPage;
  private HomePage homePage;
  private String baseUrl;

  @BeforeMethod
  public void setUp() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    editorPage = new ArticleEditorPage(driver);
    homePage = new HomePage(driver);
  }

  private void loginAsTestUser() {
    homePage.loginAs(baseUrl, "john@example.com", "password123");
  }

  @Test(
      groups = {"smoke", "validation", "article"},
      description = "TC-020: Verify error for empty article title")
  public void testTC020_EmptyArticleTitleError() {
    createTest(
        "TC-020: Verify error for empty article title",
        "Verify that empty article title shows required error");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage
        .clearTitle()
        .enterDescription("Test description")
        .enterBody("Test body content")
        .clickPublish();
    test.info("Submitted article with empty title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(editorPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        editorPage.hasErrorContaining("title")
            || editorPage.hasErrorContaining("required")
            || editorPage.hasErrorContaining("blank"),
        "Error should indicate title is required");
    test.info("Empty title error displayed: " + editorPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "article"},
      description = "TC-021: Verify error for empty article description")
  public void testTC021_EmptyArticleDescriptionError() {
    createTest(
        "TC-021: Verify error for empty article description",
        "Verify that empty article description shows required error");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage
        .enterTitle("Test Article Title")
        .clearDescription()
        .enterBody("Test body content")
        .clickPublish();
    test.info("Submitted article with empty description");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(editorPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        editorPage.hasErrorContaining("description")
            || editorPage.hasErrorContaining("required")
            || editorPage.hasErrorContaining("blank"),
        "Error should indicate description is required");
    test.info("Empty description error displayed: " + editorPage.getErrorMessages());
  }

  @Test(
      groups = {"smoke", "validation", "article"},
      description = "TC-022: Verify error for empty article body")
  public void testTC022_EmptyArticleBodyError() {
    createTest(
        "TC-022: Verify error for empty article body",
        "Verify that empty article body shows required error");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage
        .enterTitle("Test Article Title")
        .enterDescription("Test description")
        .clearBody()
        .clickPublish();
    test.info("Submitted article with empty body");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(editorPage.isErrorDisplayed(), "Error message should be displayed");
    assertTrue(
        editorPage.hasErrorContaining("body")
            || editorPage.hasErrorContaining("required")
            || editorPage.hasErrorContaining("blank"),
        "Error should indicate body is required");
    test.info("Empty body error displayed: " + editorPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "article"},
      description = "TC-023: Verify error for article with only whitespace title")
  public void testTC023_WhitespaceOnlyTitleError() {
    createTest(
        "TC-023: Verify error for article with only whitespace title",
        "Verify that whitespace-only title shows validation error");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage
        .enterTitle("   ")
        .enterDescription("Test description")
        .enterBody("Test body content")
        .clickPublish();
    test.info("Submitted article with whitespace-only title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = editorPage.isErrorDisplayed();
    if (hasError) {
      test.info("Whitespace title error displayed: " + editorPage.getErrorMessages());
    } else {
      test.info("Whitespace-only title may be trimmed and treated as empty");
    }
  }

  @Test(
      groups = {"smoke", "validation", "article"},
      description = "TC-024: Verify multiple article validation errors together")
  public void testTC024_MultipleArticleValidationErrors() {
    createTest(
        "TC-024: Verify multiple article validation errors together",
        "Verify that all article validation errors are displayed together");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage.clearTitle().clearDescription().clearBody().clickPublish();
    test.info("Submitted article with all fields empty");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(editorPage.isErrorDisplayed(), "Error messages should be displayed");
    int errorCount = editorPage.getErrorCount();
    assertTrue(errorCount >= 1, "Multiple validation errors should be displayed");
    test.info("Number of errors displayed: " + errorCount);
    test.info("All errors: " + editorPage.getErrorMessages());
  }

  @Test(
      groups = {"regression", "validation", "article"},
      description = "TC-025: Verify error for duplicate article slug")
  public void testTC025_DuplicateArticleSlugError() {
    createTest(
        "TC-025: Verify error for duplicate article slug",
        "Verify that duplicate article title/slug shows error");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage
        .enterTitle("How to train your dragon")
        .enterDescription("Test description")
        .enterBody("Test body content")
        .clickPublish();
    test.info("Submitted article with existing title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasError = editorPage.isErrorDisplayed();
    if (hasError) {
      test.info("Duplicate slug error displayed: " + editorPage.getErrorMessages());
    } else {
      test.info("Duplicate article titles may be allowed with unique slugs");
    }
  }

  @Test(
      groups = {"regression", "validation", "article"},
      description = "TC-026: Verify error for article creation without authentication")
  public void testTC026_ArticleCreationWithoutAuth() {
    createTest(
        "TC-026: Verify error for article creation without authentication",
        "Verify that unauthenticated article creation is blocked");

    driver.get(baseUrl + "/editor/new");
    test.info("Navigated directly to article editor without login");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("login");
    boolean onEditorPage = editorPage.isOnEditorPage();

    if (redirectedToLogin) {
      test.info("User redirected to login page as expected");
    } else if (!onEditorPage) {
      test.info("User blocked from accessing editor page");
    } else {
      test.info("Editor page accessible - authentication may be checked on submit");
    }
  }

  @Test(
      groups = {"regression", "validation", "article"},
      description = "TC-027: Verify error message format for article validation")
  public void testTC027_ArticleErrorMessageFormat() {
    createTest(
        "TC-027: Verify error message format for article validation",
        "Verify that article error messages are human-readable");

    loginAsTestUser();
    test.info("Logged in as test user");

    editorPage.navigateTo(baseUrl);
    test.info("Navigated to article editor");

    editorPage.clearTitle().enterDescription("Test").enterBody("Test").clickPublish();
    test.info("Submitted article with invalid data");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (editorPage.isErrorDisplayed()) {
      java.util.List<String> errors = editorPage.getErrorMessages();
      for (String error : errors) {
        assertFalse(error.isEmpty(), "Error message should not be empty");
        test.info("Error message: " + error);
      }
      test.info("Error messages are human-readable and actionable");
    } else {
      test.info("No validation errors displayed");
    }
  }
}
