package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement bioText;

  @FindBy(css = ".user-info img")
  private WebElement profileImage;

  @FindBy(css = "a[href*='/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".nav-link[href*='author']")
  private WebElement myArticlesTab;

  @FindBy(css = ".nav-link[href*='favorites']")
  private WebElement favoritedArticlesTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview h1")
  private List<WebElement> articleTitles;

  @FindBy(css = "button.btn-outline-secondary")
  private WebElement followButton;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String username) {
    driver.get(getBaseUrl() + "/profile/" + username);
  }

  public String getUsername() {
    try {
      return getText(usernameHeader);
    } catch (Exception e) {
      return "";
    }
  }

  public String getBio() {
    try {
      return getText(bioText);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isProfileImageDisplayed() {
    try {
      return waitForVisibility(profileImage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getProfileImageSrc() {
    try {
      return profileImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public void clickMyArticlesTab() {
    click(myArticlesTab);
  }

  public void clickFavoritedArticlesTab() {
    click(favoritedArticlesTab);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public boolean isMyArticlesTabActive() {
    try {
      String classes = myArticlesTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFavoritedArticlesTabActive() {
    try {
      String classes = favoritedArticlesTab.getAttribute("class");
      return classes != null && classes.contains("active");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isEditProfileButtonDisplayed() {
    return isDisplayed(editProfileButton);
  }

  public boolean isFollowButtonDisplayed() {
    return isDisplayed(followButton);
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public String getFollowButtonText() {
    try {
      return getText(followButton);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isPageLoaded() {
    try {
      return isDisplayed(usernameHeader);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasNoArticlesMessage() {
    try {
      for (WebElement preview : articlePreviews) {
        if (preview.getText().contains("No articles")) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
