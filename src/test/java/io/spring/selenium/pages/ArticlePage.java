package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the Article detail page. */
public class ArticlePage extends BasePage {

  @FindBy(css = "h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content p")
  private WebElement articleBody;

  @FindBy(css = ".author")
  private WebElement authorName;

  @FindBy(css = ".date")
  private WebElement articleDate;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = "button.btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = "button.btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = "a[href*='/editor/']")
  private WebElement editButton;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".comment-form textarea")
  private WebElement commentTextarea;

  @FindBy(css = ".comment-form button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".card.comment")
  private List<WebElement> comments;

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String slug) {
    driver.get(System.getProperty("base.url", "http://localhost:3000") + "/article/" + slug);
  }

  public String getTitle() {
    return getText(articleTitle);
  }

  public String getBody() {
    try {
      return getText(articleBody);
    } catch (Exception e) {
      return "";
    }
  }

  public String getAuthorName() {
    return getText(authorName);
  }

  public String getArticleDate() {
    return getText(articleDate);
  }

  public List<String> getTags() {
    List<String> tagNames = new ArrayList<>();
    for (WebElement tag : articleTags) {
      tagNames.add(tag.getText().trim());
    }
    return tagNames;
  }

  public boolean hasTag(String tagName) {
    return getTags().contains(tagName);
  }

  public void clickFollow() {
    click(followButton);
  }

  public void clickFavorite() {
    click(favoriteButton);
  }

  public void clickEdit() {
    click(editButton);
  }

  public void clickDelete() {
    click(deleteButton);
  }

  public void addComment(String comment) {
    type(commentTextarea, comment);
    click(postCommentButton);
  }

  public int getCommentCount() {
    return comments.size();
  }

  public boolean isArticleDisplayed() {
    try {
      return isDisplayed(articleTitle) && isDisplayed(articleMeta);
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

  public boolean isDeleteButtonVisible() {
    try {
      return isDisplayed(deleteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getSlugFromUrl() {
    String url = getCurrentUrl();
    if (url.contains("/article/")) {
      return url.substring(url.lastIndexOf("/article/") + 9);
    }
    return "";
  }

  public boolean isAuthorDisplayed() {
    try {
      return isDisplayed(authorName);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDateDisplayed() {
    try {
      return isDisplayed(articleDate);
    } catch (Exception e) {
      return false;
    }
  }

  public int getTagCount() {
    return articleTags.size();
  }

  public boolean isFavoriteButtonVisible() {
    try {
      return isDisplayed(favoriteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public String getFavoriteCount() {
    try {
      String buttonText = getText(favoriteButton);
      return buttonText.replaceAll("[^0-9]", "");
    } catch (Exception e) {
      return "0";
    }
  }
}
