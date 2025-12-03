package io.spring.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfilePage extends BasePage {

  private static final String BASE_URL = "http://localhost:3000";

  @FindBy(css = ".profile-page .user-info h4")
  private WebElement username;

  @FindBy(css = ".profile-page .user-info p")
  private WebElement bio;

  @FindBy(css = ".profile-page .user-img")
  private WebElement profileImage;

  @FindBy(css = ".error-message")
  private WebElement errorMessage;

  @FindBy(css = ".btn-outline-secondary")
  private WebElement followButton;

  public ProfilePage(WebDriver driver) {
    super(driver);
  }

  public void navigateToProfile(String usernameParam) {
    driver.get(BASE_URL + "/profile/" + usernameParam);
  }

  public void navigateToProfileWithEncodedUsername(String encodedUsername) {
    driver.get(BASE_URL + "/profile/" + encodedUsername);
  }

  public String getPageTitle() {
    return driver.getTitle();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public boolean isProfileDisplayed() {
    try {
      WebElement profile =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".profile-page .user-info")));
      return profile.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isErrorMessageDisplayed() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error']")));
      return error.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessageText() {
    try {
      WebElement error =
          wait.until(
              ExpectedConditions.presenceOfElementLocated(
                  By.cssSelector(".error-message, .error, [class*='error']")));
      return error.getText();
    } catch (Exception e) {
      return "";
    }
  }

  public String getDisplayedUsername() {
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

  public boolean isPageNotFound() {
    String pageSource = driver.getPageSource().toLowerCase();
    String currentUrl = driver.getCurrentUrl().toLowerCase();
    return pageSource.contains("not found")
        || pageSource.contains("404")
        || pageSource.contains("error")
        || pageSource.contains("can't load")
        || currentUrl.contains("404")
        || currentUrl.contains("error");
  }

  public boolean hasErrorInPageSource() {
    String pageSource = driver.getPageSource().toLowerCase();
    return pageSource.contains("error")
        || pageSource.contains("not found")
        || pageSource.contains("404")
        || pageSource.contains("cannot read")
        || pageSource.contains("can't load");
  }

  public boolean isFollowButtonDisplayed() {
    try {
      return followButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageLoad() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
