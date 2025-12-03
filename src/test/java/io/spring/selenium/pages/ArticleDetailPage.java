package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticleDetailPage extends BasePage {

  @FindBy(css = ".article-page")
  private WebElement articlePageContainer;

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card")
  private List<WebElement> comments;

  public ArticleDetailPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(articlePageContainer));
  }

  public boolean isArticlePageDisplayed() {
    return isDisplayed(articlePageContainer);
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

  public String getArticleDate() {
    return getText(articleDate);
  }

  public List<String> getArticleTags() {
    return articleTags.stream()
        .map(WebElement::getText)
        .collect(java.util.stream.Collectors.toList());
  }

  public void clickAuthorLink() {
    click(authorLink);
  }

  public void clickFavoriteButton() {
    click(favoriteButton);
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public void addComment(String commentText) {
    type(commentTextarea, commentText);
    click(postCommentButton);
  }

  public int getCommentCount() {
    return comments.size();
  }

  public boolean isFavorited() {
    return favoriteButton.getAttribute("class").contains("btn-primary");
  }
}
