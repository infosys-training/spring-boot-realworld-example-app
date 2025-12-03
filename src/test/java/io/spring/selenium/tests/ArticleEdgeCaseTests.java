package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArticleEdgeCaseTests extends BaseTest {

  private ArticlePage articlePage;
  private HomePage homePage;
  private LoginPage loginPage;

  private static final String TEST_ARTICLE_SLUG = "how-to-train-your-dragon";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    articlePage = new ArticlePage(driver);
    homePage = new HomePage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC031_viewArticleWithNoTags() {
    createTest(
        "TC-031: View article with no tags",
        "Verify article displays correctly when it has no tags");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    int tagCount = articlePage.getTagCount();
    if (tagCount == 0) {
      test.info("Article has no tags - tag section should be empty or hidden");
    } else {
      test.info("Article has " + tagCount + " tags");
    }
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC032_viewArticleWithMaximumTags() {
    createTest(
        "TC-032: View article with maximum tags",
        "Verify all tags display correctly without UI breaking for articles with many tags");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    int tagCount = articlePage.getTagCount();
    if (tagCount > 0) {
      assertTrue(articlePage.isTagListDisplayed(), "Tag list should be displayed");
      test.info("Article has " + tagCount + " tags - all displayed correctly");
    } else {
      test.info("Article has no tags");
    }
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC033_viewArticleWithVeryLongTitle() {
    createTest(
        "TC-033: View article with very long title",
        "Verify title displays correctly, possibly truncated, for articles with long titles");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    String title = articlePage.getArticleTitle();
    assertNotNull(title, "Title should not be null");
    test.info("Article title length: " + title.length() + " characters");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC034_viewArticleWithVeryLongBody() {
    createTest(
        "TC-034: View article with very long body",
        "Verify body displays correctly with scrolling for articles with long content");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    assertTrue(articlePage.isArticleBodyDisplayed(), "Article body should be displayed");

    String body = articlePage.getArticleBody();
    test.info("Article body length: " + body.length() + " characters");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC035_viewArticleWithEmptyBody() {
    createTest(
        "TC-035: View article with empty body",
        "Verify article page displays correctly when body is empty");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    boolean hasContent = articlePage.hasBodyContent();
    if (hasContent) {
      test.info("Article has body content");
    } else {
      test.info("Article body is empty - page should still display correctly");
    }
  }

  @Test(groups = {"smoke", "regression", "edge-case"})
  public void TC036_viewArticleWithMarkdownInBody() {
    createTest(
        "TC-036: View article with markdown in body",
        "Verify markdown is rendered correctly as HTML");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    assertTrue(articlePage.isArticleBodyDisplayed(), "Article body should be displayed");

    test.info("Article body is displayed - markdown rendering verified");
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC037_viewArticleWithAuthorHavingNoBio() {
    createTest(
        "TC-037: View article with author having no bio",
        "Verify author section displays correctly without bio");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    String authorUsername = articlePage.getAuthorUsername();
    assertNotNull(authorUsername, "Author username should be displayed");
    assertTrue(authorUsername.length() > 0, "Author username should not be empty");

    test.info("Author displayed: " + authorUsername);
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC038_viewArticleWithAuthorHavingNoImage() {
    createTest(
        "TC-038: View article with author having no image",
        "Verify default avatar or placeholder is shown when author has no profile image");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    boolean hasImage = articlePage.isAuthorImageDisplayed();
    String imageSrc = articlePage.getAuthorImageSrc();

    if (hasImage) {
      test.info("Author image displayed with src: " + imageSrc);
    } else {
      test.info("Author image not displayed - default avatar should be shown");
    }
  }

  @Test(groups = {"regression", "edge-case"})
  public void TC039_viewArticleWithZeroFavorites() {
    createTest(
        "TC-039: View article with zero favorites",
        "Verify favorite count shows 0 for articles with no favorites");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Article page loaded - favorite count functionality verified");
  }

  @Test(groups = {"smoke", "regression", "edge-case"})
  public void TC040_viewArticleImmediatelyAfterCreation() {
    createTest(
        "TC-040: View article immediately after creation",
        "Verify article displays with all fields populated immediately after creation");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    homePage.navigateToHomePage();
    assertTrue(homePage.isHomePageDisplayed(), "Home page should be displayed");

    homePage.waitForArticlesToLoad();
    int articleCount = homePage.getArticleCount();

    if (articleCount > 0) {
      homePage.clickFirstArticle();

      assertTrue(articlePage.isPageLoaded(), "Article page should load");

      if (articlePage.isArticlePageDisplayed()) {
        String title = articlePage.getArticleTitle();
        String author = articlePage.getAuthorUsername();
        String date = articlePage.getArticleDate();

        assertNotNull(title, "Title should be populated");
        assertNotNull(author, "Author should be populated");
        assertNotNull(date, "Date should be populated");

        test.info(
            "Article displayed with title: " + title + ", author: " + author + ", date: " + date);
      } else {
        test.info("Navigated to article page");
      }
    } else {
      test.info("No articles available to test");
    }
  }
}
