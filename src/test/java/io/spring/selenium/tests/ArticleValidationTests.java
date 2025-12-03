package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NewArticlePage;
import java.util.ArrayList;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for article creation (TC-011 to TC-020). Tests input validation scenarios
 * for the Create Article feature.
 */
public class ArticleValidationTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private NewArticlePage newArticlePage;
  private ArticlePage articlePage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    newArticlePage = new NewArticlePage(driver);
    articlePage = new ArticlePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private String generateUniqueTitle() {
    return "Test Article " + UUID.randomUUID().toString().substring(0, 8);
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC011_verifyTitleFieldIsRequired() {
    createTest(
        "TC-011: Verify title field is required",
        "Verify error message when title is not provided");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish without title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        stayedOnEditorPage || hasError,
        "Should show error or stay on editor page when title is missing");
    test.info("Title field validation working correctly");
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC012_verifyDescriptionFieldIsRequired() {
    createTest(
        "TC-012: Verify description field is required",
        "Verify error message when description is not provided");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish without description");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        stayedOnEditorPage || hasError,
        "Should show error or stay on editor page when description is missing");
    test.info("Description field validation working correctly");
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC013_verifyBodyFieldIsRequired() {
    createTest(
        "TC-013: Verify body field is required", "Verify error message when body is not provided");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("Valid description");
    newArticlePage.clickPublish();
    test.info("Attempted to publish without body");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        stayedOnEditorPage || hasError,
        "Should show error or stay on editor page when body is missing");
    test.info("Body field validation working correctly");
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC014_verifyEmptyTitleIsRejected() {
    createTest(
        "TC-014: Verify empty title is rejected", "Verify validation error for empty string title");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle("");
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with empty title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject empty title");
    test.info("Empty title validation working correctly");
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC015_verifyEmptyDescriptionIsRejected() {
    createTest(
        "TC-015: Verify empty description is rejected",
        "Verify validation error for empty string description");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with empty description");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject empty description");
    test.info("Empty description validation working correctly");
  }

  @Test(groups = {"smoke", "validation", "article"})
  public void TC016_verifyEmptyBodyIsRejected() {
    createTest(
        "TC-016: Verify empty body is rejected", "Verify validation error for empty string body");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with empty body");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject empty body");
    test.info("Empty body validation working correctly");
  }

  @Test(groups = {"regression", "validation", "article"})
  public void TC017_verifyWhitespaceOnlyTitleIsRejected() {
    createTest(
        "TC-017: Verify whitespace-only title is rejected",
        "Verify validation error for whitespace-only title");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle("   ");
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with whitespace-only title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject whitespace-only title");
    test.info("Whitespace-only title validation working correctly");
  }

  @Test(groups = {"regression", "validation", "article"})
  public void TC018_verifyWhitespaceOnlyDescriptionIsRejected() {
    createTest(
        "TC-018: Verify whitespace-only description is rejected",
        "Verify validation error for whitespace-only description");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("   ");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with whitespace-only description");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject whitespace-only description");
    test.info("Whitespace-only description validation working correctly");
  }

  @Test(groups = {"regression", "validation", "article"})
  public void TC019_verifyWhitespaceOnlyBodyIsRejected() {
    createTest(
        "TC-019: Verify whitespace-only body is rejected",
        "Verify validation error for whitespace-only body");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("   ");
    newArticlePage.clickPublish();
    test.info("Attempted to publish with whitespace-only body");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should reject whitespace-only body");
    test.info("Whitespace-only body validation working correctly");
  }

  @Test(groups = {"regression", "validation", "article"})
  public void TC020_verifyEmptyTagsArrayIsAccepted() {
    createTest(
        "TC-020: Verify empty tags array is accepted",
        "Verify article can be created without any tags");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    newArticlePage.enterTitle(title);
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.createArticleWithTags(
        title, "Valid description", "Valid body", new ArrayList<>());
    test.info("Created article without any tags");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        driver.getCurrentUrl().contains("/article/") || articlePage.isArticleDisplayed(),
        "Article should be created successfully without tags");
    test.info("Article created successfully without tags");
  }
}
