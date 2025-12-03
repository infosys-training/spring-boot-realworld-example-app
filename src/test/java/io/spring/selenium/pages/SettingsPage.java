package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page Object for the Settings page. */
public class SettingsPage extends BasePage {

  @FindBy(css = "input[placeholder='URL of profile picture']")
  private WebElement imageUrlInput;

  @FindBy(css = "input[placeholder='Username']")
  private WebElement usernameInput;

  @FindBy(css = "textarea[placeholder='Short bio about you']")
  private WebElement bioTextarea;

  @FindBy(css = "input[type='email']")
  private WebElement emailInput;

  @FindBy(css = "input[type='password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement updateSettingsButton;

  @FindBy(css = ".btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl() + "/user/settings");
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

  public void updatePassword(String newPassword) {
    enterPassword(newPassword);
    clickUpdateSettings();
  }

  public boolean isErrorDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
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

  public boolean isPageLoaded() {
    try {
      return waitForVisibility(pageTitle).getText().contains("Your Settings");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUpdateButtonEnabled() {
    return updateSettingsButton.isEnabled();
  }

  public String getPasswordInputValue() {
    return passwordInput.getAttribute("value");
  }

  public String getPasswordInputType() {
    return passwordInput.getAttribute("type");
  }

  public String getPasswordPlaceholder() {
    return passwordInput.getAttribute("placeholder");
  }

  public void clearPasswordField() {
    passwordInput.clear();
  }

  public String getCurrentUsername() {
    return usernameInput.getAttribute("value");
  }

  public String getCurrentEmail() {
    return emailInput.getAttribute("value");
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
