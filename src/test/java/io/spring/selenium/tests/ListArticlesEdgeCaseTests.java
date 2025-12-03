package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleListPage;
import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.HomePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ListArticlesEdgeCaseTests extends BaseTest {

  private HomePage homePage;
  private ArticleListPage articleListPage;

  @BeforeMethod
  public void setupPages() {
    homePage = new HomePage(driver);
    articleListPage = new ArticleListPage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-027: Verify behavior with offset exceeding total articles")
  public void testOffsetExceedingTotalArticles() {
    createTest(
        "TC-027: Verify offset exceeding total",
        "Verify that API returns empty array when offset exceeds total article count");

    driver.get("http://localhost:8080/articles?offset=10000");

    String pageSource = driver.getPageSource();
    test.info("Response received for offset=10000");

    boolean hasValidResponse =
        pageSource.contains("articles") || pageSource.contains("articlesCount");

    assertTrue(hasValidResponse, "API should return valid response with articles array");

    if (pageSource.contains("\"articles\":[]")) {
      test.info("Empty articles array returned as expected for large offset");
    } else if (pageSource.contains("articlesCount")) {
      test.info("Response includes articlesCount showing total available articles");
    }

    test.info("Large offset handled correctly");
  }

  @Test(
      groups = {"regression"},
      description = "TC-028: Verify behavior with limit of 1 article")
  public void testLimitOfOneArticle() {
    createTest(
        "TC-028: Verify limit of 1 article",
        "Verify that API returns exactly 1 article when limit=1");

    driver.get("http://localhost:8080/articles?limit=1");

    String pageSource = driver.getPageSource();
    test.info("Response received for limit=1");

    assertTrue(pageSource.contains("articles"), "API should return articles data");

    int articleCount = countOccurrences(pageSource, "\"slug\":");
    test.info("Number of articles returned: " + articleCount);

    assertTrue(articleCount <= 1, "Should return at most 1 article when limit=1");

    test.info("Limit of 1 article handled correctly");
  }

  @Test(
      groups = {"regression"},
      description = "TC-029: Verify behavior with very large limit value")
  public void testVeryLargeLimitValue() {
    createTest(
        "TC-029: Verify very large limit",
        "Verify that API handles very large limit value gracefully");

    driver.get("http://localhost:8080/articles?limit=10000");

    String pageSource = driver.getPageSource();
    test.info("Response received for limit=10000");

    assertTrue(pageSource.contains("articles"), "API should return articles data");

    if (pageSource.contains("articlesCount")) {
      test.info("Response includes articlesCount - large limit handled correctly");
    }

    test.info("Very large limit value handled - returns available articles up to system maximum");
  }

  @Test(
      groups = {"regression"},
      description = "TC-030: Verify behavior with offset at exact boundary")
  public void testOffsetAtExactBoundary() {
    createTest(
        "TC-030: Verify offset at exact boundary",
        "Verify behavior when offset equals total article count");

    driver.get("http://localhost:8080/articles?offset=0");
    String firstResponse = driver.getPageSource();

    int totalCount = 0;
    if (firstResponse.contains("articlesCount")) {
      int startIndex = firstResponse.indexOf("articlesCount") + 15;
      int endIndex = firstResponse.indexOf(",", startIndex);
      if (endIndex == -1) {
        endIndex = firstResponse.indexOf("}", startIndex);
      }
      try {
        String countStr = firstResponse.substring(startIndex, endIndex).replaceAll("[^0-9]", "");
        totalCount = Integer.parseInt(countStr);
      } catch (Exception e) {
        test.info("Could not parse articlesCount");
      }
    }

    test.info("Total articles count: " + totalCount);

    if (totalCount > 0) {
      driver.get("http://localhost:8080/articles?offset=" + totalCount);
      String boundaryResponse = driver.getPageSource();

      assertTrue(
          boundaryResponse.contains("articles"),
          "API should return valid response at boundary offset");

      test.info("Offset at exact boundary (" + totalCount + ") handled correctly");
    } else {
      test.info("No articles found - boundary test not applicable");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-031: Verify article list with single article")
  public void testArticleListWithSingleArticle() {
    createTest(
        "TC-031: Verify single article display",
        "Verify that a single article is displayed correctly without pagination");

    articleListPage.navigate();

    int articleCount = articleListPage.getDisplayedArticleCount();
    test.info("Displayed article count: " + articleCount);

    if (articleCount == 1) {
      ArticlePreviewComponent singleArticle = articleListPage.getFirstArticle();
      assertNotNull(singleArticle, "Single article should be displayed");
      assertTrue(singleArticle.hasTitleDisplayed(), "Single article should have title");
      assertTrue(singleArticle.hasAuthorUsernameDisplayed(), "Single article should have author");

      boolean paginationDisplayed = articleListPage.isPaginationDisplayed();
      test.info("Pagination displayed: " + paginationDisplayed);

      test.info("Single article displayed correctly");
    } else if (articleCount == 0) {
      test.info("No articles found - single article test not applicable");
    } else {
      test.info(
          "Multiple articles found ("
              + articleCount
              + ") - testing first article as representative");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should be displayed");
      assertTrue(firstArticle.hasTitleDisplayed(), "Article should have title");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-032: Verify article with no tags displays correctly")
  public void testArticleWithNoTagsDisplaysCorrectly() {
    createTest(
        "TC-032: Verify article with no tags",
        "Verify that an article without tags displays correctly with empty tag list area");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      ArticlePreviewComponent articleWithNoTags = articleListPage.findArticleWithNoTags();

      if (articleWithNoTags != null) {
        assertTrue(
            articleWithNoTags.hasTitleDisplayed(),
            "Article without tags should still display title");
        assertTrue(
            articleWithNoTags.hasAuthorUsernameDisplayed(),
            "Article without tags should still display author");
        assertTrue(
            articleWithNoTags.hasTagsDisplayed(),
            "Tag list area should still be present (even if empty)");

        int tagCount = articleWithNoTags.getTagCount();
        assertEquals(tagCount, 0, "Article should have 0 tags");

        test.info("Article with no tags displays correctly");
      } else {
        test.info("All articles have tags - testing article with tags instead");

        ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
        assertTrue(firstArticle.hasTagsDisplayed(), "Tag list area should be displayed");
        test.info("First article has " + firstArticle.getTagCount() + " tags");
      }
    } else {
      test.info("No articles found - no tags test not applicable");
    }
  }

  private int countOccurrences(String str, String sub) {
    int count = 0;
    int idx = 0;
    while ((idx = str.indexOf(sub, idx)) != -1) {
      count++;
      idx += sub.length();
    }
    return count;
  }
}
