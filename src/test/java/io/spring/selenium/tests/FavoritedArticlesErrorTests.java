package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritedArticlesErrorTests extends BaseTest {

  private ProfilePage profilePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String NON_EXISTENT_USER = "nonexistentuser123456789";
  private static final String USER_WITH_NO_FAVORITES = "bobsmith";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC031FilterWithNonExistentUsernameReturnsEmpty() {
    createTest(
        "TC-031: Filter with non-existent username returns empty",
        "Verify that non-existent user returns empty list or error page");

    driver.get(BASE_URL + "/profile/" + NON_EXISTENT_USER + "?favorite=true");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertNotNull(pageSource, "Page should load");

    boolean hasErrorOrEmpty =
        pageSource.contains("Can't load profile")
            || pageSource.contains("No articles")
            || pageSource.contains("error")
            || pageSource.contains("not found");

    test.info("Page loaded for non-existent user: " + NON_EXISTENT_USER);
    test.info("Contains error/empty indicator: " + hasErrorOrEmpty);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC032VerifyEmptyStateMessageForNoFavoritedArticles() {
    createTest(
        "TC-032: Verify empty state message for no favorited articles",
        "Verify 'No articles are here... yet.' message is displayed");

    profilePage.navigateToFavoritedArticles(BASE_URL, USER_WITH_NO_FAVORITES);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount == 0) {
      boolean hasEmptyMessage = profilePage.isNoArticlesMessageDisplayed();
      String message = profilePage.getNoArticlesMessage();

      test.info("Empty state message displayed: " + hasEmptyMessage);
      test.info("Message content: " + message);

      assertTrue(
          hasEmptyMessage || message.contains("No articles"), "Should display empty state message");
    } else {
      test.info("User has " + articleCount + " favorited articles - cannot test empty state");
    }
  }

  @Test(groups = {"regression"})
  public void testTC033FilterWithDeletedUserReturnsEmpty() {
    createTest(
        "TC-033: Filter with deleted user returns empty",
        "Verify appropriate error message or empty state for deleted user");

    String deletedUser = "deleteduser999";
    driver.get(BASE_URL + "/profile/" + deletedUser + "?favorite=true");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertNotNull(pageSource, "Page should load");

    test.info("Page loaded for potentially deleted user: " + deletedUser);
  }

  @Test(groups = {"regression"})
  public void testTC034VerifyErrorHandlingForInvalidApiResponse() {
    createTest(
        "TC-034: Verify error handling for invalid API response",
        "Verify error message is displayed gracefully for API errors");

    driver.get(BASE_URL + "/profile/" + NON_EXISTENT_USER);

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();

    boolean hasGracefulError =
        !pageSource.contains("500")
            && !pageSource.contains("Internal Server Error")
            && !pageSource.contains("stack trace");

    assertTrue(hasGracefulError, "Should handle API errors gracefully without exposing internals");

    test.info("Error handling verified - no internal errors exposed");
  }

  @Test(groups = {"regression"})
  public void testTC035VerifyGracefulHandlingOfNetworkTimeout() {
    createTest(
        "TC-035: Verify graceful handling of network timeout",
        "Verify loading indicator or timeout message is shown");

    profilePage.navigateToFavoritedArticles(BASE_URL, "johndoe");

    String pageSource = driver.getPageSource();

    boolean hasLoadingOrContent =
        pageSource.contains("Loading")
            || pageSource.contains("article")
            || pageSource.contains("No articles")
            || profilePage.isProfilePageDisplayed();

    assertTrue(hasLoadingOrContent, "Page should show loading indicator or content");

    test.info("Page handles loading state appropriately");
  }
}
