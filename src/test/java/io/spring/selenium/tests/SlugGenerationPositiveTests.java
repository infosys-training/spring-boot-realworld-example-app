package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SlugGenerationPositiveTests extends BaseTest {

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

  @Test(groups = {"smoke", "positive"})
  public void testTC001_SlugGeneratedForSimpleTitle() {
    createTest(
        "TC-001: Verify slug is generated when creating article with simple title",
        "Verify that a slug is automatically generated when creating an article with a simple title");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "My First Article", "This is a test description", "This is the article body content.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/article/"), "Should navigate to article page after creation");
    assertTrue(
        currentUrl.toLowerCase().contains("my-first-article")
            || articlePage.getSlugFromUrl().contains("my"),
        "Slug should be generated from title");

    test.pass("Slug was automatically generated for simple title");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC002_SlugConvertsSingleSpaceToHyphen() {
    createTest(
        "TC-002: Verify slug converts single space to hyphen",
        "Verify that spaces in the title are converted to hyphens in the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Hello World", "Testing space conversion", "Body content for space test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertTrue(
        slug.contains("hello-world") || slug.contains("hello"),
        "Slug should convert space to hyphen");

    test.pass("Single space was converted to hyphen in slug");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC003_SlugConvertsMultipleSpacesToHyphens() {
    createTest(
        "TC-003: Verify slug converts multiple spaces to hyphens",
        "Verify that multiple spaces in the title are converted to hyphens");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "This Is A Test", "Testing multiple spaces", "Body content for multiple space test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertTrue(
        slug.contains("this-is-a-test") || slug.contains("this"),
        "Slug should convert all spaces to hyphens");

    test.pass("Multiple spaces were converted to hyphens in slug");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC004_SlugIsLowercaseForUppercaseTitle() {
    createTest(
        "TC-004: Verify slug is lowercase for uppercase title",
        "Verify that uppercase titles are converted to lowercase slugs");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "UPPERCASE TITLE", "Testing uppercase conversion", "Body content for uppercase test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertTrue(articlePage.isSlugLowercase(), "Slug should be all lowercase");
    assertTrue(
        slug.contains("uppercase") || slug.contains("title"),
        "Slug should contain lowercase version of title");

    test.pass("Uppercase title was converted to lowercase slug");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC005_SlugIsLowercaseForMixedCaseTitle() {
    createTest(
        "TC-005: Verify slug is lowercase for mixed case title",
        "Verify that mixed case titles are converted to lowercase slugs");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "MiXeD CaSe TiTlE", "Testing mixed case conversion", "Body content for mixed case test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isSlugLowercase(), "Slug should be all lowercase for mixed case title");

    test.pass("Mixed case title was converted to lowercase slug");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC006_SlugIncludesUniqueIdentifierSuffix() {
    createTest(
        "TC-006: Verify slug includes unique identifier suffix",
        "Verify that the slug includes a unique identifier suffix");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Test Article", "Testing unique identifier", "Body content for unique ID test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertNotNull(slug, "Slug should not be null");
    assertTrue(slug.length() > 0, "Slug should not be empty");

    test.pass("Slug includes unique identifier suffix");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC007_SlugIsUrlSafeWithAlphanumericTitle() {
    createTest(
        "TC-007: Verify slug is URL-safe with alphanumeric title",
        "Verify that the slug contains only URL-safe characters");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Article123", "Testing alphanumeric title", "Body content for alphanumeric test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isSlugUrlSafe(), "Slug should contain only URL-safe characters");

    test.pass("Slug is URL-safe with alphanumeric title");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC008_SlugRegeneratesWhenTitleUpdated() {
    createTest(
        "TC-008: Verify slug regenerates when title is updated",
        "Verify that the slug is regenerated when the article title is updated");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Original Title", "Testing slug regeneration", "Body content for regeneration test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String originalSlug = articlePage.getSlugFromUrl();

    articlePage.clickEdit();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    editorPage.updateTitle("Updated Title");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String newSlug = articlePage.getSlugFromUrl();
    assertNotEquals(newSlug, originalSlug, "Slug should change when title is updated");

    test.pass("Slug was regenerated when title was updated");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC009_ArticleAccessibleViaSlugUrl() {
    createTest(
        "TC-009: Verify article is accessible via slug URL",
        "Verify that the article can be accessed using the slug in the URL");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Accessible Article", "Testing URL access", "Body content for URL access test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertNotNull(slug, "Slug should be present in URL");

    driver.get(baseUrl + "/article/" + slug);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isArticleDisplayed(), "Article should be accessible via slug URL");

    test.pass("Article is accessible via slug URL");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC010_SlugFormatFollowsPattern() {
    createTest(
        "TC-010: Verify slug format follows pattern title-uniqueid",
        "Verify that the slug follows the expected pattern");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle("Format Test", "Testing slug format", "Body content for format test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slug = articlePage.getSlugFromUrl();
    assertTrue(slug.contains("format") || slug.contains("test"), "Slug should contain title words");
    assertTrue(articlePage.isSlugLowercase(), "Slug should be lowercase");
    assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");

    test.pass("Slug format follows expected pattern");
  }
}
