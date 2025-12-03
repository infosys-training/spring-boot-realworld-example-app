package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticlePage extends BasePage {

  @FindBy(css = ".article-page .banner h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-page .article-content")
  private WebElement articleBody;

  @FindBy(css = ".article-page .tag-list")
  private WebElement tagList;

  @FindBy(css = ".article-page .tag-list li")
  private List<WebElement> tags;

  @FindBy(css = ".article-meta .author")
  private WebElement authorUsername;

  @FindBy(css = ".article-meta img")
  private WebElement authorImage;

  @FindBy(css = ".article-meta .date")
  private WebElement articleDate;

  @FindBy(css = ".article-meta .btn-outline-secondary")
  private WebElement editButton;

  @FindBy(css = ".article-meta .btn-outline-danger")
  private WebElement deleteButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement favoriteButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".error-page")
  private WebElement errorPage;

  @FindBy(css = ".article-page")
  private WebElement articlePageContainer;

  private static final String BASE_URL = "http://localhost:3000";

  public ArticlePage(WebDriver driver) {
    super(driver);
  }

  public ArticlePage navigateToArticle(String slug) {
    driver.get(BASE_URL + "/article/" + slug);
    return this;
  }

  public ArticlePage navigateToArticleUrl(String url) {
    driver.get(url);
    return this;
  }

  public boolean isArticlePageDisplayed() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(articlePageContainer)).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorPageDisplayed() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(errorPage)).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getArticleTitle() {
    try {
      return getText(articleTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public String getArticleBody() {
    try {
      return getText(articleBody);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isArticleBodyDisplayed() {
    try {
      return waitForVisibility(articleBody).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isTagListDisplayed() {
    try {
      return isDisplayed(tagList);
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getArticleTags() {
    try {
      wait.until(ExpectedConditions.visibilityOfAllElements(tags));
      return tags.stream().map(WebElement::getText).collect(Collectors.toList());
    } catch (Exception e) {
      return List.of();
    }
  }

  public int getTagCount() {
    try {
      return tags.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public String getAuthorUsername() {
    try {
      return getText(authorUsername);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isAuthorImageDisplayed() {
    try {
      return isDisplayed(authorImage);
    } catch (Exception e) {
      return false;
    }
  }

  public String getAuthorImageSrc() {
    try {
      return authorImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public String getArticleDate() {
    try {
      return getText(articleDate);
    } catch (Exception e) {
      return "";
    }
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

  public boolean isFavoriteButtonDisplayed() {
    try {
      return isDisplayed(favoriteButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFollowButtonDisplayed() {
    try {
      return isDisplayed(followButton);
    } catch (Exception e) {
      return false;
    }
  }

  public String getFavoriteButtonText() {
    try {
      return getText(favoriteButton);
    } catch (Exception e) {
      return "";
    }
  }

  public String getFollowButtonText() {
    try {
      return getText(followButton);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isFavoriteButtonActive() {
    try {
      String className = favoriteButton.getAttribute("class");
      return className != null && className.contains("btn-primary");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFollowButtonActive() {
    try {
      String className = followButton.getAttribute("class");
      return className != null && className.contains("btn-secondary");
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFavoriteButton() {
    click(favoriteButton);
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public void clickEditButton() {
    click(editButton);
  }

  public void clickDeleteButton() {
    click(deleteButton);
  }

  public void clickAuthorUsername() {
    click(authorUsername);
  }

  public void clickTag(String tagName) {
    for (WebElement tag : tags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public boolean urlContainsSlug(String slug) {
    return driver.getCurrentUrl().contains("/article/" + slug);
  }

  public boolean isPageLoaded() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOf(articlePageContainer),
              ExpectedConditions.visibilityOf(errorPage)));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasBodyContent() {
    String body = getArticleBody();
    return body != null && !body.trim().isEmpty();
  }

  public boolean isMarkdownRendered() {
    try {
      WebElement bodyContent = articleBody.findElement(By.cssSelector("div"));
      String innerHTML = bodyContent.getAttribute("innerHTML");
      return innerHTML != null
          && (innerHTML.contains("<p>")
              || innerHTML.contains("<h")
              || innerHTML.contains("<strong>")
              || innerHTML.contains("<em>")
              || innerHTML.contains("<ul>")
              || innerHTML.contains("<ol>"));
    } catch (Exception e) {
      return false;
    }
  }
}
