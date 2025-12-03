package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the user profile page. */
public class ProfilePage extends BasePage {

  private static final String PROFILE_URL = "/profile/";

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info .user-img")
  private WebElement userImage;

  @FindBy(css = ".user-info .btn.action-btn")
  private WebElement followUnfollowButton;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement editProfileButton;

  @FindBy(css = ".articles-toggle .nav-link")
  private java.util.List<WebElement> profileTabs;

  @FindBy(css = ".article-preview")
  private java.util.List<WebElement> articlePreviews;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public ProfilePage navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + PROFILE_URL + username);
    return this;
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

  public boolean isUserImageDisplayed() {
    return isDisplayed(userImage);
  }

  public boolean isFollowButtonDisplayed() {
    try {
      return isDisplayed(followUnfollowButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFollowing() {
    try {
      String buttonText = getText(followUnfollowButton);
      return buttonText.contains("Unfollow");
    } catch (Exception e) {
      return false;
    }
  }

  public String getFollowButtonText() {
    try {
      return getText(followUnfollowButton);
    } catch (Exception e) {
      return "";
    }
  }

  public ProfilePage clickFollowButton() {
    click(followUnfollowButton);
    return this;
  }

  public ProfilePage clickUnfollowButton() {
    click(followUnfollowButton);
    return this;
  }

  public ProfilePage follow() {
    if (!isFollowing()) {
      clickFollowButton();
      waitForFollowStateChange(true);
    }
    return this;
  }

  public ProfilePage unfollow() {
    if (isFollowing()) {
      clickUnfollowButton();
      waitForFollowStateChange(false);
    }
    return this;
  }

  public void waitForFollowStateChange(boolean expectFollowing) {
    try {
      if (expectFollowing) {
        wait.until(ExpectedConditions.textToBePresentInElement(followUnfollowButton, "Unfollow"));
      } else {
        wait.until(ExpectedConditions.textToBePresentInElement(followUnfollowButton, "Follow"));
      }
    } catch (Exception e) {
      // State may have already changed
    }
  }

  public boolean isEditProfileButtonDisplayed() {
    try {
      return isDisplayed(editProfileButton);
    } catch (Exception e) {
      return false;
    }
  }

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public void clickMyArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("My Articles")) {
        click(tab);
        break;
      }
    }
  }

  public void clickFavoritedArticlesTab() {
    for (WebElement tab : profileTabs) {
      if (tab.getText().contains("Favorited Articles")) {
        click(tab);
        break;
      }
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public boolean isProfilePageDisplayed() {
    try {
      return isDisplayed(usernameHeader);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorDisplayed() {
    try {
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isProfileNotFound() {
    try {
      WebElement errorElement =
          driver.findElement(By.cssSelector(".error-messages, .error-message"));
      String errorText = errorElement.getText().toLowerCase();
      return errorText.contains("not found") || errorText.contains("can't load");
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForProfileLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.visibilityOf(usernameHeader),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".error-messages"))));
  }

  public boolean isFollowButtonEnabled() {
    try {
      return followUnfollowButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public String getFollowButtonClass() {
    try {
      return followUnfollowButton.getAttribute("class");
    } catch (Exception e) {
      return "";
    }
  }

  public void refreshPage() {
    driver.navigate().refresh();
  }
}
