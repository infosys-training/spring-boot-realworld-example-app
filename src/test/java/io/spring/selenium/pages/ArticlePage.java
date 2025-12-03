package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the article detail page. */
public class ArticlePage extends BasePage {

  private static final String ARTICLE_URL = "/article/";

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

  @FindBy(css = ".article-meta .btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".article-meta .btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".article-meta .btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".tag-list .tag-pill")
  private java.util.List<WebElement> articleTags;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card.comment")
  private java.util.List<WebElement> comments;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public ArticlePage navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + ARTICLE_URL + slug);
    return this;
  }

  public String getTitle() {
    return getText(articleTitle);
  }

  public String getContent() {
    return getText(articleContent);
  }

  public String getAuthorName() {
    return getText(authorLink);
  }

  public ProfilePage clickAuthorLink() {
    click(authorLink);
    return new ProfilePage(driver);
  }

  public boolean isFollowAuthorButtonDisplayed() {
    try {
      return isDisplayed(followAuthorButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFollowingAuthor() {
    try {
      String buttonText = getText(followAuthorButton);
      return buttonText.contains("Unfollow");
    } catch (Exception e) {
      return false;
    }
  }

  public ArticlePage clickFollowAuthorButton() {
    click(followAuthorButton);
    return this;
  }

  public ArticlePage followAuthor() {
    if (!isFollowingAuthor()) {
      clickFollowAuthorButton();
      waitForFollowStateChange(true);
    }
    return this;
  }

  public ArticlePage unfollowAuthor() {
    if (isFollowingAuthor()) {
      clickFollowAuthorButton();
      waitForFollowStateChange(false);
    }
    return this;
  }

  public void waitForFollowStateChange(boolean expectFollowing) {
    try {
      if (expectFollowing) {
        wait.until(ExpectedConditions.textToBePresentInElement(followAuthorButton, "Unfollow"));
      } else {
        wait.until(ExpectedConditions.textToBePresentInElement(followAuthorButton, "Follow"));
      }
    } catch (Exception e) {
      // State may have already changed
    }
  }

  public boolean isFavorited() {
    try {
      String buttonClass = favoriteButton.getAttribute("class");
      return buttonClass.contains("btn-primary");
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavoriteButton() {
    click(favoriteButton);
  }

  public boolean isEditButtonDisplayed() {
    try {
      return isDisplayed(editButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDeleteButtonDisplayed() {
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

  public int getTagCount() {
    return articleTags.size();
  }

  public boolean hasTag(String tagName) {
    for (WebElement tag : articleTags) {
      if (tag.getText().equals(tagName)) {
        return true;
      }
    }
    return false;
  }

  public void addComment(String commentText) {
    type(commentTextarea, commentText);
    click(postCommentButton);
  }

  public int getCommentCount() {
    return comments.size();
  }

  public boolean isArticlePageDisplayed() {
    try {
      return isDisplayed(articleTitle);
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public String getFollowAuthorButtonText() {
    try {
      return getText(followAuthorButton);
    } catch (Exception e) {
      return "";
    }
  }
}
