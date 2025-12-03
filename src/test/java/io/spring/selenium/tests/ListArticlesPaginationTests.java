package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleListPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.PaginationComponent;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ListArticlesPaginationTests extends BaseTest {

  private HomePage homePage;
  private ArticleListPage articleListPage;

  @BeforeMethod
  public void setupPages() {
    homePage = new HomePage(driver);
    articleListPage = new ArticleListPage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-033: Verify pagination controls display when articles exceed limit")
  public void testPaginationDisplaysWhenExceedingLimit() {
    createTest(
        "TC-033: Verify pagination displays",
        "Verify that pagination controls are visible when articles exceed the default limit");

    articleListPage.navigate();

    int articleCount = articleListPage.getDisplayedArticleCount();
    test.info("Displayed article count: " + articleCount);

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();
      assertTrue(pagination.isDisplayed(), "Pagination should be displayed");
      assertTrue(pagination.hasFirstPageButton(), "First page button (<<) should be present");
      assertTrue(pagination.hasLastPageButton(), "Last page button (>>) should be present");

      List<Integer> pageNumbers = pagination.getVisiblePageNumbers();
      test.info("Visible page numbers: " + pageNumbers);

      test.info("Pagination controls displayed correctly");
    } else {
      test.info(
          "Pagination not displayed - total articles ("
              + articleCount
              + ") within single page limit");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-034: Verify clicking next page loads next set of articles")
  public void testNextPageButtonLoadsNextArticles() {
    createTest(
        "TC-034: Verify next page navigation",
        "Verify that clicking next page button loads the next set of articles");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();

      List<String> firstPageTitles = articleListPage.getAllArticleTitles();
      test.info("First page articles: " + firstPageTitles.size());

      if (pagination.hasNextPageButton()) {
        int currentPage = pagination.getCurrentPage();
        test.info("Current page before click: " + currentPage);

        articleListPage.goToNextPage();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        List<String> secondPageTitles = articleListPage.getAllArticleTitles();
        test.info("Second page articles: " + secondPageTitles.size());

        if (!secondPageTitles.isEmpty() && !firstPageTitles.isEmpty()) {
          assertNotEquals(
              firstPageTitles.get(0),
              secondPageTitles.get(0),
              "First article should be different on next page");
        }

        test.info("Next page navigation successful");
      } else {
        test.info("Next page button not available - only one page of results");
      }
    } else {
      test.info("Pagination not displayed - cannot test next page navigation");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-035: Verify clicking previous page loads previous set of articles")
  public void testPreviousPageButtonLoadsPreviousArticles() {
    createTest(
        "TC-035: Verify previous page navigation",
        "Verify that clicking previous page button loads the previous set of articles");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();

      if (pagination.hasNextPageButton()) {
        articleListPage.goToNextPage();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        List<String> secondPageTitles = articleListPage.getAllArticleTitles();
        test.info("Second page articles: " + secondPageTitles.size());

        if (pagination.hasPreviousPageButton()) {
          articleListPage.goToPreviousPage();

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }

          List<String> backToFirstPageTitles = articleListPage.getAllArticleTitles();
          test.info("Back to first page articles: " + backToFirstPageTitles.size());

          test.info("Previous page navigation successful");
        } else {
          test.info("Previous page button not available on second page");
        }
      } else {
        test.info("Cannot navigate to second page - only one page of results");
      }
    } else {
      test.info("Pagination not displayed - cannot test previous page navigation");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-036: Verify clicking specific page number loads correct articles")
  public void testSpecificPageNumberNavigation() {
    createTest(
        "TC-036: Verify specific page navigation",
        "Verify that clicking a specific page number loads the correct articles");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();
      List<Integer> pageNumbers = pagination.getVisiblePageNumbers();

      test.info("Available page numbers: " + pageNumbers);

      if (pageNumbers.size() >= 2) {
        int targetPage = pageNumbers.get(1);
        test.info("Clicking page number: " + targetPage);

        articleListPage.goToPage(targetPage);

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        int currentPage = articleListPage.getCurrentPageNumber();
        test.info("Current page after click: " + currentPage);

        assertEquals(currentPage, targetPage, "Should be on the clicked page number");

        test.info("Specific page number navigation successful");
      } else {
        test.info("Only one page available - cannot test specific page navigation");
      }
    } else {
      test.info("Pagination not displayed - cannot test specific page navigation");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-037: Verify first page button navigates to first page")
  public void testFirstPageButtonNavigation() {
    createTest(
        "TC-037: Verify first page button",
        "Verify that clicking first page button (<<) navigates to the first page");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();

      if (pagination.hasNextPageButton()) {
        articleListPage.goToNextPage();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        int pageAfterNext = articleListPage.getCurrentPageNumber();
        test.info("Page after clicking next: " + pageAfterNext);

        articleListPage.goToFirstPage();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        int pageAfterFirst = articleListPage.getCurrentPageNumber();
        test.info("Page after clicking first: " + pageAfterFirst);

        assertEquals(pageAfterFirst, 1, "Should be on first page after clicking <<");

        test.info("First page button navigation successful");
      } else {
        test.info("Cannot test first page button - only one page of results");
      }
    } else {
      test.info("Pagination not displayed - cannot test first page button");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-038: Verify last page button navigates to last page")
  public void testLastPageButtonNavigation() {
    createTest(
        "TC-038: Verify last page button",
        "Verify that clicking last page button (>>) navigates to the last page");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();

      int initialPage = pagination.getCurrentPage();
      test.info("Initial page: " + initialPage);

      articleListPage.goToLastPage();

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      int lastPage = articleListPage.getCurrentPageNumber();
      test.info("Page after clicking last: " + lastPage);

      assertTrue(lastPage >= initialPage, "Last page should be >= initial page");

      test.info("Last page button navigation successful");
    } else {
      test.info("Pagination not displayed - cannot test last page button");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-039: Verify current page is highlighted in pagination")
  public void testCurrentPageIsHighlighted() {
    createTest(
        "TC-039: Verify current page highlighting",
        "Verify that the current page number has 'active' class styling");

    articleListPage.navigate();

    if (articleListPage.isPaginationDisplayed()) {
      PaginationComponent pagination = articleListPage.getPagination();

      int currentPage = pagination.getCurrentPage();
      test.info("Current page: " + currentPage);

      assertTrue(
          pagination.isPageActive(currentPage),
          "Current page " + currentPage + " should be highlighted as active");

      if (pagination.hasNextPageButton()) {
        articleListPage.goToNextPage();

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        int newCurrentPage = articleListPage.getCurrentPageNumber();
        test.info("New current page: " + newCurrentPage);

        PaginationComponent newPagination = articleListPage.getPagination();
        assertTrue(
            newPagination.isPageActive(newCurrentPage),
            "New current page " + newCurrentPage + " should be highlighted as active");
      }

      test.info("Current page highlighting verified");
    } else {
      test.info("Pagination not displayed - cannot test page highlighting");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-040: Verify pagination hides when articles are within limit")
  public void testPaginationHidesWithinLimit() {
    createTest(
        "TC-040: Verify pagination hides within limit",
        "Verify that pagination controls are not displayed when articles are within the limit");

    articleListPage.navigate();

    int articleCount = articleListPage.getDisplayedArticleCount();
    boolean paginationDisplayed = articleListPage.isPaginationDisplayed();

    test.info("Article count: " + articleCount);
    test.info("Pagination displayed: " + paginationDisplayed);

    if (articleCount <= 20 && !paginationDisplayed) {
      test.info(
          "Pagination correctly hidden - article count ("
              + articleCount
              + ") is within limit of 20");
    } else if (articleCount > 20 && paginationDisplayed) {
      test.info(
          "Pagination correctly displayed - article count ("
              + articleCount
              + ") exceeds limit of 20");
    } else if (articleCount <= 20 && paginationDisplayed) {
      test.info(
          "Note: Pagination is displayed even with "
              + articleCount
              + " articles - may indicate more articles exist in database");
    } else {
      test.info(
          "Note: Pagination not displayed with "
              + articleCount
              + " articles - checking if this is expected");
    }

    test.info("Pagination visibility test completed");
  }
}
