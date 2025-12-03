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

  private static final String NEW_ARTICLE_URL = "/editor";
  private static final String EDIT_ARTICLE_URL = "/editor/";

  public EditorPage(WebDriver driver) {
    super(driver);
  }

  public EditorPage navigateToNewArticle(String baseUrl) {
    driver.get(baseUrl + NEW_ARTICLE_URL);
    return this;
  }

  public EditorPage navigateToEditArticle(String baseUrl, String slug) {
    driver.get(baseUrl + EDIT_ARTICLE_URL + slug);
    return this;
  }

  public EditorPage enterTitle(String title) {
    type(titleInput, title);
    return this;
  }

  public EditorPage enterDescription(String description) {
    type(descriptionInput, description);
    return this;
  }

  public EditorPage enterBody(String body) {
    type(bodyTextarea, body);
    return this;
  }

  public EditorPage enterTags(String tags) {
    type(tagsInput, tags);
    return this;
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

  public void updateArticle(String title, String description, String body) {
    if (title != null) {
      enterTitle(title);
    }
    if (description != null) {
      enterDescription(description);
    }
    if (body != null) {
      enterBody(body);
    }
    clickPublish();
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

  public boolean isEditorLoaded() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(titleInput)) != null;
    } catch (Exception e) {
      return false;
    }
  }

  public String getCurrentTitle() {
    try {
      return titleInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentDescription() {
    try {
      return descriptionInput.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getCurrentBody() {
    try {
      return bodyTextarea.getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }
}
