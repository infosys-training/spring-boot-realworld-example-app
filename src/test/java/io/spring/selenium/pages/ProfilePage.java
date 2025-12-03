package io.spring.selenium.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Page Object for the Profile page with follow functionality. */
public class ProfilePage extends BasePage {

  private static final String PROFILE_URL = "http://localhost:3000/profile/";

  @FindBy(css = ".user-info h4")
  private WebElement usernameHeader;

  @FindBy(css = ".user-info p")
  private WebElement userBio;

  @FindBy(css = ".user-info .user-img")
  private WebElement userImage;

  @FindBy(css = ".user-info .btn.action-btn")
  private WebElement followButton;

  @FindBy(css = ".user-info .btn-outline-secondary")
  private WebElement editProfileButton;

  @FindBy(css = ".articles-toggle")
  private WebElement articlesToggle;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".nav-link.active")
  private WebElement activeTab;

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String username) {
    driver.get(PROFILE_URL + username);
    waitForPageLoad();
  }

  public void waitForPageLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOf(usernameHeader));
    } catch (Exception e) {
      // Page might show error instead
    }
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
      return getText(userBio);
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

  public boolean isFollowButtonEnabled() {
    try {
      return followButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
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

  public boolean isFollowing() {
    String buttonText = getFollowButtonText();
    return buttonText.contains("Unfollow");
  }

  public boolean isNotFollowing() {
    String buttonText = getFollowButtonText();
    return buttonText.contains("Follow") && !buttonText.contains("Unfollow");
  }

  public void followUser() {
    if (isNotFollowing()) {
      clickFollowButton();
      waitForFollowStateChange(true);
    }
  }

  public void unfollowUser() {
    if (isFollowing()) {
      clickFollowButton();
      waitForFollowStateChange(false);
    }
  }

  public void waitForFollowStateChange(boolean expectFollowing) {
    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5).getSeconds());
    try {
      if (expectFollowing) {
        shortWait.until(
            d -> {
              try {
                return getFollowButtonText().contains("Unfollow");
              } catch (Exception e) {
                return false;
              }
            });
      } else {
        shortWait.until(
            d -> {
              try {
                String text = getFollowButtonText();
                return text.contains("Follow") && !text.contains("Unfollow");
              } catch (Exception e) {
                return false;
              }
            });
      }
    } catch (Exception e) {
      // Timeout - state might not have changed
    }
  }

  public boolean isEditProfileButtonDisplayed() {
    try {
      return editProfileButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnOwnProfile() {
    return isEditProfileButtonDisplayed() && !isFollowButtonDisplayed();
  }

  public boolean hasError() {
    try {
      return errorMessage.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      return getText(errorMessage);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isProfileLoaded() {
    try {
      return usernameHeader.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public boolean is404Error() {
    String pageSource = driver.getPageSource();
    return pageSource.contains("404")
        || pageSource.contains("Not Found")
        || pageSource.contains("Can't load profile");
  }

  public boolean isUnauthorizedError() {
    String pageSource = driver.getPageSource();
    return pageSource.contains("401") || pageSource.contains("Unauthorized");
  }

  public void refreshPage() {
    driver.navigate().refresh();
    waitForPageLoad();
  }

  public String getFollowButtonClass() {
    try {
      return followButton.getAttribute("class");
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isFollowButtonSecondary() {
    return getFollowButtonClass().contains("btn-secondary");
  }

  public boolean isFollowButtonOutlineSecondary() {
    return getFollowButtonClass().contains("btn-outline-secondary");
  }

  public void waitForButtonStateUpdate() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public WebElement getFollowButtonElement() {
    return followButton;
  }

  public long measureFollowResponseTime() {
    long startTime = System.currentTimeMillis();
    clickFollowButton();
    waitForFollowStateChange(!isFollowing());
    return System.currentTimeMillis() - startTime;
  }

  public void scrollToFollowButton() {
    try {
      ((org.openqa.selenium.JavascriptExecutor) driver)
          .executeScript("arguments[0].scrollIntoView(true);", followButton);
    } catch (Exception e) {
      // Element might not exist
    }
  }

  public long getScrollPosition() {
    return (Long)
        ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return window.pageYOffset;");
  }

  public void scrollDown(int pixels) {
    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript("window.scrollBy(0, " + pixels + ");");
  }

  public boolean hasAriaLabel() {
    try {
      String ariaLabel = followButton.getAttribute("aria-label");
      return ariaLabel != null && !ariaLabel.isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  public void navigateBack() {
    driver.navigate().back();
  }

  public void navigateForward() {
    driver.navigate().forward();
  }
}
