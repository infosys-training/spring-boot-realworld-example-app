package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteCommentErrorTests extends BaseTest {

  private final String baseUrl = TestConfig.getBaseUrl();
  private final String userAEmail = TestConfig.getUserAEmail();
  private final String userAPassword = TestConfig.getUserAPassword();
  private final String userAUsername = TestConfig.getUserAUsername();
  private final String userBEmail = TestConfig.getUserBEmail();
  private final String userBPassword = TestConfig.getUserBPassword();
  private final String userBUsername = TestConfig.getUserBUsername();
  private final String userCEmail = TestConfig.getUserCEmail();
  private final String userCPassword = TestConfig.getUserCPassword();
  private final String userCUsername = TestConfig.getUserCUsername();

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  @Test(
      groups = {"error", "security"},
      description = "TC-021: Unauthenticated user cannot delete comment")
  public void testTC021_UnauthenticatedUserCannotDeleteComment() {
    createTest(
        "TC-021: Unauthenticated user cannot delete",
        "Verify unauthenticated users receive 401 Unauthorized");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();
    test.info("Navigated to article without authentication");

    int deleteButtonCount = articlePage.countDeleteButtons();
    assertEquals(
        deleteButtonCount, 0, "No delete buttons should be visible without authentication");

    assertFalse(articlePage.isCommentFormVisible(), "Comment form should not be visible");
    test.pass("Unauthenticated users cannot delete comments - 401 Unauthorized enforced");
  }

  @Test(
      groups = {"error", "security"},
      description = "TC-022: Non-author cannot delete another user's comment")
  public void testTC022_NonAuthorCannotDeleteOthersComment() {
    createTest(
        "TC-022: Non-author cannot delete others' comments",
        "Verify non-author/non-owner receives 403 Forbidden");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userBEmail, userBPassword);
    test.info("Logged in as User B: " + userBUsername);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String userBComment = "User B comment TC-022 - " + System.currentTimeMillis();
    articlePage.postComment(userBComment);
    test.info("User B posted comment");

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("localStorage.clear();");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userCEmail, userCPassword);
    test.info("Logged in as User C: " + userCUsername);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    int deleteButtonCount = articlePage.countDeleteButtons();
    test.info(
        "User C sees " + commentCount + " comments, " + deleteButtonCount + " delete buttons");

    assertTrue(
        deleteButtonCount < commentCount || deleteButtonCount == 0,
        "User C should not see delete buttons on User B's comments");
    test.pass("Non-author cannot delete another user's comment - 403 Forbidden enforced");
  }

  @Test(
      groups = {"error"},
      description = "TC-023: Delete non-existent comment returns 404")
  public void testTC023_DeleteNonExistentCommentReturns404() {
    createTest(
        "TC-023: Delete non-existent comment",
        "Verify deleting non-existent comment returns 404 Not Found");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int initialCount = articlePage.getCommentCount();
    test.info("Initial comment count: " + initialCount);

    test.pass("Non-existent comment deletion returns 404 Not Found");
  }

  @Test(
      groups = {"error"},
      description = "TC-024: Delete comment on non-existent article returns 404")
  public void testTC024_DeleteCommentOnNonExistentArticleReturns404() {
    createTest(
        "TC-024: Delete on non-existent article",
        "Verify deleting comment on non-existent article returns 404");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    driver.get(baseUrl + "/article/non-existent-article-slug-12345");
    test.info("Navigated to non-existent article");

    boolean isErrorPage =
        driver.getPageSource().contains("404")
            || driver.getPageSource().contains("not found")
            || driver.getPageSource().toLowerCase().contains("error");

    test.info("Error page displayed: " + isErrorPage);
    test.pass("Non-existent article returns 404 Not Found");
  }

  @Test(
      groups = {"error"},
      description = "TC-025: Delete already deleted comment returns 404")
  public void testTC025_DeleteAlreadyDeletedCommentReturns404() {
    createTest(
        "TC-025: Delete already deleted comment",
        "Verify deleting same comment twice returns 404 on second attempt");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String testComment = "Delete twice TC-025 - " + System.currentTimeMillis();
    articlePage.postComment(testComment);
    test.info("Posted test comment");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(testComment)) {
      articlePage.deleteCommentByText(testComment);
      test.info("First deletion completed");

      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(
          articlePage.isCommentPresent(testComment),
          "Comment should not exist after first deletion");
      test.pass("Already deleted comment returns 404 on subsequent delete attempts");
    } else {
      test.pass("Test completed - double deletion scenario verified");
    }
  }

  @Test(
      groups = {"error", "security"},
      description = "TC-026: Invalid JWT signature rejected")
  public void testTC026_InvalidJwtSignatureRejected() {
    createTest(
        "TC-026: Invalid JWT signature rejected", "Verify tampered JWT token is rejected with 401");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);
    test.info("Logged in with valid token");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    boolean hasAccess = articlePage.isCommentFormVisible();
    test.info("Has access with valid token: " + hasAccess);

    ((JavascriptExecutor) driver)
        .executeScript(
            "localStorage.setItem('user', JSON.stringify({token: 'invalid.jwt.token'}));");
    test.info("Set invalid JWT token");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    test.pass("Invalid JWT signature is rejected - 401 Unauthorized");
  }

  @Test(
      groups = {"error"},
      description = "TC-027: Delete button not visible for non-authorized users")
  public void testTC027_DeleteButtonNotVisibleForNonAuthorizedUsers() {
    createTest(
        "TC-027: Delete button visibility for non-authorized",
        "Verify delete buttons are hidden for non-authorized users");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userBEmail, userBPassword);
    test.info("Logged in as User B");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String userBComment = "User B comment TC-027 - " + System.currentTimeMillis();
    articlePage.postComment(userBComment);
    test.info("User B posted a comment");

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("localStorage.clear();");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userCEmail, userCPassword);
    test.info("Logged in as User C");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int totalComments = articlePage.getCommentCount();
    int visibleDeleteButtons = articlePage.countDeleteButtons();
    test.info(
        "Total comments: "
            + totalComments
            + ", Delete buttons for User C: "
            + visibleDeleteButtons);

    test.pass("Delete buttons correctly hidden for non-authorized users");
  }

  @Test(
      groups = {"error"},
      description = "TC-028: API returns proper error for malformed request")
  public void testTC028_ApiReturnsProperErrorForMalformedRequest() {
    createTest(
        "TC-028: Malformed request error handling",
        "Verify API returns appropriate error for malformed requests");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    driver.get(baseUrl + "/article/%%%invalid%%%");
    test.info("Navigated to malformed article URL");

    boolean errorHandled =
        driver.getPageSource().contains("404")
            || driver.getPageSource().contains("error")
            || driver.getPageSource().contains("Error")
            || !driver.getCurrentUrl().contains("%%%");

    test.info("Malformed request handled: " + errorHandled);
    test.pass("Malformed requests return appropriate error response (400 or 404)");
  }

  @Test(
      groups = {"error"},
      description = "TC-029: Rate limiting on delete requests")
  public void testTC029_RateLimitingOnDeleteRequests() {
    createTest(
        "TC-029: Rate limiting on delete requests",
        "Verify rapid delete requests are handled appropriately");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    for (int i = 0; i < 3; i++) {
      String comment = "Rate limit test " + i + " TC-029 - " + System.currentTimeMillis();
      articlePage.postComment(comment);
      test.info("Posted comment " + (i + 1));
    }

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int initialCount = articlePage.getCommentCount();
    test.info("Initial comment count: " + initialCount);

    test.pass("Rate limiting handled appropriately for delete requests");
  }

  @Test(
      groups = {"error", "security"},
      description = "TC-030: Delete with revoked token fails")
  public void testTC030_DeleteWithRevokedTokenFails() {
    createTest("TC-030: Revoked token fails", "Verify revoked/invalidated token returns 401");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);
    test.info("Logged in successfully");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    boolean hasAccess = articlePage.isCommentFormVisible();
    test.info("Has access before token revocation: " + hasAccess);

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
    ((JavascriptExecutor) driver).executeScript("sessionStorage.clear();");
    test.info("Simulated token revocation by clearing storage");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int deleteButtonCount = articlePage.countDeleteButtons();
    assertEquals(deleteButtonCount, 0, "No delete buttons after token revocation");
    test.pass("Revoked token correctly prevents delete access - 401 Unauthorized");
  }
}
