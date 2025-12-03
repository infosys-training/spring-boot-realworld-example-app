package io.spring.selenium.tests.deletearticle;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.EditorPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.tests.BaseTest;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Authorization test cases for Delete Article functionality. Tests scenarios involving
 * authentication and authorization failures.
 */
public class DeleteArticleAuthorizationTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;

  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";
  private static final String AUTHOR_USERNAME = "johndoe";

  private static final String OTHER_USER_EMAIL = "jane@example.com";
  private static final String OTHER_USER_PASSWORD = "password123";
  private static final String OTHER_USER_USERNAME = "janedoe";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
  }

  @Test(groups = {"smoke", "authorization", "delete-article"})
  public void testTC009_DeleteArticleAsNonAuthor_Verify403Forbidden() {
    createTest(
        "TC-009: Delete article as non-author - verify 403 Forbidden",
        "Verify non-author cannot delete another user's article");

    String articleTitle = "Author Article TC009 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    loginAsUser(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(), "Delete button should not be visible for non-author");

    test.pass("Non-author cannot see delete button - 403 Forbidden behavior verified");
  }

  @Test(groups = {"smoke", "authorization", "delete-article"})
  public void testTC010_DeleteArticleAsDifferentAuthenticatedUser() {
    createTest(
        "TC-010: Delete article as different authenticated user",
        "Verify different authenticated user cannot delete article");

    String articleTitle = "Author Article TC010 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    loginAsUser(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(articlePage.isArticleLoaded(), "Article should be visible to other users");
    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for different user");
    assertFalse(
        articlePage.isEditButtonVisible(), "Edit button should not be visible for different user");

    test.pass("Different authenticated user cannot delete article");
  }

  @Test(groups = {"smoke", "authorization", "delete-article"})
  public void testTC011_DeleteArticleWithoutAuthentication_Verify401() {
    createTest(
        "TC-011: Delete article without authentication - verify 401",
        "Verify unauthenticated user cannot delete article");

    String articleTitle = "Author Article TC011 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for unauthenticated user");

    test.pass("Unauthenticated user cannot see delete button - 401 behavior verified");
  }

  @Test(groups = {"regression", "authorization", "delete-article"})
  public void testTC012_DeleteArticleWithExpiredToken() {
    createTest(
        "TC-012: Delete article with expired token",
        "Verify expired token results in 401 Unauthorized");

    String articleTitle = "Author Article TC012 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    clearLocalStorage();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible after token cleared");

    test.pass("Expired/cleared token prevents article deletion");
  }

  @Test(groups = {"regression", "authorization", "delete-article"})
  public void testTC013_DeleteArticleWithInvalidToken() {
    createTest(
        "TC-013: Delete article with invalid token",
        "Verify invalid token results in 401 Unauthorized");

    String articleTitle = "Author Article TC013 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    setInvalidToken();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible with invalid token");

    test.pass("Invalid token prevents article deletion");
  }

  @Test(groups = {"regression", "authorization", "delete-article"})
  public void testTC014_DeleteArticleWithMalformedAuthorizationHeader() {
    createTest(
        "TC-014: Delete article with malformed authorization header",
        "Verify malformed auth header results in 401 Unauthorized");

    String articleTitle = "Author Article TC014 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    setMalformedToken();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible with malformed token");

    test.pass("Malformed authorization header prevents article deletion");
  }

  @Test(groups = {"regression", "authorization", "delete-article"})
  public void testTC015_DeleteArticleWithEmptyAuthorizationHeader() {
    createTest(
        "TC-015: Delete article with empty authorization header",
        "Verify empty auth header results in 401 Unauthorized");

    String articleTitle = "Author Article TC015 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    clearLocalStorage();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(), "Delete button should not be visible with empty auth");

    test.pass("Empty authorization header prevents article deletion");
  }

  @Test(groups = {"regression", "authorization", "delete-article"})
  public void testTC016_DeleteArticleAfterAuthorLogsOut() {
    createTest(
        "TC-016: Delete article after author logs out",
        "Verify logged out author cannot delete article");

    String articleTitle = "Author Article TC016 " + System.currentTimeMillis();
    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(), "Delete button should not be visible after logout");

    test.pass("Logged out author cannot delete article");
  }

  private void loginAsUser(String email, String password) {
    loginPage.navigateTo();
    loginPage.login(email, password);
    waitForPageLoad();
  }

  private void createArticle(String title, String description, String body) {
    editorPage.navigateToNewArticle();
    editorPage.createArticle(title, description, body);
    waitForPageLoad();
  }

  private void logout() {
    clearLocalStorage();
    driver.navigate().refresh();
    waitForPageLoad();
  }

  private void clearLocalStorage() {
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
  }

  private void setInvalidToken() {
    ((JavascriptExecutor) driver)
        .executeScript(
            "window.localStorage.setItem('user', JSON.stringify({token: 'invalid_token_12345'}));");
  }

  private void setMalformedToken() {
    ((JavascriptExecutor) driver)
        .executeScript("window.localStorage.setItem('user', 'malformed_json_data');");
  }

  private void waitForPageLoad() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
