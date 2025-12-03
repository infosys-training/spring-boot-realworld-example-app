package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

  @FindBy(css = ".profile-page")
  private WebElement profilePageContainer;

  @FindBy(css = ".user-info")
  private WebElement userInfoSection;

  @FindBy(css = ".user-img")
  private WebElement profileImage;

  @FindBy(css = ".user-info h4")
  private WebElement usernameElement;

  @FindBy(css = ".user-info p")
  private WebElement bioElement;

  @FindBy(css = ".btn.action-btn")
  private WebElement followButton;

  @FindBy(css = ".articles-toggle")
  private WebElement articlesToggle;

  @FindBy(css = ".nav-pills .nav-item")
  private List<WebElement> profileTabs;

  @FindBy(css = ".error-content")
  private WebElement errorMessage;

  @FindBy(css = ".error-container")
  private WebElement errorContainer;

  @FindBy(css = "a[href='/settings']")
  private WebElement editProfileButton;

  private static final String BASE_URL = "http://localhost:3000";

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToProfile(String username) {
    driver.get(BASE_URL + "/profile/" + username);
    waitForPageLoad();
  }

  public void navigateToProfileWithEncodedUsername(String encodedUsername) {
    driver.get(BASE_URL + "/profile/" + encodedUsername);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(
          ExpectedConditions.or(
              ExpectedConditions.visibilityOf(profilePageContainer),
              ExpectedConditions.visibilityOf(errorContainer)));
    } catch (Exception e) {
      // Page might have loaded with different content
    }
  }

  public boolean isProfilePageDisplayed() {
    try {
      return profilePageContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserInfoSectionDisplayed() {
    try {
      return userInfoSection.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsername() {
    try {
      return waitForVisibility(usernameElement).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getBio() {
    try {
      return waitForVisibility(bioElement).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isProfileImageDisplayed() {
    try {
      return profileImage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getProfileImageSrc() {
    try {
      WebElement img = waitForVisibility(profileImage);
      String src = img.getAttribute("src");
      if (src == null || src.isEmpty()) {
        src = img.getAttribute("data-src");
      }
      return src != null ? src : "";
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

  public String getFollowButtonText() {
    try {
      return waitForVisibility(followButton).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public void clickFollowButton() {
    click(followButton);
  }

  public boolean isFollowing() {
    String buttonText = getFollowButtonText();
    return buttonText.toLowerCase().contains("unfollow");
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

  public boolean isArticlesToggleDisplayed() {
    try {
      return articlesToggle.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getProfileTabsCount() {
    try {
      return profileTabs.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean isMyArticlesTabDisplayed() {
    try {
      for (WebElement tab : profileTabs) {
        if (tab.getText().contains("My Articles")) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isFavoritedArticlesTabDisplayed() {
    try {
      for (WebElement tab : profileTabs) {
        if (tab.getText().contains("Favorited Articles")) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
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

  public boolean isErrorMessageDisplayed() {
    try {
      return errorMessage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      return waitForVisibility(errorMessage).getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public boolean hasFollowButtonClass(String className) {
    try {
      String classes = followButton.getAttribute("class");
      return classes != null && classes.contains(className);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isBioEmpty() {
    String bio = getBio();
    return bio == null || bio.trim().isEmpty();
  }

  public boolean isPageLoadedWithinTimeout(long timeoutSeconds) {
    long startTime = System.currentTimeMillis();
    waitForPageLoad();
    long endTime = System.currentTimeMillis();
    return (endTime - startTime) < (timeoutSeconds * 1000);
  }

  public boolean hasValidImageSource() {
    String src = getProfileImageSrc();
    return src != null && !src.isEmpty() && (src.startsWith("http") || src.startsWith("data:"));
  }

  public boolean isElementKeyboardAccessible(WebElement element) {
    try {
      element.sendKeys("");
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<WebElement> getAllInteractiveElements() {
    return driver.findElements(By.cssSelector("a, button, input, [tabindex]"));
  }
}
