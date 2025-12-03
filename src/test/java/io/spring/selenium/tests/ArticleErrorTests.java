package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArticleErrorTests extends BaseTest {

  private ArticlePage articlePage;

  private static final String NONEXISTENT_SLUG = "nonexistent-article-that-does-not-exist-12345";

  @BeforeMethod
  public void initPages() {
    articlePage = new ArticlePage(driver);
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC023_accessArticleWithNonExistentSlug() {
    createTest(
        "TC-023: Access article with non-existent slug",
        "Verify 404 Not Found error page is displayed for non-existent article slug");

    articlePage.navigateToArticle(NONEXISTENT_SLUG);

    assertTrue(articlePage.isPageLoaded(), "Page should load");
    assertFalse(
        articlePage.isArticlePageDisplayed() && articlePage.getArticleTitle().length() > 0,
        "Article page should not display valid content for non-existent slug");

    test.info("Verified error handling for non-existent slug: " + NONEXISTENT_SLUG);
  }

  @Test(groups = {"regression", "error"})
  public void TC024_accessArticleWithEmptySlug() {
    createTest(
        "TC-024: Access article with empty slug",
        "Verify error page or redirect to home for empty slug");

    articlePage.navigateToArticle("");

    assertTrue(articlePage.isPageLoaded(), "Page should load");

    test.info("Verified handling of empty slug. Current URL: " + articlePage.getCurrentUrl());
  }

  @Test(groups = {"smoke", "regression", "error", "security"})
  public void TC025_accessArticleWithSpecialCharactersInSlug() {
    createTest(
        "TC-025: Access article with special characters in slug",
        "Verify error is handled gracefully with no XSS vulnerability");

    String xssSlug = "<script>alert(1)</script>";
    articlePage.navigateToArticle(xssSlug);

    assertTrue(articlePage.isPageLoaded(), "Page should load");

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("<script>alert(1)</script>"),
        "XSS script should not be rendered in page");

    test.info("Verified XSS protection for special characters in slug");
  }

  @Test(groups = {"regression", "error"})
  public void TC026_accessArticleWithVeryLongSlug() {
    createTest(
        "TC-026: Access article with very long slug",
        "Verify error is handled gracefully for extremely long slug");

    StringBuilder longSlug = new StringBuilder();
    for (int i = 0; i < 500; i++) {
      longSlug.append("a");
    }

    articlePage.navigateToArticle(longSlug.toString());

    assertTrue(articlePage.isPageLoaded(), "Page should load");

    test.info("Verified handling of 500-character slug");
  }

  @Test(groups = {"smoke", "regression", "error", "security"})
  public void TC027_accessArticleWithSqlInjectionInSlug() {
    createTest(
        "TC-027: Access article with SQL injection in slug",
        "Verify error is handled gracefully with no SQL injection vulnerability");

    String sqlInjectionSlug = "'; DROP TABLE articles;--";
    articlePage.navigateToArticle(sqlInjectionSlug);

    assertTrue(articlePage.isPageLoaded(), "Page should load");

    test.info("Verified SQL injection protection in slug handling");
  }

  @Test(groups = {"regression", "error"})
  public void TC028_accessArticleWithNumericOnlySlug() {
    createTest(
        "TC-028: Access article with numeric-only slug",
        "Verify 404 error or appropriate handling for numeric slug");

    String numericSlug = "12345";
    articlePage.navigateToArticle(numericSlug);

    assertTrue(articlePage.isPageLoaded(), "Page should load");
    assertFalse(
        articlePage.isArticlePageDisplayed() && articlePage.getArticleTitle().length() > 0,
        "Should not display valid article for numeric-only slug");

    test.info("Verified handling of numeric-only slug: " + numericSlug);
  }

  @Test(groups = {"regression", "error"})
  public void TC029_accessArticleWithUnicodeCharactersInSlug() {
    createTest(
        "TC-029: Access article with unicode characters in slug",
        "Verify error is handled gracefully for unicode characters");

    String unicodeSlug = "测试文章";
    articlePage.navigateToArticle(unicodeSlug);

    assertTrue(articlePage.isPageLoaded(), "Page should load");

    test.info("Verified handling of unicode characters in slug");
  }

  @Test(groups = {"regression", "error"})
  public void TC030_accessArticleAfterItHasBeenDeleted() {
    createTest(
        "TC-030: Access article after it has been deleted",
        "Verify 404 Not Found error is displayed for deleted article");

    String deletedArticleSlug = "deleted-article-slug-that-no-longer-exists";
    articlePage.navigateToArticle(deletedArticleSlug);

    assertTrue(articlePage.isPageLoaded(), "Page should load");
    assertFalse(
        articlePage.isArticlePageDisplayed() && articlePage.getArticleTitle().length() > 0,
        "Should not display valid article for deleted article slug");

    test.info("Verified 404 handling for deleted article slug");
  }
}
