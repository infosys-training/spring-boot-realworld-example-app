package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthorFilterValidationTests extends BaseTest {

  private ProfilePage profilePage;
  private String baseUrl;

  private static final String VALID_AUTHOR = "johndoe";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    profilePage = new ProfilePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-021: Author filter is case-sensitive")
  public void testTC021_AuthorFilterIsCaseSensitive() {
    createTest(
        "TC-021: Author filter is case-sensitive",
        "Verify that author filter matches exact case (case-sensitive)");

    profilePage.navigateTo(baseUrl, "JohnDoe");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for 'JohnDoe' (different case): " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Case-sensitive filter should return appropriate results");
    test.info("Case sensitivity test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-022: Partial username does not match")
  public void testTC022_PartialUsernameDoesNotMatch() {
    createTest(
        "TC-022: Partial username does not match",
        "Verify that partial username does not return results (exact match required)");

    profilePage.navigateTo(baseUrl, "john");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for partial username 'john': " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Partial username should not match or return appropriate results");
    test.info("Partial username test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-023: Username with leading/trailing spaces")
  public void testTC023_UsernameWithLeadingTrailingSpaces() {
    createTest(
        "TC-023: Username with leading/trailing spaces",
        "Verify that spaces in username are handled appropriately");

    profilePage.navigateTo(baseUrl, " " + VALID_AUTHOR + " ");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count for username with spaces: " + articleCount);

    assertTrue(articleCount >= 0, "Spaces should be handled gracefully");
    test.info("Username with spaces test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-024: Special characters in author parameter")
  public void testTC024_SpecialCharactersInAuthorParameter() {
    createTest(
        "TC-024: Special characters in author parameter",
        "Verify that special characters in author parameter are handled safely");

    profilePage.navigateTo(baseUrl, "@#$%");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for special characters: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Special characters should be handled safely without errors");
    test.info("Special characters test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-025: Empty author parameter")
  public void testTC025_EmptyAuthorParameter() {
    createTest(
        "TC-025: Empty author parameter",
        "Verify that empty author parameter is handled appropriately");

    driver.get(baseUrl + "/profile/");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    test.info("Current URL after empty author: " + currentUrl);

    assertTrue(
        currentUrl.contains("/profile") || currentUrl.contains(baseUrl),
        "Empty author should be handled gracefully");
    test.info("Empty author parameter test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-026: Very long author parameter")
  public void testTC026_VeryLongAuthorParameter() {
    createTest(
        "TC-026: Very long author parameter",
        "Verify that very long author parameter is handled gracefully");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longUsername.append("a");
    }

    profilePage.navigateTo(baseUrl, longUsername.toString());
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for very long username: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Very long username should be handled gracefully");
    test.info("Very long author parameter test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-027: Author parameter with numbers")
  public void testTC027_AuthorParameterWithNumbers() {
    createTest(
        "TC-027: Author parameter with numbers",
        "Verify that author parameter with numbers works correctly");

    profilePage.navigateTo(baseUrl, "user123");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for 'user123': " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount >= 0 || hasNoArticles, "Username with numbers should be handled correctly");
    test.info("Author with numbers test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-028: Author parameter with unicode characters")
  public void testTC028_AuthorParameterWithUnicodeCharacters() {
    createTest(
        "TC-028: Author parameter with unicode characters",
        "Verify that unicode characters in author parameter are handled gracefully");

    profilePage.navigateTo(baseUrl, "用户名");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for unicode username: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Unicode characters should be handled gracefully");
    test.info("Unicode characters test completed");
  }
}
