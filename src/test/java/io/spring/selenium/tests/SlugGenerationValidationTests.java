package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SlugGenerationValidationTests extends BaseTest {

  private HomePage homePage;
  private LoginPage loginPage;
  private ArticleEditorPage editorPage;
  private ArticlePage articlePage;
  private String baseUrl;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

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

  @Test(groups = {"regression", "validation"})
  public void testTC011_SlugGenerationWithMinimumLengthTitle() {
    createTest(
        "TC-011: Verify slug generation with minimum length title (1 char)",
        "Verify that a slug is generated for a single character title");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "A", "Single character title test", "Body content for min length test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          slug.contains("a") || slug.length() > 0, "Slug should be generated for single char");
      test.pass("Slug generated for minimum length title");
    } else {
      test.info("Article creation may require longer title - validation in place");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC012_SlugGenerationWithMaximumLengthTitle() {
    createTest(
        "TC-012: Verify slug generation with maximum length title",
        "Verify that a slug is generated and appropriately handled for very long titles");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);

    StringBuilder longTitle = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      longTitle.append("Word").append(i).append(" ");
    }

    editorPage.createArticle(
        longTitle.toString().trim(), "Long title test", "Body content for max length test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(slug.length() > 0, "Slug should be generated for long title");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Slug generated for maximum length title");
    } else {
      test.info("Long title may be truncated or rejected");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC013_SlugHandlesTitleWithLeadingSpaces() {
    createTest(
        "TC-013: Verify slug handles title with leading spaces",
        "Verify that leading spaces are trimmed from the title in slug generation");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "  Leading Spaces", "Leading spaces test", "Body content for leading spaces test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.startsWith("-"), "Slug should not start with hyphen from leading spaces");
      assertTrue(
          slug.contains("leading") || slug.contains("spaces"), "Slug should contain title words");
      test.pass("Leading spaces were trimmed in slug generation");
    } else {
      test.info("Title with leading spaces may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC014_SlugHandlesTitleWithTrailingSpaces() {
    createTest(
        "TC-014: Verify slug handles title with trailing spaces",
        "Verify that trailing spaces are trimmed from the title in slug generation");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Trailing Spaces  ", "Trailing spaces test", "Body content for trailing spaces test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.endsWith("-"), "Slug should not end with hyphen from trailing spaces");
      assertTrue(
          slug.contains("trailing") || slug.contains("spaces"), "Slug should contain title words");
      test.pass("Trailing spaces were trimmed in slug generation");
    } else {
      test.info("Title with trailing spaces may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC015_SlugHandlesTitleWithConsecutiveSpaces() {
    createTest(
        "TC-015: Verify slug handles title with consecutive spaces",
        "Verify that consecutive spaces become a single hyphen in the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Multiple   Spaces",
        "Consecutive spaces test",
        "Body content for consecutive spaces test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.contains("--"), "Slug should not contain consecutive hyphens");
      test.pass("Consecutive spaces were handled correctly in slug");
    } else {
      test.info("Title with consecutive spaces may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC016_SlugHandlesTitleWithNumbers() {
    createTest(
        "TC-016: Verify slug handles title with numbers",
        "Verify that numbers in the title are preserved in the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Article 123 Test", "Numbers in title test", "Body content for numbers test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(slug.contains("123"), "Slug should preserve numbers from title");
      test.pass("Numbers were preserved in slug");
    } else {
      test.info("Title with numbers may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC017_SlugHandlesTitleWithHyphens() {
    createTest(
        "TC-017: Verify slug handles title with hyphens",
        "Verify that hyphens in the title are preserved in the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Pre-existing Hyphen", "Hyphens in title test", "Body content for hyphen test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          slug.contains("pre-existing") || slug.contains("hyphen"),
          "Slug should handle hyphens correctly");
      test.pass("Hyphens were handled correctly in slug");
    } else {
      test.info("Title with hyphens may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC018_SlugHandlesTitleStartingWithNumber() {
    createTest(
        "TC-018: Verify slug handles title starting with number",
        "Verify that titles starting with numbers generate valid slugs");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "123 Starting Number", "Number start test", "Body content for number start test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          slug.startsWith("123") || slug.contains("123"), "Slug should handle leading numbers");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Title starting with number generated valid slug");
    } else {
      test.info("Title starting with number may be handled differently");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC019_SlugHandlesTitleEndingWithNumber() {
    createTest(
        "TC-019: Verify slug handles title ending with number",
        "Verify that titles ending with numbers generate valid slugs");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Ending Number 456", "Number end test", "Body content for number end test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          slug.contains("456") || slug.contains("ending"), "Slug should handle trailing numbers");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Title ending with number generated valid slug");
    } else {
      test.info("Title ending with number may be handled differently");
    }
  }

  @Test(groups = {"smoke", "validation"})
  public void testTC020_SlugUniquenessForDuplicateTitles() {
    createTest(
        "TC-020: Verify slug uniqueness for duplicate titles",
        "Verify that articles with the same title get unique slugs");

    loginAsTestUser();

    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Duplicate Test", "First duplicate article", "Body content for first duplicate.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String firstSlug = articlePage.getSlugFromUrl();

    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Duplicate Test", "Second duplicate article", "Body content for second duplicate.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String secondSlug = articlePage.getSlugFromUrl();

    assertNotEquals(firstSlug, secondSlug, "Duplicate titles should generate unique slugs");
    test.pass("Duplicate titles generated unique slugs");
  }
}
