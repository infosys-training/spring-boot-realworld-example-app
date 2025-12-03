package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-page")));
  }

  public String getTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public String getContent() {
    try {
      return getText(articleContent);
    } catch (Exception e) {
      return "";
    }
  }

  public List<String> getTags() {
    List<String> tags = new ArrayList<>();
    try {
      for (WebElement tag : articleTags) {
        tags.add(tag.getText().trim());
      }
    } catch (Exception e) {
      // No tags
    }
    return tags;
  }

  public boolean hasTag(String tagName) {
    return getTags().contains(tagName);
  }

  public String getAuthor() {
    try {
      return getText(authorLink);
    } catch (Exception e) {
      return "";
    }
  }

  public HomePage deleteArticle() {
    click(deleteButton);
    try {
      wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
    } catch (Exception e) {
      // Deletion might have failed
    }
    return new HomePage(driver);
  }

  public EditorPage editArticle() {
    click(editButton);
    return new EditorPage(driver);
  }

  public boolean canDelete() {
    try {
      return deleteButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean canEdit() {
    try {
      return editButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessages() {
    try {
      return errorMessages.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnArticlePage() {
    return driver.getCurrentUrl().contains("/article/");
  }

  public String getSlug() {
    String url = driver.getCurrentUrl();
    if (url.contains("/article/")) {
      return url.substring(url.lastIndexOf("/article/") + 9);
    }
    return "";
  }
}
