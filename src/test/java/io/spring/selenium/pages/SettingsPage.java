package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SettingsPage extends BasePage {

  private static final String SETTINGS_URL = "/user/settings";

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

  @FindBy(css = "button.btn-primary")
  private WebElement updateSettingsButton;

  @FindBy(css = "ul.error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = "ul.error-messages")
  private WebElement errorMessagesContainer;

  @FindBy(css = ".settings-page")
  private WebElement settingsContainer;

  public SettingsPage(WebDriver driver) {
    super(driver);
  }

  public SettingsPage navigateTo(String baseUrl) {
    driver.get(baseUrl + SETTINGS_URL);
    return this;
  }

  public SettingsPage enterImageUrl(String imageUrl) {
    type(imageUrlInput, imageUrl);
    return this;
  }

  public SettingsPage enterUsername(String username) {
    type(usernameInput, username);
    return this;
  }

  public SettingsPage enterBio(String bio) {
    type(bioTextarea, bio);
    return this;
  }

  public SettingsPage enterEmail(String email) {
    type(emailInput, email);
    return this;
  }

  public SettingsPage enterPassword(String password) {
    type(passwordInput, password);
    return this;
  }

  public SettingsPage clickUpdateSettings() {
    click(updateSettingsButton);
    return this;
  }

  public SettingsPage clearImageUrl() {
    waitForVisibility(imageUrlInput).clear();
    return this;
  }

  public SettingsPage clearUsername() {
    waitForVisibility(usernameInput).clear();
    return this;
  }

  public SettingsPage clearBio() {
    waitForVisibility(bioTextarea).clear();
    return this;
  }

  public SettingsPage clearEmail() {
    waitForVisibility(emailInput).clear();
    return this;
  }

  public SettingsPage clearPassword() {
    waitForVisibility(passwordInput).clear();
    return this;
  }

  public boolean isErrorDisplayed() {
    try {
      return waitForVisibility(errorMessagesContainer).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getErrorMessages() {
    waitForVisibility(errorMessagesContainer);
    return errorMessages.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public int getErrorCount() {
    try {
      waitForVisibility(errorMessagesContainer);
      return errorMessages.size();
    } catch (Exception e) {
      return 0;
    }
  }

  public boolean hasErrorContaining(String text) {
    return getErrorMessages().stream()
        .anyMatch(msg -> msg.toLowerCase().contains(text.toLowerCase()));
  }

  public boolean isOnSettingsPage() {
    try {
      return settingsContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}
