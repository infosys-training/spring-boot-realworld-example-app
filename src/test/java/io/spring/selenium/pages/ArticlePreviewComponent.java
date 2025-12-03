package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ArticlePreviewComponent {

  private WebDriver driver;
  private WebElement rootElement;
  private WebDriverWait wait;
  private static final long DEFAULT_TIMEOUT_SECONDS = 10;

  public ArticlePreviewComponent(WebDriver driver, WebElement rootElement) {
    this.driver = driver;
    this.rootElement = rootElement;
    this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT_SECONDS);
  }

  public String getTitle() {
    try {
      WebElement titleElement = rootElement.findElement(By.cssSelector("h1"));
      return titleElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getDescription() {
    try {
      WebElement descElement = rootElement.findElement(By.cssSelector(".preview-link p"));
      return descElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getAuthorUsername() {
    try {
      WebElement authorElement = rootElement.findElement(By.cssSelector(".author"));
      return authorElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getCreatedDate() {
    try {
      WebElement dateElement = rootElement.findElement(By.cssSelector(".date"));
      return dateElement.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public int getFavoriteCount() {
    try {
      WebElement favoriteButton = rootElement.findElement(By.cssSelector("button"));
      String buttonText = favoriteButton.getText().trim();
      return Integer.parseInt(buttonText.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean isFavorited() {
    try {
      WebElement favoriteButton = rootElement.findElement(By.cssSelector("button"));
      String classes = favoriteButton.getAttribute("class");
      return classes != null && classes.contains("btn-primary") && !classes.contains("btn-outline");
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavoriteButton() {
    try {
      WebElement favoriteButton = rootElement.findElement(By.cssSelector("button"));
      favoriteButton.click();
    } catch (Exception e) {
      throw new RuntimeException("Could not click favorite button", e);
    }
  }

  public List<String> getTags() {
    List<String> tags = new ArrayList<>();
    try {
      List<WebElement> tagElements = rootElement.findElements(By.cssSelector(".tag-list li"));
      for (WebElement tag : tagElements) {
        tags.add(tag.getText());
      }
    } catch (Exception e) {
      return tags;
    }
    return tags;
  }

  public boolean hasTags() {
    return !getTags().isEmpty();
  }

  public void clickTitle() {
    try {
      WebElement titleElement = rootElement.findElement(By.cssSelector("h1"));
      titleElement.click();
    } catch (Exception e) {
      throw new RuntimeException("Could not click article title", e);
    }
  }

  public void clickReadMore() {
    try {
      WebElement readMoreLink = rootElement.findElement(By.cssSelector(".preview-link"));
      readMoreLink.click();
    } catch (Exception e) {
      throw new RuntimeException("Could not click read more link", e);
    }
  }

  public void clickAuthor() {
    try {
      WebElement authorElement = rootElement.findElement(By.cssSelector(".author"));
      authorElement.click();
    } catch (Exception e) {
      throw new RuntimeException("Could not click author link", e);
    }
  }

  public boolean hasAuthorImage() {
    try {
      WebElement authorImage = rootElement.findElement(By.cssSelector(".article-meta img"));
      return authorImage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getAuthorImageSrc() {
    try {
      WebElement authorImage = rootElement.findElement(By.cssSelector(".article-meta img"));
      return authorImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isDisplayed() {
    try {
      return rootElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isTitleDisplayed() {
    try {
      WebElement titleElement = rootElement.findElement(By.cssSelector("h1"));
      return titleElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDescriptionDisplayed() {
    try {
      WebElement descElement = rootElement.findElement(By.cssSelector(".preview-link p"));
      return descElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isAuthorDisplayed() {
    try {
      WebElement authorElement = rootElement.findElement(By.cssSelector(".author"));
      return authorElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDateDisplayed() {
    try {
      WebElement dateElement = rootElement.findElement(By.cssSelector(".date"));
      return dateElement.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFavoriteButtonDisplayed() {
    try {
      WebElement favoriteButton = rootElement.findElement(By.cssSelector("button"));
      return favoriteButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickTag(String tagName) {
    try {
      WebElement tag =
          rootElement.findElement(
              By.xpath(
                  ".//li[contains(@class, 'tag-pill') and .//span[text()='" + tagName + "']]"));
      tag.click();
    } catch (Exception e) {
      throw new RuntimeException("Could not click tag: " + tagName, e);
    }
  }
}
