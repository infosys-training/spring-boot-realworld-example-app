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
 * Error handling test cases for Delete Article functionality. Tests error responses and UI
 * feedback.
 */
public class DeleteArticleErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;

  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";

  private static final String OTHER_USER_EMAIL = "jane@example.com";
  private static final String OTHER_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
  }

  @Test(groups = {"regression", "error-handling", "delete-article"})
  public void testTC035_VerifyErrorResponseFormatOn403() {
    createTest(
        "TC-035: Verify error response format on 403",
        "Verify proper error handling when non-author attempts delete");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Error Format TC035 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    loginAsUser(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for non-author (403 scenario)");

    assertTrue(articlePage.isArticleLoaded(), "Article should still be viewable");

    test.pass("403 Forbidden scenario handled correctly - delete button hidden");
  }

  @Test(groups = {"regression", "error-handling", "delete-article"})
  public void testTC036_VerifyErrorResponseFormatOn404() {
    createTest(
        "TC-036: Verify error response format on 404",
        "Verify proper error handling for non-existent article");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String nonExistentSlug = "non-existent-article-tc036-" + System.currentTimeMillis();
    articlePage.navigateTo(nonExistentSlug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleNotFound() || !articlePage.isArticleLoaded(),
        "Non-existent article should show 404 error");

    assertFalse(
        articlePage.isDeleteButtonVisible(), "Delete button should not be visible for 404 page");

    test.pass("404 Not Found error handled correctly");
  }

  @Test(groups = {"regression", "error-handling", "delete-article"})
  public void testTC037_VerifyErrorResponseFormatOn401() {
    createTest(
        "TC-037: Verify error response format on 401",
        "Verify proper error handling for unauthenticated request");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Error Format TC037 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    logout();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(articlePage.isArticleLoaded(), "Article should be viewable without auth");

    assertFalse(
        articlePage.isDeleteButtonVisible(),
        "Delete button should not be visible for unauthenticated user (401 scenario)");

    test.pass("401 Unauthorized scenario handled correctly - delete button hidden");
  }

  @Test(groups = {"regression", "error-handling", "delete-article"})
  public void testTC038_DeleteArticleResponseTimeValidation() {
    createTest(
        "TC-038: Delete article response time validation",
        "Verify delete operation completes within acceptable time");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Response Time TC038 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible");

    long startTime = System.currentTimeMillis();

    articlePage.deleteArticle();

    long endTime = System.currentTimeMillis();
    long responseTime = endTime - startTime;

    test.info("Delete operation response time: " + responseTime + "ms");

    assertTrue(responseTime < 10000, "Delete operation should complete within 10 seconds");

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(), "Article should be deleted");

    test.pass("Delete operation completed within acceptable time: " + responseTime + "ms");
  }

  @Test(groups = {"regression", "error-handling", "delete-article"})
  public void testTC039_DeleteArticleUIFeedbackVerification() {
    createTest(
        "TC-039: Delete article UI feedback verification",
        "Verify proper UI feedback during delete operation");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "UI Feedback TC039 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible");

    String deleteButtonText = getDeleteButtonText();
    test.info("Delete button text: " + deleteButtonText);

    assertTrue(
        deleteButtonText.toLowerCase().contains("delete"),
        "Delete button should have clear delete label");

    articlePage.clickDeleteButton();

    assertTrue(
        articlePage.isConfirmationDialogPresent(),
        "Confirmation dialog should appear before deletion");

    articlePage.acceptConfirmationDialog();

    waitForPageLoad();

    assertTrue(
        articlePage.isOnHomePage() || !articlePage.isArticleLoaded(),
        "Should redirect after successful deletion");

    test.pass("UI feedback during delete operation is correct");
  }

  @Test(groups = {"smoke", "error-handling", "delete-article"})
  public void testTC040_DeleteArticleConfirmationDialog() {
    createTest(
        "TC-040: Delete article confirmation dialog",
        "Verify confirmation dialog prevents accidental deletion");

    loginAsUser(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    String articleTitle = "Confirmation TC040 " + System.currentTimeMillis();
    createArticle(articleTitle, "Description", "Body content");

    String articleUrl = driver.getCurrentUrl();
    String slug = articleUrl.substring(articleUrl.lastIndexOf("/") + 1);

    assertTrue(articlePage.isDeleteButtonVisible(), "Delete button should be visible");

    articlePage.clickDeleteButton();

    assertTrue(
        articlePage.isConfirmationDialogPresent(), "Confirmation dialog should appear on delete");

    String dialogText = articlePage.getConfirmationDialogText();
    test.info("Confirmation dialog text: " + dialogText);

    articlePage.dismissConfirmationDialog();

    waitForPageLoad();

    articlePage.navigateTo(slug);
    articlePage.waitForPageLoad();

    assertTrue(
        articlePage.isArticleLoaded(), "Article should still exist after canceling deletion");

    test.pass("Confirmation dialog prevents accidental deletion");
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
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    driver.navigate().refresh();
    waitForPageLoad();
  }

  private String getDeleteButtonText() {
    try {
      return (String)
          ((JavascriptExecutor) driver)
              .executeScript(
                  "return document.querySelector('.btn-outline-danger')?.textContent || ''");
    } catch (Exception e) {
      return "";
    }
  }

  private void waitForPageLoad() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
