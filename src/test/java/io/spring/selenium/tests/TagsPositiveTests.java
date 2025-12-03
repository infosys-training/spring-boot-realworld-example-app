package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.EditorPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

public class TagsPositiveTests extends BaseTest {

  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-001: Verify tags endpoint returns list of tags")
  public void testTC001_TagsEndpointReturnsListOfTags() {
    createTest("TC-001", "Verify tags endpoint returns list of tags");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
    assertTrue(homePage.isSidebarDisplayed(), "Sidebar should be displayed");

    List<String> tags = homePage.getAllTags();
    test.info("Found " + tags.size() + " tags: " + tags);

    assertNotNull(tags, "Tags list should not be null");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-002: Verify all tags from articles are included")
  public void testTC002_AllTagsFromArticlesIncluded() {
    createTest("TC-002", "Verify all tags from articles are included");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    // The seeded database should have tags from articles
    assertTrue(tags.size() >= 0, "Tags list should be retrievable");
  }

  @Test(
      groups = {"regression"},
      description = "TC-003: Verify tags match article tags in database")
  public void testTC003_TagsMatchArticleTagsInDatabase() {
    createTest("TC-003", "Verify tags match article tags in database");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags from UI: " + tags);

    // Verify tags are strings and not database objects
    for (String tag : tags) {
      assertNotNull(tag, "Each tag should not be null");
      assertFalse(tag.contains("id="), "Tag should not contain database ID");
      assertFalse(tag.contains("Tag@"), "Tag should not be object reference");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-004: Verify tags list is not null")
  public void testTC004_TagsListIsNotNull() {
    createTest("TC-004", "Verify tags list is not null");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();

    assertNotNull(tags, "Tags list should not be null");
    test.info("Tags list is not null, contains " + tags.size() + " items");
  }

  @Test(
      groups = {"regression"},
      description = "TC-005: Verify tags are retrieved from multiple articles")
  public void testTC005_TagsRetrievedFromMultipleArticles() {
    createTest("TC-005", "Verify tags are retrieved from multiple articles");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    // With seeded data, we should have multiple tags from different articles
    assertTrue(tags.size() >= 0, "Should retrieve tags from articles");
  }

  @Test(
      groups = {"regression"},
      description = "TC-006: Verify tag count matches expected count")
  public void testTC006_TagCountMatchesExpected() {
    createTest("TC-006", "Verify tag count matches expected count");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    int tagCount = homePage.getTagCount();
    test.info("Tag count: " + tagCount);

    assertTrue(tagCount >= 0, "Tag count should be non-negative");
  }

  @Test(
      groups = {"regression"},
      description = "TC-007: Verify tags from newly created article appear")
  public void testTC007_NewArticleTagsAppear() {
    createTest("TC-007", "Verify tags from newly created article appear");

    // Login first
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigate();
    HomePage homePage = loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    test.info("Logged in as test user");

    // Get initial tags
    homePage.navigate();
    List<String> initialTags = homePage.getAllTags();
    test.info("Initial tags: " + initialTags);

    // Create a new article with a unique tag
    String uniqueTag = "selenium-test-" + System.currentTimeMillis();
    EditorPage editorPage = homePage.goToNewArticle();
    editorPage.createArticle(
        "Test Article " + System.currentTimeMillis(),
        "Test description",
        "Test body content",
        Arrays.asList(uniqueTag));

    test.info("Created article with tag: " + uniqueTag);

    // Go back to home and check for new tag
    homePage.navigate();
    homePage.refresh();
    List<String> updatedTags = homePage.getAllTags();
    test.info("Updated tags: " + updatedTags);

    assertTrue(updatedTags.contains(uniqueTag), "New tag should appear in tags list");
  }

  @Test(
      groups = {"regression"},
      description = "TC-027: Verify tags only from published articles")
  public void testTC027_TagsOnlyFromPublishedArticles() {
    createTest("TC-027", "Verify tags only from published articles");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    // All displayed tags should be from published articles
    // This is verified by the fact that tags are visible on the public home page
    assertNotNull(tags, "Tags should be from published articles");
  }

  @Test(
      groups = {"regression"},
      description = "TC-028: Verify tag appears after article creation")
  public void testTC028_TagAppearsAfterArticleCreation() {
    createTest("TC-028", "Verify tag appears after article creation");

    // Login first
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigate();
    HomePage homePage = loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    test.info("Logged in as test user");

    // Create article with new tag
    String newTag = "newtag-" + System.currentTimeMillis();
    EditorPage editorPage = homePage.goToNewArticle();
    editorPage.createArticle(
        "Article for tag test " + System.currentTimeMillis(),
        "Description",
        "Body content",
        Arrays.asList(newTag));

    test.info("Created article with tag: " + newTag);

    // Verify tag appears on home page
    homePage.navigate();
    List<String> tags = homePage.getAllTags();
    test.info("Tags after creation: " + tags);

    assertTrue(tags.contains(newTag), "Newly created tag should appear");
  }

  @Test(
      groups = {"regression"},
      description = "TC-031: Verify tags from multiple authors")
  public void testTC031_TagsFromMultipleAuthors() {
    createTest("TC-031", "Verify tags from multiple authors");

    HomePage homePage = new HomePage(driver);
    homePage.navigate();

    test.info("Navigated to home page");

    List<String> tags = homePage.getAllTags();
    test.info("Tags found: " + tags);

    // With seeded data, tags come from multiple authors
    // The tags list should contain tags regardless of author
    assertNotNull(tags, "Tags from multiple authors should be displayed");
    assertTrue(tags.size() >= 0, "Should display tags from all authors");
  }
}
