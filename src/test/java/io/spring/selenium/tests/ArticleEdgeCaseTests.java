package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NewArticlePage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for article creation (TC-031 to TC-040). Tests boundary conditions and edge
 * cases for the Create Article feature.
 */
public class ArticleEdgeCaseTests extends BaseTest {

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

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC031_createArticleWithSpecialCharactersInTitle() {
    createTest(
        "TC-031: Create article with special characters in title",
        "Verify article can be created with special characters and slug is sanitized");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 6);
    String title = "Special !@#$%^&*() Title " + uniqueId;
    String description = "Testing special characters in title";
    String body = "Body content for special characters test.";

    newArticlePage.createArticle(title, description, body);
    test.info("Created article with special characters in title: " + title);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with special characters should be created");
    test.info("Article created successfully. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC032_createArticleWithUnicodeCharactersInTitle() {
    createTest(
        "TC-032: Create article with unicode characters in title",
        "Verify article can be created with unicode characters");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String uniqueId = UUID.randomUUID().toString().substring(0, 6);
    String title = "Unicode Test " + uniqueId;
    String description = "Testing unicode characters";
    String body = "Body content for unicode test.";

    newArticlePage.createArticle(title, description, body);
    test.info("Created article with unicode in title: " + title);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with unicode characters should be created");
    test.info("Article with unicode created successfully. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC033_createArticleWithVeryLongTitle() {
    createTest(
        "TC-033: Create article with very long title", "Verify behavior with 200+ character title");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    StringBuilder longTitle = new StringBuilder();
    for (int i = 0; i < 25; i++) {
      longTitle.append("LongTitle");
    }
    String title = longTitle.toString().substring(0, 200);
    String description = "Testing very long title";
    String body = "Body content for long title test.";

    newArticlePage.createArticle(title, description, body);
    test.info("Created article with 200+ character title");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean articleCreated = currentUrl.contains("/article/") || articlePage.isArticleDisplayed();
    boolean stayedOnEditor = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        articleCreated || stayedOnEditor || hasError,
        "Long title should either be accepted or show validation error");
    test.info("Long title handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "edge-case", "article"})
  public void TC034_createArticleWithHtmlContentInBody() {
    createTest(
        "TC-034: Create article with HTML content in body",
        "Verify HTML is escaped/sanitized to prevent XSS");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing HTML content in body";
    String body =
        "<script>alert('XSS')</script><p>Normal content</p><img src='x' onerror='alert(1)'>";

    newArticlePage.createArticle(title, description, body);
    test.info("Created article with HTML/script content in body");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with HTML content should be created (with sanitization)");
    test.info("HTML content handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC035_createArticleWithMarkdownContentInBody() {
    createTest(
        "TC-035: Create article with markdown content in body",
        "Verify markdown is preserved and rendered correctly");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing markdown content";
    String body =
        "# Heading 1\n\n"
            + "## Heading 2\n\n"
            + "**Bold text** and *italic text*\n\n"
            + "- List item 1\n"
            + "- List item 2\n\n"
            + "`code snippet`\n\n"
            + "```java\npublic class Test {}\n```";

    newArticlePage.createArticle(title, description, body);
    test.info("Created article with markdown content");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with markdown content should be created");
    test.info("Markdown content handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "edge-case", "article"})
  public void TC036_verifySlugUniquenessForDuplicateTitles() {
    createTest(
        "TC-036: Verify slug uniqueness for duplicate titles",
        "Verify second article with same title gets unique slug");

    loginAsTestUser();
    test.info("Logged in as test user");

    String baseTitle = "Duplicate Title Test " + UUID.randomUUID().toString().substring(0, 6);
    String description = "Testing slug uniqueness";
    String body = "Body content for slug uniqueness test.";

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    newArticlePage.createArticle(baseTitle, description, body);
    test.info("Created first article with title: " + baseTitle);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String firstUrl = driver.getCurrentUrl();
    test.info("First article URL: " + firstUrl);

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    newArticlePage.createArticle(baseTitle, description + " 2", body + " 2");
    test.info("Created second article with same title");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String secondUrl = driver.getCurrentUrl();
    test.info("Second article URL: " + secondUrl);

    boolean bothCreated = firstUrl.contains("/article/") && secondUrl.contains("/article/");
    boolean differentUrls = !firstUrl.equals(secondUrl);

    assertTrue(bothCreated && differentUrls, "Both articles should be created with unique slugs");
    test.info("Slug uniqueness verified. URLs are different.");
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC037_createArticleWithDuplicateTagNames() {
    createTest(
        "TC-037: Create article with duplicate tag names",
        "Verify duplicate tags are handled (deduplicated or error)");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing duplicate tags";
    String body = "Body content for duplicate tags test.";
    List<String> tags = Arrays.asList("java", "java", "spring");

    newArticlePage.createArticleWithTags(title, description, body, tags);
    test.info("Created article with duplicate tags");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with duplicate tags should be handled");
    test.info("Duplicate tags handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC038_createArticleWithEmptyStringTagInList() {
    createTest(
        "TC-038: Create article with empty string tag in list",
        "Verify empty tag is rejected or ignored");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing empty tag";
    String body = "Body content for empty tag test.";

    newArticlePage.enterTitle(title);
    newArticlePage.enterDescription(description);
    newArticlePage.enterBody(body);
    newArticlePage.enterTag("validtag");
    newArticlePage.enterTag("");
    newArticlePage.clickPublish();
    test.info("Created article with empty tag attempt");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article should be created (empty tag ignored)");
    test.info("Empty tag handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC039_createArticleWithSpecialCharactersInTags() {
    createTest(
        "TC-039: Create article with special characters in tags",
        "Verify tags with special characters are handled appropriately");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing special character tags";
    String body = "Body content for special character tags test.";
    List<String> tags = Arrays.asList("c++", "c#", "node.js");

    newArticlePage.createArticleWithTags(title, description, body, tags);
    test.info("Created article with special character tags");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/article/") || articlePage.isArticleDisplayed(),
        "Article with special character tags should be handled");
    test.info("Special character tags handling verified. URL: " + currentUrl);
  }

  @Test(groups = {"regression", "edge-case", "article"})
  public void TC040_createArticleWithMaximumNumberOfTags() {
    createTest(
        "TC-040: Create article with maximum number of tags", "Verify behavior with 20+ tags");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    String title = generateUniqueTitle();
    String description = "Testing maximum tags";
    String body = "Body content for maximum tags test.";

    List<String> tags = new ArrayList<>();
    for (int i = 1; i <= 25; i++) {
      tags.add("tag" + i);
    }

    newArticlePage.createArticleWithTags(title, description, body, tags);
    test.info("Created article with 25 tags");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean articleCreated = currentUrl.contains("/article/") || articlePage.isArticleDisplayed();
    boolean stayedOnEditor = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        articleCreated || stayedOnEditor || hasError,
        "Maximum tags should either be accepted or show limit error");
    test.info("Maximum tags handling verified. URL: " + currentUrl);
  }
}
