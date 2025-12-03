package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the User Profile page. */
public class ProfilePage extends BasePage {

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement bioText;

  @FindBy(css = ".user-info img")
  private WebElement profileImage;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement editProfileButton;

  @FindBy(css = ".btn-outline-primary")
  private WebElement followButton;

  @FindBy(linkText = "My Articles")
  private WebElement myArticlesTab;

  @FindBy(linkText = "Favorited Articles")
  private WebElement favoritedArticlesTab;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl, String username) {
    driver.get(baseUrl + "/profile/" + username);
  }

  public String getDisplayedUsername() {
    try {
      return getText(usernameHeader);
    } catch (Exception e) {
      return "";
    }
  }

  public String getDisplayedBio() {
    try {
      return getText(bioText);
    } catch (Exception e) {
      return "";
    }
  }

  public String getProfileImageSrc() {
    try {
      waitForVisibility(profileImage);
      return profileImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public void clickEditProfile() {
    click(editProfileButton);
  }

  public void clickFollow() {
    click(followButton);
  }

  public void clickMyArticles() {
    click(myArticlesTab);
  }

  public void clickFavoritedArticles() {
    click(favoritedArticlesTab);
  }

  public boolean isEditProfileButtonDisplayed() {
    try {
      return isDisplayed(editProfileButton);
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

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(usernameHeader));
  }

  public boolean isOnProfilePage() {
    try {
      return driver.getCurrentUrl().contains("/profile/");
    } catch (Exception e) {
      return false;
    }
  }
}
