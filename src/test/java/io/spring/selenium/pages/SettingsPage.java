package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the Settings page where users update their profile. */
public class SettingsPage extends BasePage {

  @FindBy(css = "input[placeholder='URL of profile picture']")
  private WebElement imageUrlInput;

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "textarea[placeholder='Short bio about you']")
  private WebElement bioTextarea;

  @FindBy(css = "input[placeholder='Email']")
  private WebElement emailInput;

  @FindBy(css = "input[placeholder='New Password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement updateButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(linkText = "Settings")
  private WebElement settingsLink;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/settings");
  }

  public void clickSettingsLink() {
    click(settingsLink);
  }

  public void enterImageUrl(String imageUrl) {
    type(imageUrlInput, imageUrl);
  }

  public void enterUsername(String username) {
    type(usernameInput, username);
  }

  public void enterBio(String bio) {
    type(bioTextarea, bio);
  }

  public void enterEmail(String email) {
    type(emailInput, email);
  }

  public void enterPassword(String password) {
    type(passwordInput, password);
  }

  public void clickUpdate() {
    click(updateButton);
  }

  public void clickLogout() {
    click(logoutButton);
  }

  public void updateProfile(
      String imageUrl, String username, String bio, String email, String password) {
    if (imageUrl != null) {
      enterImageUrl(imageUrl);
    }
    if (username != null) {
      enterUsername(username);
    }
    if (bio != null) {
      enterBio(bio);
    }
    if (email != null) {
      enterEmail(email);
    }
    if (password != null) {
      enterPassword(password);
    }
    clickUpdate();
  }

  public void updateEmail(String email) {
    enterEmail(email);
    clickUpdate();
  }

  public void updateUsername(String username) {
    enterUsername(username);
    clickUpdate();
  }

  public void updatePassword(String password) {
    enterPassword(password);
    clickUpdate();
  }

  public void updateBio(String bio) {
    enterBio(bio);
    clickUpdate();
  }

  public void updateImageUrl(String imageUrl) {
    enterImageUrl(imageUrl);
    clickUpdate();
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

  public boolean isOnSettingsPage() {
    try {
      return driver.getCurrentUrl().contains("/settings");
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentUsername() {
    try {
      waitForVisibility(usernameInput);
      return usernameInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentEmail() {
    try {
      waitForVisibility(emailInput);
      return emailInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentBio() {
    try {
      waitForVisibility(bioTextarea);
      return bioTextarea.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentImageUrl() {
    try {
      waitForVisibility(imageUrlInput);
      return imageUrlInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public void clearEmailField() {
    waitForVisibility(emailInput);
    emailInput.clear();
  }

  public void clearUsernameField() {
    waitForVisibility(usernameInput);
    usernameInput.clear();
  }

  public void clearBioField() {
    waitForVisibility(bioTextarea);
    bioTextarea.clear();
  }

  public void clearImageUrlField() {
    waitForVisibility(imageUrlInput);
    imageUrlInput.clear();
  }

  public void clearPasswordField() {
    waitForVisibility(passwordInput);
    passwordInput.clear();
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(updateButton));
  }

  public boolean isUpdateButtonEnabled() {
    try {
      return updateButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForSuccessfulUpdate() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
