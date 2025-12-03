package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the User Profile page. */
public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement username;

  @FindBy(css = ".user-info p")
  private WebElement bio;

  @FindBy(css = ".user-info img")
  private WebElement profileImage;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".btn-secondary")
  private WebElement unfollowButton;

  @FindBy(css = "a[href*='/user/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  @FindBy(css = ".nav-pills .nav-link")
  private List<WebElement> profileTabs;

  @FindBy(css = ".pagination .page-item")
  private List<WebElement> paginationItems;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String usernameParam) {
    driver.get(getBaseUrl() + "/profile/" + usernameParam);
  }

  public String getUsername() {
    return getText(username);
  }

  public String getBio() {
    try {
      return getText(bio);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isFollowButtonVisible() {
    try {
      return isDisplayed(followButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUnfollowButtonVisible() {
    try {
      return isDisplayed(unfollowButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditProfileButtonVisible() {
    try {
      return isDisplayed(editProfileButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFollow() {
    click(followButton);
  }

  public void clickUnfollow() {
    click(unfollowButton);
  }

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickMyArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("My Articles")) {
        click(tab);
        return;
      }
    }
  }

  public void clickFavoritedArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited Articles")) {
        click(tab);
        return;
      }
    }
  }

  public boolean isArticleVisible(String title) {
    for (WebElement titleElement : articleTitles) {
      if (titleElement.getText().equals(title)) {
        return true;
      }
    }
    return false;
  }

  public void clickArticleByTitle(String title) {
    for (WebElement preview : articlePreviews) {
      WebElement titleElement = preview.findElement(By.cssSelector("h1"));
      if (titleElement.getText().equals(title)) {
        click(titleElement);
        return;
      }
    }
    throw new RuntimeException("Article with title '" + title + "' not found");
  }

  public void waitForProfileLoad() {
    wait.until(ExpectedConditions.visibilityOf(username));
  }

  public void waitForArticlesToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.visibilityOfAllElements(articlePreviews),
            ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".article-preview"), "No articles")));
  }

  public List<String> getArticleTitles() {
    return articleTitles.stream()
        .map(WebElement::getText)
        .collect(java.util.stream.Collectors.toList());
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
