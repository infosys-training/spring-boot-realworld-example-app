package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article detail page. */
public class ArticlePage extends BasePage {

  @FindBy(css = "h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = "a[href*='/editor/']")
  private WebElement editArticleButton;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement deleteArticleButton;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = ".author")
  private WebElement authorLink;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card")
  private List<WebElement> comments;

  private static final String ARTICLE_URL = "/article/";

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public ArticlePage navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + ARTICLE_URL + slug);
    return this;
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleContent() {
    try {
      return getText(articleContent);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isEditButtonVisible() {
    try {
      return isDisplayed(editArticleButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonVisible() {
    try {
      return isDisplayed(deleteArticleButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickEditButton() {
    click(editArticleButton);
  }

  public void clickDeleteButton() {
    click(deleteArticleButton);
  }

  public String getAuthorUsername() {
    return getText(authorLink);
  }

  public List<String> getArticleTags() {
    return articleTags.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public void addComment(String commentText) {
    type(commentTextarea, commentText);
    click(postCommentButton);
  }

  public int getCommentCount() {
    return comments.size();
  }

  public boolean isCommentDeleteButtonVisible(int commentIndex) {
    try {
      if (commentIndex >= comments.size()) {
        return false;
      }
      WebElement comment = comments.get(commentIndex);
      List<WebElement> deleteButtons = comment.findElements(By.cssSelector(".ion-trash-a"));
      return !deleteButtons.isEmpty() && deleteButtons.get(0).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void deleteComment(int commentIndex) {
    if (commentIndex < comments.size()) {
      WebElement comment = comments.get(commentIndex);
      WebElement deleteButton = comment.findElement(By.cssSelector(".ion-trash-a"));
      deleteButton.click();
    }
  }

  public boolean isArticleLoaded() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(articleTitle)) != null;
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }
}
