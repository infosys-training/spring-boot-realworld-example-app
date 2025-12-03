package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FeedPage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = ".pagination .page-item.active")
  private WebElement activePaginationItem;

  @FindBy(linkText = "Your Feed")
  private WebElement yourFeedTab;

  @FindBy(linkText = "Global Feed")
  private WebElement globalFeedTab;

  public FeedPage(WebDriver driver) {
    super(driver);
  }

  public void waitForFeedToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'No articles')]"))));
  }

  public int getArticleCount() {
    try {
      waitForFeedToLoad();
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<ArticlePreviewComponent> getArticles() {
    List<ArticlePreviewComponent> articles = new ArrayList<>();
    for (WebElement preview : articlePreviews) {
      articles.add(new ArticlePreviewComponent(driver, preview));
    }
    return articles;
  }

  public ArticlePreviewComponent getArticleAt(int index) {
    if (index >= 0 && index < articlePreviews.size()) {
      return new ArticlePreviewComponent(driver, articlePreviews.get(index));
    }
    throw new IndexOutOfBoundsException("Article index out of bounds: " + index);
  }

  public boolean isEmptyFeedMessageDisplayed() {
    try {
      WebElement emptyMessage =
          driver.findElement(By.xpath("//*[contains(text(), 'No articles are here')]"));
      return isDisplayed(emptyMessage);
    } catch (Exception e) {
      return false;
    }
  }

  public String getEmptyFeedMessage() {
    try {
      WebElement emptyMessage =
          driver.findElement(By.xpath("//*[contains(text(), 'No articles')]"));
      return getText(emptyMessage);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isPaginationVisible() {
    try {
      return isDisplayed(pagination);
    } catch (Exception e) {
      return false;
    }
  }

  public int getPaginationPageCount() {
    try {
      return paginationItems.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public int getCurrentPageNumber() {
    try {
      String pageText = getText(activePaginationItem);
      return Integer.parseInt(pageText.trim());
    } catch (Exception e) {
      return 1;
    }
  }

  public void goToPage(int pageNumber) {
    try {
      WebElement pageLink =
          driver.findElement(
              By.xpath("//ul[contains(@class, 'pagination')]//a[text()='" + pageNumber + "']"));
      click(pageLink);
      waitForFeedToLoad();
    } catch (Exception e) {
      throw new RuntimeException("Could not navigate to page " + pageNumber, e);
    }
  }

  public void goToNextPage() {
    int currentPage = getCurrentPageNumber();
    goToPage(currentPage + 1);
  }

  public void goToPreviousPage() {
    int currentPage = getCurrentPageNumber();
    if (currentPage > 1) {
      goToPage(currentPage - 1);
    }
  }

  public boolean isYourFeedActive() {
    try {
      String classes = yourFeedTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isGlobalFeedActive() {
    try {
      String classes = globalFeedTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public void clickYourFeed() {
    click(yourFeedTab);
    waitForFeedToLoad();
  }

  public void clickGlobalFeed() {
    click(globalFeedTab);
    waitForFeedToLoad();
  }

  public List<String> getAllArticleAuthors() {
    List<String> authors = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      authors.add(article.getAuthorUsername());
    }
    return authors;
  }

  public List<String> getAllArticleTitles() {
    List<String> titles = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      titles.add(article.getTitle());
    }
    return titles;
  }

  public List<String> getAllArticleDates() {
    List<String> dates = new ArrayList<>();
    for (ArticlePreviewComponent article : getArticles()) {
      dates.add(article.getCreatedDate());
    }
    return dates;
  }

  public boolean areArticlesOrderedByDateDescending() {
    List<String> dates = getAllArticleDates();
    if (dates.size() <= 1) {
      return true;
    }
    for (int i = 0; i < dates.size() - 1; i++) {
      if (dates.get(i).compareTo(dates.get(i + 1)) < 0) {
        return false;
      }
    }
    return true;
  }

  public boolean isErrorMessageDisplayed() {
    try {
      WebElement errorMsg = driver.findElement(By.xpath("//*[contains(text(), 'Cannot load')]"));
      return isDisplayed(errorMsg);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      WebElement errorMsg = driver.findElement(By.xpath("//*[contains(text(), 'Cannot load')]"));
      return getText(errorMsg);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isLoadingSpinnerDisplayed() {
    try {
      WebElement spinner = driver.findElement(By.cssSelector(".spinner, .loading"));
      return isDisplayed(spinner);
    } catch (Exception e) {
      return false;
    }
  }
}
