package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class FavoritesPage extends BasePage {

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".user-info")
  private WebElement userInfo;

  @FindBy(css = ".nav-pills")
  private WebElement profileTabs;

  public FavoritesPage(WebDriver driver) {
    super(driver);
  }

  public FavoritesPage navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username + "/favorites");
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
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

  public int getFavoritedArticleCount() {
    return articlePreviews.size();
  }

  public List<String> getFavoritedArticleTitles() {
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

  public boolean isArticleInFavorites(String articleTitle) {
    return getFavoritedArticleTitles().contains(articleTitle);
  }

  public void clickUnfavoriteOnArticle(int index) {
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

  public void clickUnfavoriteOnArticleByTitle(String title) {
    for (int i = 0; i < articlePreviews.size(); i++) {
      WebElement preview = articlePreviews.get(i);
      try {
        WebElement titleElement = preview.findElement(By.cssSelector("h1, .preview-link h1"));
        if (titleElement.getText().equals(title)) {
          clickUnfavoriteOnArticle(i);
          return;
        }
      } catch (Exception e) {
        // Continue to next preview
      }
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

  public void clickMyArticlesTab() {
    try {
      WebElement myArticlesLink =
          profileTabs.findElement(By.xpath(".//a[contains(text(), 'My Articles')]"));
      click(myArticlesLink);
    } catch (Exception e) {
      // Tab might not exist
    }
  }

  public void clickFavoritedArticlesTab() {
    try {
      WebElement favoritedLink =
          profileTabs.findElement(By.xpath(".//a[contains(text(), 'Favorited')]"));
      click(favoritedLink);
    } catch (Exception e) {
      // Tab might not exist
    }
  }

  public boolean hasNoArticlesMessage() {
    try {
      return driver.getPageSource().contains("No articles");
    } catch (Exception e) {
      return false;
    }
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }
}
