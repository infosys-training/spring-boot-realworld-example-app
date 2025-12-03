package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article view page. */
public class ArticlePage extends BasePage {

  @FindBy(css = ".banner h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = "a.btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card .card-block")
  private List<WebElement> comments;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
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

  public String getAuthor() {
    try {
      return getText(authorLink);
    } catch (Exception e) {
      return "";
    }
  }

  public String getDate() {
    try {
      return getText(articleDate);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isEditButtonVisible() {
    try {
      return isDisplayed(editButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonVisible() {
    try {
      return isDisplayed(deleteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickEditButton() {
    click(editButton);
  }

  public void clickDeleteButton() {
    click(deleteButton);
  }

  public List<String> getTags() {
    return articleTags.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public void addComment(String commentText) {
    type(commentTextarea, commentText);
    click(postCommentButton);
  }

  public int getCommentCount() {
    return comments.size();
  }

  public boolean hasError() {
    try {
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }

  public void waitForPageToLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public boolean isOnArticlePage() {
    try {
      return driver.getCurrentUrl().contains("/article/");
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentSlug() {
    String url = driver.getCurrentUrl();
    if (url.contains("/article/")) {
      return url.substring(url.lastIndexOf("/article/") + 9);
    }
    return "";
  }

  public boolean is404Error() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("404") || pageSource.contains("not found");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean is403Error() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("403") || pageSource.contains("forbidden");
    } catch (Exception e) {
      return false;
    }
  }
}
