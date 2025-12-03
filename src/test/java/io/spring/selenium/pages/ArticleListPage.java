package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticleListPage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".nav-link.active")
  private WebElement activeTab;

  @FindBy(css = ".tag-list")
  private WebElement tagSidebar;

  @FindBy(css = ".pagination")
  private WebElement pagination;

  public ArticleListPage(WebDriver driver) {
    super(driver);
  }

  public ArticleListPage navigateToHome(String baseUrl) {
    driver.get(baseUrl);
    waitForArticles();
    return this;
  }

  public ArticleListPage navigateToGlobalFeed(String baseUrl) {
    driver.get(baseUrl);
    waitForArticles();
    clickGlobalFeed();
    return this;
  }

  public ArticleListPage navigateToUserFeed(String baseUrl) {
    driver.get(baseUrl);
    waitForArticles();
    clickYourFeed();
    return this;
  }

  public void waitForArticles() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
              ExpectedConditions.presenceOfElementLocated(
                  By.xpath("//*[contains(text(), 'No articles')]"))));
    } catch (Exception e) {
      // Page might be empty
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<String> getArticleTitles() {
    List<String> titles = new ArrayList<>();
    for (WebElement preview : articlePreviews) {
      try {
        WebElement titleElement = preview.findElement(By.cssSelector("h1, .preview-link h1"));
        titles.add(titleElement.getText());
      } catch (Exception e) {
        // Skip if title not found
      }
    }
    return titles;
  }

  public void clickArticleByIndex(int index) {
    if (index < articlePreviews.size()) {
      WebElement preview = articlePreviews.get(index);
      WebElement link = preview.findElement(By.cssSelector(".preview-link"));
      click(link);
    }
  }

  public void clickArticleByTitle(String title) {
    for (WebElement preview : articlePreviews) {
      try {
        WebElement titleElement = preview.findElement(By.cssSelector("h1, .preview-link h1"));
        if (titleElement.getText().equals(title)) {
          WebElement link = preview.findElement(By.cssSelector(".preview-link"));
          click(link);
          return;
        }
      } catch (Exception e) {
        // Continue to next preview
      }
    }
  }

  public void clickFavoriteOnArticle(int index) {
    if (index < articlePreviews.size()) {
      WebElement preview = articlePreviews.get(index);
      WebElement favoriteBtn =
          preview.findElement(By.cssSelector(".btn-outline-primary, .btn-primary"));
      click(favoriteBtn);
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public void clickUnfavoriteOnArticle(int index) {
    clickFavoriteOnArticle(index);
  }

  public boolean isArticleFavorited(int index) {
    if (index < articlePreviews.size()) {
      WebElement preview = articlePreviews.get(index);
      try {
        List<WebElement> favoritedBtns = preview.findElements(By.cssSelector(".btn-primary"));
        return !favoritedBtns.isEmpty();
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  public int getFavoritesCountForArticle(int index) {
    if (index < articlePreviews.size()) {
      WebElement preview = articlePreviews.get(index);
      try {
        WebElement favoriteBtn =
            preview.findElement(By.cssSelector(".btn-outline-primary, .btn-primary"));
        String text = favoriteBtn.getText();
        String countStr = text.replaceAll("[^0-9]", "");
        if (!countStr.isEmpty()) {
          return Integer.parseInt(countStr);
        }
      } catch (Exception e) {
        return 0;
      }
    }
    return 0;
  }

  public void clickGlobalFeed() {
    try {
      WebElement globalFeedLink =
          driver.findElement(By.xpath("//a[contains(text(), 'Global Feed')]"));
      click(globalFeedLink);
      waitForArticles();
    } catch (Exception e) {
      // Global feed might not be available
    }
  }

  public void clickYourFeed() {
    try {
      WebElement yourFeedLink = driver.findElement(By.xpath("//a[contains(text(), 'Your Feed')]"));
      click(yourFeedLink);
      waitForArticles();
    } catch (Exception e) {
      // Your feed might not be available
    }
  }

  public void clickTagFilter(String tag) {
    try {
      WebElement tagLink =
          tagSidebar.findElement(By.xpath(".//a[contains(text(), '" + tag + "')]"));
      click(tagLink);
      waitForArticles();
    } catch (Exception e) {
      // Tag might not exist
    }
  }

  public String getActiveTabName() {
    return getText(activeTab);
  }

  public boolean hasNoArticlesMessage() {
    try {
      return driver.getPageSource().contains("No articles");
    } catch (Exception e) {
      return false;
    }
  }
}
