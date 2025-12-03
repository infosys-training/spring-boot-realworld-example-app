package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArticlePreviewComponent {

  private WebDriver driver;
  private WebElement articleElement;
  private WebDriverWait wait;

  private static final By TITLE_LOCATOR = By.cssSelector("h1");
  private static final By DESCRIPTION_LOCATOR = By.cssSelector("p");
  private static final By AUTHOR_USERNAME_LOCATOR = By.cssSelector(".author");
  private static final By AUTHOR_IMAGE_LOCATOR = By.cssSelector(".article-meta img");
  private static final By DATE_LOCATOR = By.cssSelector(".date");
  private static final By TAG_LIST_LOCATOR = By.cssSelector(".tag-list");
  private static final By TAG_ITEM_LOCATOR = By.cssSelector(".tag-list li");
  private static final By FAVORITE_BUTTON_LOCATOR = By.cssSelector(".pull-xs-right button");
  private static final By FAVORITE_COUNT_LOCATOR = By.cssSelector(".pull-xs-right button");
  private static final By READ_MORE_LOCATOR = By.cssSelector(".preview-link span");
  private static final By ARTICLE_LINK_LOCATOR = By.cssSelector(".preview-link");

  public ArticlePreviewComponent(WebDriver driver, WebElement articleElement) {
    this.driver = driver;
    this.articleElement = articleElement;
    this.wait = new WebDriverWait(driver, 10);
  }

  public String getTitle() {
    try {
      WebElement titleElement = articleElement.findElement(TITLE_LOCATOR);
      return titleElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean hasTitleDisplayed() {
    try {
      WebElement titleElement = articleElement.findElement(TITLE_LOCATOR);
      return titleElement.isDisplayed() && !titleElement.getText().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public String getDescription() {
    try {
      WebElement descElement = articleElement.findElement(DESCRIPTION_LOCATOR);
      return descElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean hasDescriptionDisplayed() {
    try {
      WebElement descElement = articleElement.findElement(DESCRIPTION_LOCATOR);
      return descElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getAuthorUsername() {
    try {
      WebElement authorElement = articleElement.findElement(AUTHOR_USERNAME_LOCATOR);
      return authorElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean hasAuthorUsernameDisplayed() {
    try {
      WebElement authorElement = articleElement.findElement(AUTHOR_USERNAME_LOCATOR);
      return authorElement.isDisplayed() && !authorElement.getText().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasAuthorImageDisplayed() {
    try {
      WebElement imageElement = articleElement.findElement(AUTHOR_IMAGE_LOCATOR);
      return imageElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getAuthorImageSrc() {
    try {
      WebElement imageElement = articleElement.findElement(AUTHOR_IMAGE_LOCATOR);
      return imageElement.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCreationDate() {
    try {
      WebElement dateElement = articleElement.findElement(DATE_LOCATOR);
      return dateElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean hasCreationDateDisplayed() {
    try {
      WebElement dateElement = articleElement.findElement(DATE_LOCATOR);
      return dateElement.isDisplayed() && !dateElement.getText().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getTags() {
    try {
      List<WebElement> tagElements = articleElement.findElements(TAG_ITEM_LOCATOR);
      return tagElements.stream().map(WebElement::getText).collect(Collectors.toList());
    } catch (Exception e) {
      return List.of();
    }
  }

  public boolean hasTagsDisplayed() {
    try {
      WebElement tagList = articleElement.findElement(TAG_LIST_LOCATOR);
      return tagList.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getTagCount() {
    try {
      List<WebElement> tagElements = articleElement.findElements(TAG_ITEM_LOCATOR);
      return tagElements.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public int getFavoriteCount() {
    try {
      WebElement favoriteButton = articleElement.findElement(FAVORITE_COUNT_LOCATOR);
      String text = favoriteButton.getText().trim();
      return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean hasFavoriteCountDisplayed() {
    try {
      WebElement favoriteButton = articleElement.findElement(FAVORITE_BUTTON_LOCATOR);
      return favoriteButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFavorited() {
    try {
      WebElement favoriteButton = articleElement.findElement(FAVORITE_BUTTON_LOCATOR);
      String className = favoriteButton.getAttribute("class");
      return className.contains("btn-primary") && !className.contains("btn-outline-primary");
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavoriteButton() {
    try {
      WebElement favoriteButton = articleElement.findElement(FAVORITE_BUTTON_LOCATOR);
      favoriteButton.click();
    } catch (Exception e) {
      throw new RuntimeException("Failed to click favorite button", e);
    }
  }

  public void clickArticleLink() {
    try {
      WebElement link = articleElement.findElement(ARTICLE_LINK_LOCATOR);
      link.click();
    } catch (Exception e) {
      throw new RuntimeException("Failed to click article link", e);
    }
  }

  public void clickAuthorLink() {
    try {
      WebElement authorLink = articleElement.findElement(AUTHOR_USERNAME_LOCATOR);
      authorLink.click();
    } catch (Exception e) {
      throw new RuntimeException("Failed to click author link", e);
    }
  }

  public void clickTag(String tagName) {
    try {
      List<WebElement> tagElements = articleElement.findElements(TAG_ITEM_LOCATOR);
      for (WebElement tag : tagElements) {
        if (tag.getText().equals(tagName)) {
          tag.click();
          return;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to click tag: " + tagName, e);
    }
  }

  public boolean hasReadMoreLink() {
    try {
      WebElement readMore = articleElement.findElement(READ_MORE_LOCATOR);
      return readMore.isDisplayed() && readMore.getText().contains("Read more");
    } catch (Exception e) {
      return false;
    }
  }

  public WebElement getElement() {
    return articleElement;
  }

  public boolean isDisplayed() {
    try {
      return articleElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}
