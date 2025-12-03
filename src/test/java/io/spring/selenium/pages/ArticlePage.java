package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ArticlePage extends BasePage {

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagsInput;

  @FindBy(css = "button[type='submit']")
  private WebElement publishButton;

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = "button.btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = "button.btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".btn-outline-secondary[href*='/editor/']")
  private WebElement editButton;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = "textarea[placeholder='Write a comment...']")
  private WebElement commentTextarea;

  @FindBy(css = "button[type='submit'].btn-primary")
  private WebElement postCommentButton;

  @FindBy(css = ".card .card-block")
  private List<WebElement> comments;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToNewArticlePage(String baseUrl) {
    driver.get(baseUrl + "/editor");
  }

  public void navigateToArticle(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
  }

  public void navigateToEditArticle(String baseUrl, String slug) {
    driver.get(baseUrl + "/editor/" + slug);
  }

  public boolean isNewArticlePageDisplayed() {
    try {
      return titleInput.isDisplayed() && bodyTextarea.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isArticlePageDisplayed() {
    try {
      return articleTitle.isDisplayed() && articleContent.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void enterTitle(String title) {
    type(titleInput, title);
  }

  public void enterDescription(String description) {
    type(descriptionInput, description);
  }

  public void enterBody(String body) {
    type(bodyTextarea, body);
  }

  public void enterTags(String tags) {
    type(tagsInput, tags);
  }

  public void clickPublish() {
    click(publishButton);
  }

  public void createArticle(String title, String description, String body, String tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    if (tags != null && !tags.isEmpty()) {
      enterTags(tags);
    }
    clickPublish();
  }

  public void updateArticle(String title, String description, String body) {
    if (title != null && !title.isEmpty()) {
      enterTitle(title);
    }
    if (description != null && !description.isEmpty()) {
      enterDescription(description);
    }
    if (body != null && !body.isEmpty()) {
      enterBody(body);
    }
    clickPublish();
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public String getArticleContent() {
    return getText(articleContent);
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public void clickFollow() {
    click(followButton);
  }

  public void clickEdit() {
    click(editButton);
  }

  public void clickDelete() {
    click(deleteButton);
  }

  public boolean isEditButtonDisplayed() {
    try {
      return editButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonDisplayed() {
    try {
      return deleteButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFavoriteButtonDisplayed() {
    try {
      return favoriteButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
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

  public int getCommentCount() {
    return comments.size();
  }

  public boolean isCommentSectionDisplayed() {
    try {
      return commentTextarea.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    return getText(errorMessages);
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public int getTagCount() {
    return articleTags.size();
  }
}
