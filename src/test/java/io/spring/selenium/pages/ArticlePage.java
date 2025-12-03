package io.spring.selenium.pages;

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

  @FindBy(css = ".article-meta .date")
  private WebElement publishDate;

  @FindBy(css = ".card.comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".card.comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card:not(.comment-form)")
  private List<WebElement> commentCards;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement deleteArticleButton;

  @FindBy(css = "a[href*='/editor/']")
  private WebElement editArticleButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement followButton;

  @FindBy(css = ".btn-primary")
  private WebElement favoriteButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public ArticlePage navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
    waitForPageLoad();
    return this;
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

  public void enterComment(String commentText) {
    type(commentTextarea, commentText);
  }

  public void clickPostComment() {
    click(postCommentButton);
    waitForCommentToPost();
  }

  public void postComment(String commentText) {
    enterComment(commentText);
    clickPostComment();
  }

  public int getCommentCount() {
    try {
      return commentCards.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public List<WebElement> getCommentCards() {
    return commentCards;
  }

  public String getCommentText(int index) {
    if (index < commentCards.size()) {
      WebElement card = commentCards.get(index);
      WebElement cardText = card.findElement(By.cssSelector(".card-text"));
      return cardText.getText();
    }
    throw new IndexOutOfBoundsException("Comment index out of bounds: " + index);
  }

  public String getCommentAuthor(int index) {
    if (index < commentCards.size()) {
      WebElement card = commentCards.get(index);
      WebElement authorElement = card.findElement(By.cssSelector(".comment-author"));
      return authorElement.getText();
    }
    throw new IndexOutOfBoundsException("Comment index out of bounds: " + index);
  }

  public boolean isDeleteButtonVisibleForComment(int index) {
    if (index < commentCards.size()) {
      WebElement card = commentCards.get(index);
      try {
        WebElement deleteButton = card.findElement(By.cssSelector(".mod-options .ion-trash-a"));
        return deleteButton.isDisplayed();
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  public void deleteComment(int index) {
    if (index < commentCards.size()) {
      WebElement card = commentCards.get(index);
      WebElement deleteButton = card.findElement(By.cssSelector(".mod-options .ion-trash-a"));
      click(deleteButton);
      waitForCommentDeletion();
    } else {
      throw new IndexOutOfBoundsException("Comment index out of bounds: " + index);
    }
  }

  public void deleteCommentByText(String commentText) {
    for (int i = 0; i < commentCards.size(); i++) {
      if (getCommentText(i).contains(commentText)) {
        deleteComment(i);
        return;
      }
    }
    throw new RuntimeException("Comment not found with text: " + commentText);
  }

  public boolean isCommentPresent(String commentText) {
    for (WebElement card : commentCards) {
      try {
        WebElement cardText = card.findElement(By.cssSelector(".card-text"));
        if (cardText.getText().contains(commentText)) {
          return true;
        }
      } catch (Exception e) {
        // continue
      }
    }
    return false;
  }

  public int countDeleteButtons() {
    int count = 0;
    for (int i = 0; i < commentCards.size(); i++) {
      if (isDeleteButtonVisibleForComment(i)) {
        count++;
      }
    }
    return count;
  }

  public boolean isCommentFormVisible() {
    try {
      return commentTextarea.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInPromptVisible() {
    try {
      WebElement signInPrompt = driver.findElement(By.cssSelector("a[href='/user/login']"));
      return signInPrompt.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteArticleButtonVisible() {
    try {
      return deleteArticleButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditArticleButtonVisible() {
    try {
      return editArticleButton.isDisplayed();
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

  private void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  private void waitForCommentToPost() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector(".card:not(.comment-form)"), 0),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-text"))));
  }

  private void waitForCommentDeletion() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void waitForCommentsToLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }
}
