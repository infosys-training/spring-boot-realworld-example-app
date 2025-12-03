package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article page with author and comment functionality. */
public class ArticlePage extends BasePage {

  private static final String ARTICLE_URL = "http://localhost:3000/article/";

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".article-meta .btn.action-btn")
  private WebElement followAuthorButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".comment")
  private List<WebElement> comments;

  @FindBy(css = ".comment .author")
  private List<WebElement> commentAuthors;

  @FindBy(css = ".card-text")
  private WebElement commentInput;

  @FindBy(css = ".btn-primary[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".article-actions .btn-outline-secondary")
  private WebElement editArticleButton;

  @FindBy(css = ".article-actions .btn-outline-danger")
  private WebElement deleteArticleButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String slug) {
    driver.get(ARTICLE_URL + slug);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOf(articleTitle));
    } catch (Exception e) {
      // Article might not exist
    }
  }

  public String getArticleTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public String getArticleContent() {
    try {
      return getText(articleContent);
    } catch (Exception e) {
      return "";
    }
  }

  public String getAuthorName() {
    try {
      return getText(authorLink);
    } catch (Exception e) {
      return "";
    }
  }

  public void clickAuthorLink() {
    click(authorLink);
  }

  public boolean isFollowAuthorButtonDisplayed() {
    try {
      return followAuthorButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFollowAuthorButton() {
    click(followAuthorButton);
  }

  public String getFollowAuthorButtonText() {
    try {
      return getText(followAuthorButton);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isFollowingAuthor() {
    return getFollowAuthorButtonText().contains("Unfollow");
  }

  public int getCommentCount() {
    return comments.size();
  }

  public void clickCommentAuthor(int index) {
    if (index < commentAuthors.size()) {
      click(commentAuthors.get(index));
    }
  }

  public String getCommentAuthorName(int index) {
    if (index < commentAuthors.size()) {
      return getText(commentAuthors.get(index));
    }
    return "";
  }

  public void addComment(String commentText) {
    type(commentInput, commentText);
    click(postCommentButton);
  }

  public boolean isArticleLoaded() {
    try {
      return articleTitle.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean canEditArticle() {
    try {
      return editArticleButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean canDeleteArticle() {
    try {
      return deleteArticleButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public String getFavoriteButtonText() {
    try {
      return getText(favoriteButton);
    } catch (Exception e) {
      return "";
    }
  }

  public int getTagCount() {
    return articleTags.size();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }
}
