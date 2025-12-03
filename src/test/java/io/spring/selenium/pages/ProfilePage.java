package io.spring.selenium.pages;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info img.user-img")
  private WebElement userImage;

  @FindBy(css = ".articles-toggle .nav-pills .nav-link")
  private List<WebElement> profileTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview")
  private WebElement firstArticlePreview;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  @FindBy(css = "button.btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".article-preview .article-meta .author")
  private List<WebElement> articleAuthors;

  @FindBy(css = ".article-preview .tag-list .tag-pill")
  private List<WebElement> articleTags;

  @FindBy(css = ".article-preview .pull-xs-right")
  private List<WebElement> favoriteButtons;

  private static final String NO_ARTICLES_MESSAGE = "No articles are here... yet.";

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
    waitForPageLoad();
  }

  public void navigateToFavoritedArticles(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username + "?favorite=true");
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(usernameHeader));
  }

  public void waitForArticlesLoad() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".article-preview")),
              ExpectedConditions.textToBePresentInElementLocated(
                  By.cssSelector(".article-preview"), NO_ARTICLES_MESSAGE)));
    } catch (Exception e) {
      // Articles may not be present
    }
  }

  public boolean isProfilePageDisplayed() {
    try {
      return usernameHeader.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsername() {
    return getText(usernameHeader);
  }

  public String getUserBio() {
    try {
      return getText(userBio);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isUserImageDisplayed() {
    try {
      return userImage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<WebElement> getProfileTabs() {
    return profileTabs;
  }

  public void clickMyArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("My Articles")) {
        click(tab);
        waitForArticlesLoad();
        break;
      }
    }
  }

  public void clickFavoritedArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited Articles")) {
        click(tab);
        waitForArticlesLoad();
        break;
      }
    }
  }

  public boolean isMyArticlesTabActive() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("My Articles")) {
        return tab.getAttribute("class").contains("active");
      }
    }
    return false;
  }

  public boolean isFavoritedArticlesTabActive() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited Articles")) {
        return tab.getAttribute("class").contains("active");
      }
    }
    return false;
  }

  public boolean isFavoritedArticlesTabDisplayed() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited Articles")) {
        return tab.isDisplayed();
      }
    }
    return false;
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public int getArticleCount() {
    try {
      if (isNoArticlesMessageDisplayed()) {
        return 0;
      }
      return articlePreviews.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean isNoArticlesMessageDisplayed() {
    try {
      WebElement preview = driver.findElement(By.cssSelector(".article-preview"));
      return preview.getText().contains(NO_ARTICLES_MESSAGE);
    } catch (Exception e) {
      return false;
    }
  }

  public String getNoArticlesMessage() {
    try {
      WebElement preview = driver.findElement(By.cssSelector(".article-preview"));
      return preview.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public void clickArticle(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement titleLink = article.findElement(By.cssSelector("a.preview-link h1"));
      click(titleLink);
    }
  }

  public String getArticleTitle(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      return article.findElement(By.cssSelector("a.preview-link h1")).getText();
    }
    return null;
  }

  public String getArticleDescription(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      return article.findElement(By.cssSelector("a.preview-link p")).getText();
    }
    return null;
  }

  public String getArticleAuthor(int index) {
    if (index < articleAuthors.size()) {
      return articleAuthors.get(index).getText();
    }
    return null;
  }

  public String getArticleDate(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      return article.findElement(By.cssSelector(".article-meta .date")).getText();
    }
    return null;
  }

  public List<String> getArticleTags(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      List<WebElement> tags = article.findElements(By.cssSelector(".tag-list .tag-pill"));
      return tags.stream().map(WebElement::getText).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public int getArticleFavoriteCount(int index) {
    if (index < favoriteButtons.size()) {
      String text = favoriteButtons.get(index).getText().trim();
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  public void clickFavoriteButton(int index) {
    if (index < favoriteButtons.size()) {
      click(favoriteButtons.get(index));
    }
  }

  public boolean isArticleFavorited(int index) {
    if (index < favoriteButtons.size()) {
      String classes = favoriteButtons.get(index).getAttribute("class");
      return classes.contains("btn-primary");
    }
    return false;
  }

  public List<WebElement> getPaginationItems() {
    return paginationItems;
  }

  public boolean isPaginationDisplayed() {
    return !paginationItems.isEmpty();
  }

  public int getPaginationPageCount() {
    return paginationItems.size();
  }

  public void clickPaginationPage(int pageNumber) {
    for (WebElement item : paginationItems) {
      WebElement link = item.findElement(By.cssSelector("a.page-link"));
      if (link.getText().equals(String.valueOf(pageNumber))) {
        click(link);
        waitForArticlesLoad();
        break;
      }
    }
  }

  public int getCurrentPage() {
    for (WebElement item : paginationItems) {
      if (item.getAttribute("class").contains("active")) {
        return Integer.parseInt(item.findElement(By.cssSelector("a.page-link")).getText());
      }
    }
    return 1;
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public String getFollowButtonText() {
    try {
      return followButton.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isFollowButtonDisplayed() {
    try {
      return followButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditProfileButtonDisplayed() {
    try {
      return editProfileButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickEditProfileButton() {
    click(editProfileButton);
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean urlContainsFavoriteParameter() {
    return driver.getCurrentUrl().contains("favorite=true");
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }
}
