package io.spring.selenium.pages;

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

  @FindBy(css = ".article-preview")
  private java.util.List<WebElement> articlePreviews;

  private static final String BASE_URL = "http://localhost:3000";

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToProfile(String usernameParam) {
    driver.get(BASE_URL + "/profile/" + usernameParam);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-info")),
              ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".profile-page"))));
    } catch (Exception e) {
      // Page may have loaded with different structure
    }
  }

  public String getUsername() {
    try {
      return getText(username);
    } catch (Exception e) {
      return "";
    }
  }

  public String getBio() {
    try {
      return getText(bio);
    } catch (Exception e) {
      return "";
    }
  }

  public String getProfileImageSrc() {
    try {
      return profileImage.getAttribute("src");
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isProfileImageDisplayed() {
    try {
      return isDisplayed(profileImage);
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

  public String getFollowButtonText() {
    try {
      return getText(followButton);
    } catch (Exception e) {
      return "";
    }
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public boolean isFollowing() {
    try {
      String buttonText = getFollowButtonText().toLowerCase();
      return buttonText.contains("unfollow") || buttonText.contains("following");
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public boolean hasBio() {
    try {
      String bioText = getBio();
      return bioText != null && !bioText.trim().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }
}
