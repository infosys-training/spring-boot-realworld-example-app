package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Article Editor page. */
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

  @FindBy(css = ".error-messages li")
  private WebElement errorMessage;

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

  public boolean isTitleInputDisplayed() {
    return isDisplayed(titleInput);
  }

  public boolean isDescriptionInputDisplayed() {
    return isDisplayed(descriptionInput);
  }

  public boolean isBodyTextareaDisplayed() {
    return isDisplayed(bodyTextarea);
  }

  public boolean isTagsInputDisplayed() {
    return isDisplayed(tagsInput);
  }

  public boolean isPublishButtonDisplayed() {
    return isDisplayed(publishButton);
  }
}
