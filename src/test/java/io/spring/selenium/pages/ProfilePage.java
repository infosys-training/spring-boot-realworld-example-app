package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info img")
  private WebElement userImage;

  @FindBy(css = "button.btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = "a.btn-outline-secondary[href*='/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".articles-toggle .nav-link")
  private List<WebElement> articleTabs;

  @FindBy(linkText = "My Articles")
  private WebElement myArticlesTab;

  @FindBy(linkText = "Favorited Articles")
  private WebElement favoritedArticlesTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToProfile(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
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

  public String getBio() {
    try {
      return getText(userBio);
    } catch (Exception e) {
      return "";
    }
  }

  public String getImageUrl() {
    try {
      return userImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public void clickFollow() {
    click(followButton);
  }

  public String getFollowButtonText() {
    return getText(followButton);
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

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public void clickMyArticles() {
    click(myArticlesTab);
  }

  public void clickFavoritedArticles() {
    click(favoritedArticlesTab);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public boolean hasArticles() {
    return !articlePreviews.isEmpty();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isOwnProfile() {
    return isEditProfileButtonDisplayed();
  }
}
