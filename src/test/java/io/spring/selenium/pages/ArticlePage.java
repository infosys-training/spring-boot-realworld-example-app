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

  @FindBy(css = ".article-content p")
  private WebElement articleBody;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".article-meta button.btn-outline-secondary")
  private WebElement followAuthorButton;

  @FindBy(css = ".article-meta button.btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".article-actions button.btn-outline-secondary")
  private WebElement editArticleButton;

  @FindBy(css = ".article-actions button.btn-outline-danger")
  private WebElement deleteArticleButton;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card.comment")
  private List<WebElement> comments;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public boolean isArticlePageDisplayed() {
    try {
      return articleTitle.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleBody() {
    return getText(articleBody);
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public void clickAuthorLink() {
    click(authorLink);
  }

  public String getArticleDate() {
    return getText(articleDate);
  }

  public List<String> getArticleTags() {
    return articleTags.stream().map(WebElement::getText).toList();
  }

  public void clickFollowAuthor() {
    click(followAuthorButton);
  }

  public String getFollowButtonText() {
    return followAuthorButton.getText();
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public String getFavoriteButtonText() {
    return favoriteButton.getText();
  }

  public boolean isFavorited() {
    String classes = favoriteButton.getAttribute("class");
    return classes.contains("btn-primary");
  }

  public int getFavoriteCount() {
    String text = favoriteButton.getText();
    String countStr = text.replaceAll("[^0-9]", "");
    try {
      return Integer.parseInt(countStr);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public boolean isEditButtonDisplayed() {
    try {
      return editArticleButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonDisplayed() {
    try {
      return deleteArticleButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickEditArticle() {
    click(editArticleButton);
  }

  public void clickDeleteArticle() {
    click(deleteArticleButton);
  }

  public void enterComment(String comment) {
    type(commentTextarea, comment);
  }

  public void clickPostComment() {
    click(postCommentButton);
  }

  public void postComment(String comment) {
    enterComment(comment);
    clickPostComment();
  }

  public List<WebElement> getComments() {
    return comments;
  }

  public int getCommentCount() {
    return comments.size();
  }

  public String getCommentText(int index) {
    if (index < comments.size()) {
      return comments.get(index).findElement(By.cssSelector(".card-block p")).getText();
    }
    return null;
  }

  public String getCommentAuthor(int index) {
    if (index < comments.size()) {
      return comments.get(index).findElement(By.cssSelector(".comment-author")).getText();
    }
    return null;
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
