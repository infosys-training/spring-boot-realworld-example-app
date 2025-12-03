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

  @FindBy(css = ".article-meta")
  private WebElement articleMeta;

  @FindBy(css = ".article-meta .author")
  private WebElement authorLink;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> tags;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".comment-form")
  private WebElement commentForm;

  @FindBy(css = ".card.comment")
  private List<WebElement> comments;

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String slug) {
    driver.get(baseUrl + "/article/" + slug);
  }

  public boolean isOnArticlePage() {
    try {
      return waitForVisibility(articleTitle).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return null;
    }
  }

  public String getAuthorUsername() {
    try {
      return getText(authorLink);
    } catch (Exception e) {
      return null;
    }
  }

  public String getArticleDate() {
    try {
      return getText(articleDate);
    } catch (Exception e) {
      return null;
    }
  }

  public List<String> getTags() {
    try {
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".tag-list .tag-pill")));
      return tags.stream().map(WebElement::getText).collect(java.util.stream.Collectors.toList());
    } catch (Exception e) {
      return java.util.Collections.emptyList();
    }
  }

  public boolean hasEditButton() {
    try {
      return isDisplayed(editButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasDeleteButton() {
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

  public int getCommentCount() {
    try {
      return comments.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean hasCommentForm() {
    try {
      return isDisplayed(commentForm);
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageToLoad() {
    wait.until(ExpectedConditions.visibilityOf(articleTitle));
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getSlugFromUrl() {
    String url = getCurrentUrl();
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
