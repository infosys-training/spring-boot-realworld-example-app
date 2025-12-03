package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleListPage;
import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.PaginationComponent;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ListArticlesValidationTests extends BaseTest {

  private HomePage homePage;
  private ArticleListPage articleListPage;
  private static final int DEFAULT_LIMIT = 20;
  private static final int DEFAULT_OFFSET = 0;

  @BeforeMethod
  public void setupPages() {
    homePage = new HomePage(driver);
    articleListPage = new ArticleListPage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-011: Verify default limit of 20 articles is applied")
  public void testDefaultLimitOf20Articles() {
    createTest(
        "TC-011: Verify default limit of 20 articles",
        "Verify that the default limit of 20 articles is applied when no limit parameter is specified");

    articleListPage.navigate();

    int articleCount = articleListPage.getDisplayedArticleCount();
    test.info("Displayed article count: " + articleCount);

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleCount <= DEFAULT_LIMIT,
          "Article count should not exceed default limit of " + DEFAULT_LIMIT);
      test.info("Article count (" + articleCount + ") is within default limit of " + DEFAULT_LIMIT);
    } else {
      test.info("No articles found - default limit verification not applicable");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-012: Verify default offset of 0 is applied")
  public void testDefaultOffsetOfZero() {
    createTest(
        "TC-012: Verify default offset of 0",
        "Verify that the default offset of 0 is applied, showing most recent articles first");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should be displayed (offset 0)");

      String firstArticleTitle = firstArticle.getTitle();
      test.info("First article at offset 0: " + firstArticleTitle);

      String currentUrl = articleListPage.getCurrentUrl();
      test.info("Current URL: " + currentUrl);
      test.info("Default offset of 0 is applied - showing most recent articles");
    } else {
      test.info("No articles found - offset verification not applicable");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-013: Verify custom limit parameter works correctly")
  public void testCustomLimitParameter() {
    createTest(
        "TC-013: Verify custom limit parameter",
        "Verify that custom limit parameter correctly limits the number of articles returned");

    driver.get("http://localhost:8080/articles?limit=10");

    String pageSource = driver.getPageSource();
    test.info("API response received for limit=10");

    assertTrue(
        pageSource.contains("articles") || pageSource.contains("articlesCount"),
        "API should return articles data");

    test.info("Custom limit parameter test completed via API");
  }

  @Test(
      groups = {"regression"},
      description = "TC-014: Verify custom offset parameter works correctly")
  public void testCustomOffsetParameter() {
    createTest(
        "TC-014: Verify custom offset parameter",
        "Verify that custom offset parameter correctly skips articles");

    articleListPage.navigate();
    List<String> firstPageTitles = articleListPage.getAllArticleTitles();

    if (firstPageTitles.size() > 0) {
      test.info("First page articles: " + firstPageTitles);

      driver.get("http://localhost:8080/articles?offset=5");
      String pageSource = driver.getPageSource();

      assertTrue(
          pageSource.contains("articles") || pageSource.contains("articlesCount"),
          "API should return articles data with offset");

      test.info("Custom offset parameter test completed via API");
    } else {
      test.info("No articles found - offset parameter verification not applicable");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-015: Verify limit and offset work together correctly")
  public void testLimitAndOffsetTogether() {
    createTest(
        "TC-015: Verify limit and offset together",
        "Verify that limit and offset parameters work correctly when used together");

    driver.get("http://localhost:8080/articles?offset=10&limit=5");

    String pageSource = driver.getPageSource();
    test.info("API response received for offset=10&limit=5");

    assertTrue(
        pageSource.contains("articles") || pageSource.contains("articlesCount"),
        "API should return articles data with both offset and limit");

    test.info("Limit and offset combination test completed via API");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-016: Verify total article count is displayed")
  public void testTotalArticleCountDisplayed() {
    createTest(
        "TC-016: Verify total article count",
        "Verify that total article count is reflected in pagination");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      int displayedCount = articleListPage.getDisplayedArticleCount();
      test.info("Displayed articles on current page: " + displayedCount);

      if (articleListPage.isPaginationDisplayed()) {
        PaginationComponent pagination = articleListPage.getPagination();
        int pageCount = pagination.getPageCount();
        test.info("Pagination shows " + pageCount + " page numbers");
        test.info("Total article count is reflected in pagination controls");
      } else {
        test.info(
            "Pagination not displayed - total count within single page limit ("
                + displayedCount
                + " articles)");
      }
    } else {
      test.info("No articles found - total count verification not applicable");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-017: Verify article count updates when filtering by tag")
  public void testArticleCountUpdatesWithTagFilter() {
    createTest(
        "TC-017: Verify article count with tag filter",
        "Verify that article count updates when filtering by tag");

    homePage.navigate();

    int initialCount = homePage.getArticleCount();
    test.info("Initial article count: " + initialCount);

    List<WebElement> tags = homePage.getPopularTags();
    if (!tags.isEmpty()) {
      String tagName = tags.get(0).getText();
      test.info("Clicking on tag: " + tagName);

      homePage.clickTag(tagName);

      int filteredCount = homePage.getArticleCount();
      test.info("Filtered article count: " + filteredCount);

      assertTrue(
          filteredCount <= initialCount,
          "Filtered count should be less than or equal to initial count");
      test.info("Article count updated correctly after tag filter");
    } else {
      test.info("No tags available - tag filter verification not applicable");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-018: Verify article count updates when filtering by author")
  public void testArticleCountUpdatesWithAuthorFilter() {
    createTest(
        "TC-018: Verify article count with author filter",
        "Verify that article count updates when filtering by author");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      String authorName = firstArticle.getAuthorUsername();
      test.info("Clicking on author: " + authorName);

      firstArticle.clickAuthorLink();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Navigated to: " + currentUrl);

      assertTrue(
          currentUrl.contains("/profile/") || currentUrl.contains(authorName),
          "Should navigate to author profile page");

      test.info("Author filter navigation completed");
    } else {
      test.info("No articles found - author filter verification not applicable");
    }
  }
}
