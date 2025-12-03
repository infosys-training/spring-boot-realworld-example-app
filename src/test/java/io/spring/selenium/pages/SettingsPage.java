package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Settings page. */
public class SettingsPage extends BasePage {

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

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

  @FindBy(css = ".btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessage;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/user/settings");
  }

  public String getPageTitleText() {
    return getText(pageTitle);
  }

  public String getImageUrl() {
    return waitForVisibility(imageUrlInput).getAttribute("value");
  }

  public void setImageUrl(String url) {
    type(imageUrlInput, url);
  }

  public void clearImageUrl() {
    WebElement input = waitForVisibility(imageUrlInput);
    input.clear();
  }

  public String getUsername() {
    return waitForVisibility(usernameInput).getAttribute("value");
  }

  public void setUsername(String username) {
    type(usernameInput, username);
  }

  public String getBio() {
    return waitForVisibility(bioTextarea).getAttribute("value");
  }

  public void setBio(String bio) {
    type(bioTextarea, bio);
  }

  public String getEmail() {
    return waitForVisibility(emailInput).getAttribute("value");
  }

  public void setEmail(String email) {
    type(emailInput, email);
  }

  public void setPassword(String password) {
    type(passwordInput, password);
  }

  public void clickUpdateSettings() {
    click(updateSettingsButton);
  }

  public void clickLogout() {
    click(logoutButton);
  }

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessage);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isImageUrlInputDisplayed() {
    return isDisplayed(imageUrlInput);
  }

  public boolean isUsernameInputDisplayed() {
    return isDisplayed(usernameInput);
  }

  public boolean isBioTextareaDisplayed() {
    return isDisplayed(bioTextarea);
  }

  public boolean isEmailInputDisplayed() {
    return isDisplayed(emailInput);
  }

  public boolean isPasswordInputDisplayed() {
    return isDisplayed(passwordInput);
  }

  public boolean isUpdateSettingsButtonDisplayed() {
    return isDisplayed(updateSettingsButton);
  }

  public boolean isLogoutButtonDisplayed() {
    return isDisplayed(logoutButton);
  }

  public void updateImageUrl(String newUrl) {
    clearImageUrl();
    setImageUrl(newUrl);
    clickUpdateSettings();
  }

  public boolean verifyImageUrlMatchesDefault(String defaultImageUrl) {
    String currentUrl = getImageUrl();
    return currentUrl != null && currentUrl.equals(defaultImageUrl);
  }

  public int getImageUrlLength() {
    String url = getImageUrl();
    return url != null ? url.length() : 0;
  }
}
