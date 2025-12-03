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
  private WebElement updateButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".error-messages li")
  private WebElement errorMessageItem;

  @FindBy(css = "button.btn-outline-danger")
  private WebElement logoutButton;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl() + "/settings");
  }

  public void enterImageUrl(String url) {
    type(imageUrlInput, url);
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

  public boolean isErrorMessageDisplayed() {
    try {
      return waitForVisibility(errorMessages).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    try {
      return getText(errorMessageItem);
    } catch (Exception e) {
      return "";
    }
  }

  public String getImageUrlValue() {
    return imageUrlInput.getAttribute("value");
  }

  public String getUsernameValue() {
    return usernameInput.getAttribute("value");
  }

  public String getBioValue() {
    return bioTextarea.getAttribute("value");
  }

  public String getEmailValue() {
    return emailInput.getAttribute("value");
  }

  public void clearImageUrl() {
    imageUrlInput.clear();
  }

  public void clearUsername() {
    usernameInput.clear();
  }

  public void clearBio() {
    bioTextarea.clear();
  }

  public void clearEmail() {
    emailInput.clear();
  }

  public void clearPassword() {
    passwordInput.clear();
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

  public boolean isUpdateButtonDisplayed() {
    return isDisplayed(updateButton);
  }

  public boolean isLogoutButtonDisplayed() {
    return isDisplayed(logoutButton);
  }

  public boolean isPageLoaded() {
    try {
      return isUsernameInputDisplayed() && isEmailInputDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
