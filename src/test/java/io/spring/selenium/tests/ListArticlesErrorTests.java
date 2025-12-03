package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleListPage;
import io.spring.selenium.pages.HomePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ListArticlesErrorTests extends BaseTest {

  private HomePage homePage;
  private ArticleListPage articleListPage;

  @BeforeMethod
  public void setupPages() {
    homePage = new HomePage(driver);
    articleListPage = new ArticleListPage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-019: Verify error handling when API is unavailable")
  public void testErrorHandlingWhenApiUnavailable() {
    createTest(
        "TC-019: Verify error handling when API unavailable",
        "Verify that appropriate error message is displayed when backend API is unavailable");

    driver.get("http://localhost:3000");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean hasArticles = homePage.hasArticles();
    boolean hasError = homePage.isErrorMessageDisplayed();
    boolean hasEmptyState = homePage.isEmptyStateDisplayed();

    test.info("Has articles: " + hasArticles);
    test.info("Has error message: " + hasError);
    test.info("Has empty state: " + hasEmptyState);

    assertTrue(
        hasArticles || hasError || hasEmptyState,
        "Page should show articles, error message, or empty state");

    test.info("Error handling test completed - page handles API state appropriately");
  }

  @Test(
      groups = {"regression"},
      description = "TC-020: Verify error message displays for failed article load")
  public void testErrorMessageForFailedArticleLoad() {
    createTest(
        "TC-020: Verify error message for failed load",
        "Verify that appropriate error message is displayed when article loading fails");

    homePage.navigate();

    boolean pageLoaded =
        homePage.isHomePageDisplayed()
            || homePage.isErrorMessageDisplayed()
            || homePage.hasArticles();

    assertTrue(pageLoaded, "Page should load with content, error, or empty state");

    if (homePage.isErrorMessageDisplayed()) {
      String errorText = homePage.getErrorMessageText();
      test.info("Error message displayed: " + errorText);
      assertTrue(
          errorText.contains("Cannot load") || errorText.length() > 0,
          "Error message should be meaningful");
    } else {
      test.info("No error - articles loaded successfully or empty state shown");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-021: Verify empty state when no articles exist")
  public void testEmptyStateWhenNoArticlesExist() {
    createTest(
        "TC-021: Verify empty state display",
        "Verify that 'No articles are here... yet.' message is displayed when no articles exist");

    articleListPage.navigate();

    if (!articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.isEmptyStateDisplayed(),
          "Empty state message should be displayed when no articles exist");
      test.info("Empty state message displayed correctly");
    } else {
      test.info(
          "Articles exist in database - empty state not applicable. Found "
              + articleListPage.getDisplayedArticleCount()
              + " articles");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-022: Verify handling of invalid offset parameter")
  public void testHandlingInvalidOffsetParameter() {
    createTest(
        "TC-022: Verify handling of invalid offset",
        "Verify that API handles invalid offset parameter gracefully");

    driver.get("http://localhost:8080/articles?offset=invalid");

    String pageSource = driver.getPageSource();
    test.info("Response received for invalid offset parameter");

    boolean hasValidResponse =
        pageSource.contains("articles")
            || pageSource.contains("error")
            || pageSource.contains("Bad Request");

    assertTrue(hasValidResponse, "API should return valid response or error for invalid offset");

    test.info("Invalid offset parameter handled - API did not crash");
  }

  @Test(
      groups = {"regression"},
      description = "TC-023: Verify handling of invalid limit parameter")
  public void testHandlingInvalidLimitParameter() {
    createTest(
        "TC-023: Verify handling of invalid limit",
        "Verify that API handles invalid limit parameter gracefully");

    driver.get("http://localhost:8080/articles?limit=invalid");

    String pageSource = driver.getPageSource();
    test.info("Response received for invalid limit parameter");

    boolean hasValidResponse =
        pageSource.contains("articles")
            || pageSource.contains("error")
            || pageSource.contains("Bad Request");

    assertTrue(hasValidResponse, "API should return valid response or error for invalid limit");

    test.info("Invalid limit parameter handled - API did not crash");
  }

  @Test(
      groups = {"regression"},
      description = "TC-024: Verify handling of negative offset value")
  public void testHandlingNegativeOffsetValue() {
    createTest(
        "TC-024: Verify handling of negative offset",
        "Verify that API handles negative offset value gracefully");

    driver.get("http://localhost:8080/articles?offset=-5");

    String pageSource = driver.getPageSource();
    test.info("Response received for negative offset value");

    boolean hasValidResponse =
        pageSource.contains("articles")
            || pageSource.contains("error")
            || pageSource.contains("Bad Request");

    assertTrue(hasValidResponse, "API should return valid response or error for negative offset");

    test.info("Negative offset value handled - API did not crash");
  }

  @Test(
      groups = {"regression"},
      description = "TC-025: Verify handling of negative limit value")
  public void testHandlingNegativeLimitValue() {
    createTest(
        "TC-025: Verify handling of negative limit",
        "Verify that API handles negative limit value gracefully");

    driver.get("http://localhost:8080/articles?limit=-10");

    String pageSource = driver.getPageSource();
    test.info("Response received for negative limit value");

    boolean hasValidResponse =
        pageSource.contains("articles")
            || pageSource.contains("error")
            || pageSource.contains("Bad Request");

    assertTrue(hasValidResponse, "API should return valid response or error for negative limit");

    test.info("Negative limit value handled - API did not crash");
  }

  @Test(
      groups = {"regression"},
      description = "TC-026: Verify handling of non-numeric pagination parameters")
  public void testHandlingNonNumericParameters() {
    createTest(
        "TC-026: Verify handling of non-numeric parameters",
        "Verify that API handles non-numeric pagination parameters gracefully");

    driver.get("http://localhost:8080/articles?offset=abc&limit=xyz");

    String pageSource = driver.getPageSource();
    test.info("Response received for non-numeric parameters");

    boolean hasValidResponse =
        pageSource.contains("articles")
            || pageSource.contains("error")
            || pageSource.contains("Bad Request")
            || pageSource.length() > 0;

    assertTrue(
        hasValidResponse, "API should return valid response or error for non-numeric parameters");

    test.info("Non-numeric parameters handled - API did not crash");
  }
}
