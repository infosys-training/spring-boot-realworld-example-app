package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
  private WebElement updateSettingsButton;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "h1")
  private WebElement pageTitle;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToSettingsPage(String baseUrl) {
    driver.get(baseUrl + "/settings");
  }

  public boolean isSettingsPageDisplayed() {
    try {
      return usernameInput.isDisplayed() && emailInput.isDisplayed();
    } catch (Exception e) {
      return false;
    }
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

  public void clickUpdateSettings() {
    click(updateSettingsButton);
  }

  public void clickLogout() {
    click(logoutButton);
  }

  public void updateProfile(String imageUrl, String username, String bio) {
    if (imageUrl != null && !imageUrl.isEmpty()) {
      enterImageUrl(imageUrl);
    }
    if (username != null && !username.isEmpty()) {
      enterUsername(username);
    }
    if (bio != null && !bio.isEmpty()) {
      enterBio(bio);
    }
    clickUpdateSettings();
  }

  public String getCurrentUsername() {
    return usernameInput.getAttribute("value");
  }

  public String getCurrentEmail() {
    return emailInput.getAttribute("value");
  }

  public String getCurrentBio() {
    return bioTextarea.getAttribute("value");
  }

  public String getCurrentImageUrl() {
    return imageUrlInput.getAttribute("value");
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    return getText(errorMessages);
  }

  public String getPageTitleText() {
    return getText(pageTitle);
  }

  public boolean isLogoutButtonDisplayed() {
    try {
      return logoutButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
