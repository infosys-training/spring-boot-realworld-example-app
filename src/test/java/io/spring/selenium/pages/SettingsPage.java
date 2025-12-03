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

  @FindBy(css = "input[type='email']")
  private WebElement emailInput;

  @FindBy(css = "input[type='password']")
  private WebElement passwordInput;

  @FindBy(css = "button[type='submit']")
  private WebElement updateSettingsButton;

  @FindBy(css = ".btn-outline-danger")
  private WebElement logoutButton;

  @FindBy(css = "h1.text-xs-center")
  private WebElement pageTitle;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/user/settings");
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

  public boolean isPageLoaded() {
    try {
      return waitForVisibility(pageTitle).getText().contains("Your Settings");
    } catch (Exception e) {
      return false;
    }
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

  public String getUsernameValue() {
    return usernameInput.getAttribute("value");
  }

  public String getEmailValue() {
    return emailInput.getAttribute("value");
  }

  public String getBioValue() {
    return bioTextarea.getAttribute("value");
  }

  public String getImageUrlValue() {
    return imageUrlInput.getAttribute("value");
  }

  public void updatePassword(String newPassword) {
    enterPassword(newPassword);
    clickUpdateSettings();
  }
}
