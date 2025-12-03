package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article detail page. */
public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page .banner h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".card .comment-body")
  private List<WebElement> comments;

  @FindBy(css = ".card-footer .comment-author")
  private List<WebElement> commentAuthors;

  @FindBy(css = "textarea[placeholder='Write a comment...']")
  private WebElement commentInput;

  @FindBy(css = "button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".btn-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement unfavoriteButton;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String slug) {
    driver.get(getBaseUrl() + "/article/" + slug);
  }

  public String getTitle() {
    return getText(articleTitle);
  }

  public String getContent() {
    return getText(articleContent);
  }

  public String getAuthor() {
    return getText(authorLink);
  }

  public String getDate() {
    return getText(articleDate);
  }

  public boolean isDeleteButtonVisible() {
    try {
      return isDisplayed(deleteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditButtonVisible() {
    try {
      return isDisplayed(editButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickDeleteButton() {
    click(deleteButton);
  }

  public void clickEditButton() {
    click(editButton);
  }

  public void deleteArticle() {
    clickDeleteButton();
    acceptConfirmationDialog();
  }

  public void deleteArticleAndCancel() {
    clickDeleteButton();
    dismissConfirmationDialog();
  }

  public boolean isConfirmationDialogPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  public void acceptConfirmationDialog() {
    try {
      wait.until(ExpectedConditions.alertIsPresent());
      Alert alert = driver.switchTo().alert();
      alert.accept();
    } catch (Exception e) {
      // Alert may have already been handled
    }
  }

  public void dismissConfirmationDialog() {
    try {
      wait.until(ExpectedConditions.alertIsPresent());
      Alert alert = driver.switchTo().alert();
      alert.dismiss();
    } catch (Exception e) {
      // Alert may have already been handled
    }
  }

  public String getConfirmationDialogText() {
    try {
      wait.until(ExpectedConditions.alertIsPresent());
      Alert alert = driver.switchTo().alert();
      return alert.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public int getTagCount() {
    return articleTags.size();
  }

  public List<String> getTags() {
    return articleTags.stream()
        .map(WebElement::getText)
        .collect(java.util.stream.Collectors.toList());
  }

  public int getCommentCount() {
    return comments.size();
  }

  public void addComment(String commentText) {
    type(commentInput, commentText);
    click(postCommentButton);
  }

  public boolean isCommentInputVisible() {
    try {
      return isDisplayed(commentInput);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public void clickUnfavorite() {
    click(unfavoriteButton);
  }

  public boolean isFavorited() {
    try {
      return isDisplayed(unfavoriteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickAuthorProfile() {
    click(authorLink);
  }

  public boolean isArticleLoaded() {
    try {
      return isDisplayed(articleTitle) && isDisplayed(articleContent);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isArticleNotFound() {
    try {
      String pageSource = driver.getPageSource();
      return pageSource.contains("404")
          || pageSource.contains("not found")
          || pageSource.contains("Not Found");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessages() {
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

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.visibilityOf(articleTitle),
            ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "404")));
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isOnHomePage() {
    String currentUrl = driver.getCurrentUrl();
    return currentUrl.equals(getBaseUrl()) || currentUrl.equals(getBaseUrl() + "/");
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
