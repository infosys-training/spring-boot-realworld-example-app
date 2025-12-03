package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticlePage extends BasePage {

  @FindBy(css = "h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".btn-primary")
  private WebElement favoritedButton;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> tagList;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public ArticlePage navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleContent() {
    return getText(articleContent);
  }

  public boolean isFavorited() {
    try {
      List<WebElement> favoritedButtons = driver.findElements(By.cssSelector(".btn-primary"));
      for (WebElement btn : favoritedButtons) {
        String text = btn.getText().toLowerCase();
        if (text.contains("favorited") || text.contains("unfavorite")) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavoriteButton() {
    try {
      WebElement btn = findFavoriteButton();
      if (btn != null) {
        click(btn);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to click favorite button: " + e.getMessage());
    }
  }

  public void clickUnfavoriteButton() {
    clickFavoriteButton();
  }

  private WebElement findFavoriteButton() {
    List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
    for (WebElement btn : buttons) {
      String text = btn.getText().toLowerCase();
      if (text.contains("favorite")) {
        return btn;
      }
    }
    List<WebElement> outlineButtons = driver.findElements(By.cssSelector(".btn-outline-primary"));
    if (!outlineButtons.isEmpty()) {
      return outlineButtons.get(0);
    }
    List<WebElement> primaryButtons = driver.findElements(By.cssSelector(".btn-primary"));
    if (!primaryButtons.isEmpty()) {
      return primaryButtons.get(0);
    }
    return null;
  }

  public int getFavoritesCount() {
    try {
      WebElement btn = findFavoriteButton();
      if (btn != null) {
        String text = btn.getText();
        String countStr = text.replaceAll("[^0-9]", "");
        if (!countStr.isEmpty()) {
          return Integer.parseInt(countStr);
        }
      }
      return 0;
    } catch (Exception e) {
      return 0;
    }
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public List<String> getTags() {
    return tagList.stream().map(WebElement::getText).toList();
  }

  public boolean isDeleteButtonVisible() {
    return isDisplayed(deleteButton);
  }

  public boolean isEditButtonVisible() {
    return isDisplayed(editButton);
  }

  public void clickDeleteButton() {
    click(deleteButton);
  }

  public void clickEditButton() {
    click(editButton);
  }

  public boolean isArticleLoaded() {
    try {
      return isDisplayed(articleTitle);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean is404Page() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("not found") || pageSource.contains("404");
    } catch (Exception e) {
      return false;
    }
  }
}
