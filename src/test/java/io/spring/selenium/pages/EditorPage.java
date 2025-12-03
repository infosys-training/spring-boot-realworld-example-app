package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article Editor page. */
public class EditorPage extends BasePage {

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagsInput;

  @FindBy(css = "button[type='submit']")
  private WebElement publishButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  public EditorPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/editor");
  }

  public void enterTitle(String title) {
    type(titleInput, title);
  }

  public void enterDescription(String description) {
    type(descriptionInput, description);
  }

  public void enterBody(String body) {
    type(bodyTextarea, body);
  }

  public void enterTags(String tags) {
    type(tagsInput, tags);
  }

  public void clickPublish() {
    click(publishButton);
  }

  public void createArticle(String title, String description, String body, String tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    if (tags != null && !tags.isEmpty()) {
      enterTags(tags);
    }
    clickPublish();
  }

  public void waitForRedirectToArticle() {
    wait.until(ExpectedConditions.urlContains("/article/"));
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

  public boolean isOnEditorPage() {
    try {
      return driver.getCurrentUrl().contains("/editor");
    } catch (Exception e) {
      return false;
    }
  }
}
