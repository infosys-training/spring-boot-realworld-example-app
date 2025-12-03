package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article detail page. */
public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".author")
  private WebElement authorLink;

  @FindBy(css = ".date")
  private WebElement publishDate;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".card.comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".card.comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card:not(.comment-form)")
  private List<WebElement> commentCards;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteArticleButton;

  @FindBy(css = ".btn-outline-secondary[href*='/editor']")
  private WebElement editArticleButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String articleSlug) {
    driver.get(baseUrl + "/article/" + articleSlug);
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleContent() {
    return getText(articleContent);
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public String getPublishDate() {
    return getText(publishDate);
  }

  public void enterComment(String commentText) {
    type(commentTextarea, commentText);
  }

  public void clickPostComment() {
    click(postCommentButton);
  }

  public void addComment(String commentText) {
    enterComment(commentText);
    clickPostComment();
  }

  public int getCommentCount() {
    return commentCards.size();
  }

  public boolean isCommentFormDisplayed() {
    try {
      return isDisplayed(commentTextarea);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isPostCommentButtonDisplayed() {
    try {
      return isDisplayed(postCommentButton);
    } catch (Exception e) {
      return false;
    }
  }

  public String getLatestCommentText() {
    if (!commentCards.isEmpty()) {
      WebElement latestComment = commentCards.get(0);
      WebElement commentBody = latestComment.findElement(By.cssSelector(".card-text"));
      return getText(commentBody);
    }
    return "";
  }

  public String getCommentTextByIndex(int index) {
    if (index < commentCards.size()) {
      WebElement comment = commentCards.get(index);
      WebElement commentBody = comment.findElement(By.cssSelector(".card-text"));
      return getText(commentBody);
    }
    return "";
  }

  public String getCommentAuthorByIndex(int index) {
    if (index < commentCards.size()) {
      WebElement comment = commentCards.get(index);
      WebElement authorElement = comment.findElement(By.cssSelector(".comment-author"));
      return getText(authorElement);
    }
    return "";
  }

  public String getCommentDateByIndex(int index) {
    if (index < commentCards.size()) {
      WebElement comment = commentCards.get(index);
      WebElement dateElement = comment.findElement(By.cssSelector(".date-posted"));
      return getText(dateElement);
    }
    return "";
  }

  public boolean hasComments() {
    return !commentCards.isEmpty();
  }

  public void waitForCommentsToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card:not(.comment-form)")),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".card.comment-form"))));
  }

  public void waitForCommentToAppear(String commentText) {
    wait.until(
        ExpectedConditions.textToBePresentInElementLocated(
            By.cssSelector(".card-text"), commentText));
  }

  public boolean isErrorDisplayed() {
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

  public void clickFollowButton() {
    click(followButton);
  }

  public void clickFavoriteButton() {
    click(favoriteButton);
  }

  public boolean isDeleteButtonDisplayed() {
    try {
      return isDisplayed(deleteArticleButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditButtonDisplayed() {
    try {
      return isDisplayed(editArticleButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickDeleteArticle() {
    click(deleteArticleButton);
  }

  public void clickEditArticle() {
    click(editArticleButton);
  }

  public boolean isOnArticlePage() {
    try {
      return driver.getCurrentUrl().contains("/article/");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean is404Displayed() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("404")
          || pageSource.contains("not found")
          || pageSource.contains("does not exist");
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void clearCommentTextarea() {
    waitForVisibility(commentTextarea);
    commentTextarea.clear();
  }

  public String getCommentTextareaValue() {
    return commentTextarea.getAttribute("value");
  }

  public boolean isCommentTextareaEmpty() {
    String value = getCommentTextareaValue();
    return value == null || value.trim().isEmpty();
  }

  public void deleteCommentByIndex(int index) {
    if (index < commentCards.size()) {
      WebElement comment = commentCards.get(index);
      try {
        WebElement deleteButton = comment.findElement(By.cssSelector(".ion-trash-a, .delete-btn"));
        click(deleteButton);
      } catch (Exception e) {
        // Delete button may not be present if user is not the author
      }
    }
  }

  public boolean isSignInPromptDisplayed() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("sign in") && pageSource.contains("sign up");
    } catch (Exception e) {
      return false;
    }
  }
}
