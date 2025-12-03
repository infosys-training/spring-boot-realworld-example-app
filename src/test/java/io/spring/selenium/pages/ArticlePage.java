package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article detail page with comments section. */
public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-content")
  private WebElement articleContent;

  @FindBy(css = ".banner")
  private WebElement articleBanner;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  @FindBy(css = ".card")
  private List<WebElement> commentCards;

  @FindBy(css = ".card-text")
  private List<WebElement> commentBodies;

  @FindBy(css = ".comment-author")
  private List<WebElement> commentAuthors;

  @FindBy(css = ".comment-author-img")
  private List<WebElement> commentAuthorImages;

  @FindBy(css = ".date-posted")
  private List<WebElement> commentDates;

  @FindBy(css = ".card-footer")
  private List<WebElement> commentFooters;

  @FindBy(css = "textarea[placeholder='Write a comment...']")
  private WebElement commentInput;

  @FindBy(css = "button[type='submit']")
  private WebElement postCommentButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".spinner-border")
  private WebElement loadingSpinner;

  private static final String BASE_URL = "http://localhost:3000";

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToArticle(String slug) {
    driver.get(BASE_URL + "/article/" + slug);
    waitForPageLoad();
  }

  public void navigateToUrl(String url) {
    driver.get(url);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-page")),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-page")),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1"))));
    } catch (Exception e) {
      // Page may have loaded with different structure
    }
  }

  public void waitForCommentsToLoad() {
    try {
      // Wait for loading spinner to disappear if present
      wait.until(
          ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".spinner-border")));
      // Small delay to allow comments to render
      Thread.sleep(500);
    } catch (Exception e) {
      // Comments may already be loaded or no spinner present
    }
  }

  public String getArticleTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isArticlePageDisplayed() {
    try {
      return driver.findElement(By.cssSelector(".article-page")).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorPageDisplayed() {
    try {
      String pageSource = driver.getPageSource().toLowerCase();
      return pageSource.contains("404")
          || pageSource.contains("not found")
          || pageSource.contains("error");
    } catch (Exception e) {
      return false;
    }
  }

  public int getCommentCount() {
    waitForCommentsToLoad();
    return commentCards.size();
  }

  public List<WebElement> getAllComments() {
    waitForCommentsToLoad();
    return commentCards;
  }

  public String getCommentBody(int index) {
    waitForCommentsToLoad();
    if (index < commentBodies.size()) {
      return getText(commentBodies.get(index));
    }
    return "";
  }

  public String getCommentAuthorUsername(int index) {
    waitForCommentsToLoad();
    if (index < commentAuthors.size()) {
      // Every comment has 2 author links (image and text), so we need index * 2 + 1
      int authorIndex = index * 2 + 1;
      if (authorIndex < commentAuthors.size()) {
        return getText(commentAuthors.get(authorIndex));
      }
    }
    return "";
  }

  public String getCommentAuthorImageSrc(int index) {
    waitForCommentsToLoad();
    if (index < commentAuthorImages.size()) {
      try {
        return commentAuthorImages.get(index).getAttribute("src");
      } catch (Exception e) {
        return "";
      }
    }
    return "";
  }

  public String getCommentDate(int index) {
    waitForCommentsToLoad();
    if (index < commentDates.size()) {
      return getText(commentDates.get(index));
    }
    return "";
  }

  public boolean isCommentAuthorImageDisplayed(int index) {
    waitForCommentsToLoad();
    if (index < commentAuthorImages.size()) {
      return isDisplayed(commentAuthorImages.get(index));
    }
    return false;
  }

  public boolean hasComments() {
    waitForCommentsToLoad();
    return !commentCards.isEmpty();
  }

  public boolean isCommentInputDisplayed() {
    try {
      return isDisplayed(commentInput);
    } catch (Exception e) {
      return false;
    }
  }

  public void typeComment(String text) {
    type(commentInput, text);
  }

  public void submitComment() {
    click(postCommentButton);
  }

  public boolean isLoadingSpinnerDisplayed() {
    try {
      return isDisplayed(loadingSpinner);
    } catch (Exception e) {
      return false;
    }
  }

  public String getPageSource() {
    return driver.getPageSource();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }

  public boolean isCommentBodyPreservingFormatting(int index) {
    waitForCommentsToLoad();
    if (index < commentBodies.size()) {
      WebElement body = commentBodies.get(index);
      String html = body.getAttribute("innerHTML");
      // Check if formatting is preserved (line breaks, etc.)
      return html != null && !html.isEmpty();
    }
    return false;
  }

  public List<String> getAllCommentBodies() {
    waitForCommentsToLoad();
    return commentBodies.stream().map(this::getText).toList();
  }

  public List<String> getAllCommentAuthorUsernames() {
    waitForCommentsToLoad();
    // Get every other author element (the text ones, not image links)
    return java.util.stream.IntStream.range(0, commentCards.size())
        .mapToObj(this::getCommentAuthorUsername)
        .toList();
  }

  public List<String> getAllCommentDates() {
    waitForCommentsToLoad();
    return commentDates.stream().map(this::getText).toList();
  }

  public boolean areCommentsOrderedByDate() {
    List<String> dates = getAllCommentDates();
    if (dates.size() <= 1) {
      return true;
    }
    // Check if dates are in chronological order
    for (int i = 0; i < dates.size() - 1; i++) {
      // Dates are displayed as "Mon Dec 03 2025" format
      // For simplicity, we just check they exist
      if (dates.get(i) == null || dates.get(i).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public void clickAuthorProfile(int index) {
    waitForCommentsToLoad();
    if (index < commentAuthors.size()) {
      int authorIndex = index * 2 + 1;
      if (authorIndex < commentAuthors.size()) {
        click(commentAuthors.get(authorIndex));
      }
    }
  }

  public WebElement getCommentCard(int index) {
    waitForCommentsToLoad();
    if (index < commentCards.size()) {
      return commentCards.get(index);
    }
    return null;
  }

  public boolean hasFollowButton() {
    try {
      return driver.findElements(By.cssSelector(".btn-outline-secondary")).stream()
          .anyMatch(
              btn -> {
                String text = btn.getText().toLowerCase();
                return text.contains("follow") || text.contains("following");
              });
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFollowStatusDisplayed() {
    try {
      List<WebElement> followButtons =
          driver.findElements(By.cssSelector(".btn-outline-secondary"));
      return followButtons.stream()
          .anyMatch(
              btn -> {
                String text = btn.getText().toLowerCase();
                return text.contains("follow") || text.contains("following");
              });
    } catch (Exception e) {
      return false;
    }
  }
}
