package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ArticleListPage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080/articles";

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  @FindBy(css = ".col-md-9")
  private WebElement mainContent;

  public ArticleListPage(WebDriver driver) {
    super(driver);
  }

  public ArticleListPage navigate() {
    driver.get(BASE_URL);
    waitForArticleList();
    return this;
  }

  public ArticleListPage navigateWithOffset(int offset) {
    driver.get(BASE_URL + "/?offset=" + offset);
    waitForArticleList();
    return this;
  }

  public ArticleListPage navigateWithTag(String tag) {
    driver.get(BASE_URL + "/?tag=" + tag);
    waitForArticleList();
    return this;
  }

  public void waitForArticleList() {
    try {
      wait.until(
          driver -> {
            List<WebElement> previews = driver.findElements(By.cssSelector(".article-preview"));
            return !previews.isEmpty();
          });
    } catch (Exception e) {
      // Might be empty state or error
    }
  }

  public int getDisplayedArticleCount() {
    return articlePreviews.size();
  }

  public List<ArticlePreviewComponent> getArticles() {
    List<ArticlePreviewComponent> articles = new ArrayList<>();
    for (WebElement preview : articlePreviews) {
      if (!preview.getText().contains("No articles are here")) {
        articles.add(new ArticlePreviewComponent(driver, preview));
      }
    }
    return articles;
  }

  public ArticlePreviewComponent getArticleAt(int index) {
    List<ArticlePreviewComponent> articles = getArticles();
    if (index >= 0 && index < articles.size()) {
      return articles.get(index);
    }
    return null;
  }

  public ArticlePreviewComponent getFirstArticle() {
    return getArticleAt(0);
  }

  public ArticlePreviewComponent getLastArticle() {
    List<ArticlePreviewComponent> articles = getArticles();
    if (!articles.isEmpty()) {
      return articles.get(articles.size() - 1);
    }
    return null;
  }

  public boolean hasArticles() {
    List<ArticlePreviewComponent> articles = getArticles();
    return !articles.isEmpty();
  }

  public boolean isEmptyStateDisplayed() {
    for (WebElement preview : articlePreviews) {
      if (preview.getText().contains("No articles are here")) {
        return true;
      }
    }
    return false;
  }

  public boolean isPaginationDisplayed() {
    try {
      return pagination.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public PaginationComponent getPagination() {
    if (isPaginationDisplayed()) {
      return new PaginationComponent(driver, pagination);
    }
    return null;
  }

  public List<String> getAllArticleTitles() {
    List<String> titles = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      titles.add(article.getTitle());
    }
    return titles;
  }

  public List<String> getAllArticleAuthors() {
    List<String> authors = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      authors.add(article.getAuthorUsername());
    }
    return authors;
  }

  public List<String> getAllArticleDates() {
    List<String> dates = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      dates.add(article.getCreationDate());
    }
    return dates;
  }

  public boolean allArticlesHaveTitles() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasTitleDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveDescriptions() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasDescriptionDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveAuthors() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasAuthorUsernameDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveAuthorImages() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasAuthorImageDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveDates() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasCreationDateDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveFavoriteCounts() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasFavoriteCountDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public boolean allArticlesHaveTagLists() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (!article.hasTagsDisplayed()) {
        return false;
      }
    }
    return true;
  }

  public ArticlePreviewComponent findArticleByTitle(String title) {
    for (ArticlePreviewComponent article : getArticles()) {
      if (article.getTitle().equals(title)) {
        return article;
      }
    }
    return null;
  }

  public ArticlePreviewComponent findArticleByAuthor(String author) {
    for (ArticlePreviewComponent article : getArticles()) {
      if (article.getAuthorUsername().equals(author)) {
        return article;
      }
    }
    return null;
  }

  public ArticlePreviewComponent findArticleWithTag(String tag) {
    for (ArticlePreviewComponent article : getArticles()) {
      if (article.getTags().contains(tag)) {
        return article;
      }
    }
    return null;
  }

  public ArticlePreviewComponent findArticleWithNoTags() {
    for (ArticlePreviewComponent article : getArticles()) {
      if (article.getTagCount() == 0) {
        return article;
      }
    }
    return null;
  }

  public void goToNextPage() {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      paginationComponent.clickNextPage();
      waitForArticleList();
    }
  }

  public void goToPreviousPage() {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      paginationComponent.clickPreviousPage();
      waitForArticleList();
    }
  }

  public void goToPage(int pageNumber) {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      paginationComponent.clickPage(pageNumber);
      waitForArticleList();
    }
  }

  public void goToFirstPage() {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      paginationComponent.clickFirstPage();
      waitForArticleList();
    }
  }

  public void goToLastPage() {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      paginationComponent.clickLastPage();
      waitForArticleList();
    }
  }

  public int getCurrentPageNumber() {
    PaginationComponent paginationComponent = getPagination();
    if (paginationComponent != null) {
      return paginationComponent.getCurrentPage();
    }
    return 1;
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
