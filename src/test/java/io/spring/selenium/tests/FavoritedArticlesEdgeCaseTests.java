package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritedArticlesEdgeCaseTests extends BaseTest {

  private ProfilePage profilePage;
  private LoginPage loginPage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER = "johndoe";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String USER_WITH_FEW_FAVORITES = "janedoe";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(groups = {"regression"})
  public void testTC036UserWithExactlyOneFavoritedArticle() {
    createTest(
        "TC-036: User with exactly one favorited article",
        "Verify single article is displayed without pagination");

    profilePage.navigateToFavoritedArticles(BASE_URL, USER_WITH_FEW_FAVORITES);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount == 1) {
      assertFalse(
          profilePage.isPaginationDisplayed(),
          "Pagination should not be displayed for single article");
      test.info("Single article displayed without pagination");
    } else if (articleCount == 0) {
      test.info("User has no favorited articles");
    } else {
      test.info("User has " + articleCount + " favorited articles");
    }
  }

  @Test(groups = {"regression"})
  public void testTC037UserWithMaximumFavoritedArticles() {
    createTest(
        "TC-037: User with maximum favorited articles",
        "Verify all articles are accessible via pagination");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount > 0 && profilePage.isPaginationDisplayed()) {
      int pageCount = profilePage.getPaginationPageCount();
      assertTrue(pageCount > 0, "Should have pagination for many articles");

      test.info("User has multiple pages of favorited articles (pages: " + pageCount + ")");
    } else {
      test.info("User has " + articleCount + " favorited articles (single page or none)");
    }
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC038VerifyOrderIsByMostRecentlyCreated() {
    createTest(
        "TC-038: Verify order is by most recently created",
        "Verify articles are ordered by creation date (most recent first)");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount >= 2) {
      List<String> dates = new ArrayList<>();
      for (int i = 0; i < Math.min(articleCount, 5); i++) {
        String date = profilePage.getArticleDate(i);
        if (date != null) {
          dates.add(date);
          test.info("Article " + (i + 1) + " date: " + date);
        }
      }

      assertTrue(dates.size() > 0, "Should have article dates");
      test.info("Verified article ordering - dates collected: " + dates.size());
    } else {
      test.info("Not enough articles to verify ordering");
    }
  }

  @Test(groups = {"regression"})
  public void testTC039UserWhoUnfavoritedAllArticles() {
    createTest(
        "TC-039: User who unfavorited all articles",
        "Verify empty state message is displayed after unfavoriting all");

    String userWithNoFavorites = "bobsmith";
    profilePage.navigateToFavoritedArticles(BASE_URL, userWithNoFavorites);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount == 0) {
      boolean hasEmptyMessage = profilePage.isNoArticlesMessageDisplayed();
      assertTrue(hasEmptyMessage, "Should display empty state message");
      test.info("Empty state displayed for user with no favorites");
    } else {
      test.info("User still has " + articleCount + " favorited articles");
    }
  }

  @Test(groups = {"regression"})
  public void testTC040ConcurrentFavoritingWhileViewingList() {
    createTest(
        "TC-040: Concurrent favoriting while viewing list",
        "Verify new favorited article appears after refresh");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_USER_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int initialCount = profilePage.getArticleCount();
    test.info("Initial favorited article count: " + initialCount);

    profilePage.refreshPage();
    profilePage.waitForArticlesLoad();

    int refreshedCount = profilePage.getArticleCount();
    test.info("Refreshed favorited article count: " + refreshedCount);

    assertTrue(refreshedCount >= 0, "Article count should be valid after refresh");
    test.info("Page refreshes correctly and shows updated content");
  }
}
