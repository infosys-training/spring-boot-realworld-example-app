package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Profile page. */
public class ProfilePage extends BasePage {

  @FindBy(css = ".user-img")
  private WebElement profileImage;

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  @FindBy(css = ".articles-toggle .nav-link")
  private List<WebElement> profileTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview .author img")
  private List<WebElement> articleAuthorImages;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
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
      return waitForVisibility(profileImage).getAttribute("src");
    } catch (Exception e) {
      return null;
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

  public boolean isEditProfileButtonDisplayed() {
    return isDisplayed(editProfileButton);
  }

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public boolean isFollowButtonDisplayed() {
    return isDisplayed(followButton);
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public String getFollowButtonText() {
    return getText(followButton);
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

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticleAuthorImages() {
    return articleAuthorImages;
  }

  public boolean verifyProfileImageMatchesDefault(String defaultImageUrl) {
    String currentSrc = getProfileImageSrc();
    return currentSrc != null && currentSrc.contains(defaultImageUrl);
  }

  public boolean isProfileImageLoaded() {
    try {
      WebElement img = waitForVisibility(profileImage);
      String naturalWidth =
          img.getAttribute("naturalWidth") != null ? img.getAttribute("naturalWidth") : "0";
      return Integer.parseInt(naturalWidth) > 0;
    } catch (Exception e) {
      return false;
    }
  }
}
