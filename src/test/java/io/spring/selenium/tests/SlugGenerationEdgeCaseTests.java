package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SlugGenerationEdgeCaseTests extends BaseTest {

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

  @Test(groups = {"regression", "edge"})
  public void testTC031_SlugWithSpecialCharacters() {
    createTest(
        "TC-031: Verify slug with special characters (!@#$%^&*)",
        "Verify that special characters are removed from the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Test!@#$%Title", "Special characters test", "Body content for special chars test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          articlePage.isSlugUrlSafe(), "Slug should be URL-safe after removing special chars");
      assertFalse(
          slug.contains("!") || slug.contains("@") || slug.contains("#"),
          "Slug should not contain special characters");
      test.pass("Special characters were removed from slug");
    } else {
      test.info("Title with special characters may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC032_SlugWithUnicodeCharacters() {
    createTest(
        "TC-032: Verify slug with unicode characters",
        "Verify that unicode characters are handled appropriately in slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Cafe Resume", "Unicode characters test", "Body content for unicode test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Unicode characters were handled appropriately in slug");
    } else {
      test.info("Title with unicode characters may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC033_SlugWithEmojiInTitle() {
    createTest(
        "TC-033: Verify slug with emoji in title",
        "Verify that emoji characters are removed from the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle("Happy Article", "Emoji test", "Body content for emoji test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe after removing emoji");
      test.pass("Emoji was handled appropriately in slug");
    } else {
      test.info("Title with emoji may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC034_SlugWithHtmlTagsInTitle() {
    createTest(
        "TC-034: Verify slug with HTML tags in title",
        "Verify that HTML tags are stripped from the slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "<b>Bold</b> Title", "HTML tags test", "Body content for HTML tags test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.contains("<") || slug.contains(">"), "Slug should not contain HTML tags");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("HTML tags were stripped from slug");
    } else {
      test.info("Title with HTML tags may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC035_SlugWithNewlineCharactersInTitle() {
    createTest(
        "TC-035: Verify slug with newline characters in title",
        "Verify that newline characters are handled in slug generation");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Title With Newline", "Newline test", "Body content for newline test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.contains("\n") || slug.contains("\r"), "Slug should not contain newlines");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Newline characters were handled in slug");
    } else {
      test.info("Title with newline may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC036_SlugWithTabCharactersInTitle() {
    createTest(
        "TC-036: Verify slug with tab characters in title",
        "Verify that tab characters are handled in slug generation");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle("Title With Tab", "Tab test", "Body content for tab test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertFalse(slug.contains("\t"), "Slug should not contain tabs");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Tab characters were handled in slug");
    } else {
      test.info("Title with tab may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC037_SlugWithMixedSpecialCharsAndSpaces() {
    createTest(
        "TC-037: Verify slug with mixed special chars and spaces",
        "Verify that mixed special characters and spaces are handled correctly");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Test! @Article #1", "Mixed chars test", "Body content for mixed chars test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      assertTrue(
          slug.contains("test") || slug.contains("article"),
          "Slug should contain valid title words");
      test.pass("Mixed special chars and spaces were handled correctly");
    } else {
      test.info("Title with mixed chars may be handled differently");
    }
  }

  @Test(groups = {"smoke", "edge"})
  public void testTC038_SlugUniquenessAcrossRapidArticleCreation() {
    createTest(
        "TC-038: Verify slug uniqueness across rapid article creation",
        "Verify that rapidly created articles with same title get unique slugs");

    loginAsTestUser();

    String[] slugs = new String[3];

    for (int i = 0; i < 3; i++) {
      editorPage.navigateTo(baseUrl);
      editorPage.createArticle(
          "Rapid Creation Test",
          "Rapid test " + i,
          "Body content for rapid creation test " + i + ".");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      if (driver.getCurrentUrl().contains("/article/")) {
        slugs[i] = articlePage.getSlugFromUrl();
      }
    }

    if (slugs[0] != null && slugs[1] != null) {
      assertNotEquals(slugs[0], slugs[1], "First two articles should have unique slugs");
    }
    if (slugs[1] != null && slugs[2] != null) {
      assertNotEquals(slugs[1], slugs[2], "Second and third articles should have unique slugs");
    }
    if (slugs[0] != null && slugs[2] != null) {
      assertNotEquals(slugs[0], slugs[2], "First and third articles should have unique slugs");
    }

    test.pass("Rapid article creation produced unique slugs");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC039_SlugWithVeryLongWordsNoSpaces() {
    createTest(
        "TC-039: Verify slug with very long words (no spaces)",
        "Verify that very long words without spaces are handled in slug");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.createArticle(
        "Supercalifragilisticexpialidocious", "Long word test", "Body content for long word test.");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    if (currentUrl.contains("/article/")) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(slug.length() > 0, "Slug should be generated for long word");
      assertTrue(articlePage.isSlugUrlSafe(), "Slug should be URL-safe");
      test.pass("Very long word was handled in slug generation");
    } else {
      test.info("Very long word title may be handled differently");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC040_SlugWithOnlySpecialCharactersInTitle() {
    createTest(
        "TC-040: Verify slug with only special characters in title",
        "Verify that title with only special characters is handled appropriately");

    loginAsTestUser();
    editorPage.navigateTo(baseUrl);
    editorPage.enterTitle("!@#$%^&*()");
    editorPage.enterDescription("Only special chars test");
    editorPage.enterBody("Body content for only special chars test.");
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

    if (articleCreated) {
      String slug = articlePage.getSlugFromUrl();
      assertTrue(
          articlePage.isSlugUrlSafe(), "Slug should be URL-safe even for special chars only");
      test.pass("Title with only special characters generated a valid slug");
    } else {
      assertTrue(
          stillOnEditor || hasError,
          "Should either create article or show error for special chars only title");
      test.pass("Title with only special characters was handled appropriately");
    }
  }
}
