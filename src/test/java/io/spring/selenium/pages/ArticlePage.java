package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Article page. */
public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-meta img")
  private WebElement authorImage;

  @FindBy(css = ".article-meta .author")
  private WebElement authorName;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".card .comment-author-img")
  private List<WebElement> commentAuthorImages;

  @FindBy(css = ".card .comment-author")
  private List<WebElement> commentAuthors;

  @FindBy(css = ".card .card-text")
  private List<WebElement> commentTexts;

  @FindBy(css = "textarea[placeholder='Write a comment...']")
  private WebElement commentInput;

  @FindBy(css = "button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
  }

  public String getArticleTitle() {
    return getText(articleTitle);
  }

  public boolean isAuthorImageDisplayed() {
    try {
      return waitForVisibility(authorImage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getAuthorImageSrc() {
    try {
      return waitForVisibility(authorImage).getAttribute("src");
    } catch (Exception e) {
      return null;
    }
  }

  public String getAuthorName() {
    return getText(authorName);
  }

  public String getArticleDate() {
    return getText(articleDate);
  }

  public String getArticleContent() {
    return getText(articleContent);
  }

  public int getTagCount() {
    return articleTags.size();
  }

  public List<WebElement> getCommentAuthorImages() {
    return commentAuthorImages;
  }

  public String getCommentAuthorImageSrc(int index) {
    if (index < commentAuthorImages.size()) {
      return commentAuthorImages.get(index).getAttribute("src");
    }
    return null;
  }

  public int getCommentCount() {
    return commentTexts.size();
  }

  public void enterComment(String comment) {
    type(commentInput, comment);
  }

  public void clickPostComment() {
    click(postCommentButton);
  }

  public void postComment(String comment) {
    enterComment(comment);
    clickPostComment();
  }

  public boolean isCommentInputDisplayed() {
    return isDisplayed(commentInput);
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public void clickFollow() {
    click(followButton);
  }

  public boolean verifyAuthorImageMatchesDefault(String defaultImageUrl) {
    String currentSrc = getAuthorImageSrc();
    return currentSrc != null && currentSrc.contains(defaultImageUrl);
  }

  public boolean isAuthorImageLoaded() {
    try {
      WebElement img = waitForVisibility(authorImage);
      String naturalWidth =
          img.getAttribute("naturalWidth") != null ? img.getAttribute("naturalWidth") : "0";
      return Integer.parseInt(naturalWidth) > 0;
    } catch (Exception e) {
      return false;
    }
  }
}
