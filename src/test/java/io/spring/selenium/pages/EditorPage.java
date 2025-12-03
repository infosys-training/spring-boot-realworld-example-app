package io.spring.selenium.pages;

import org.openqa.selenium.Keys;
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
  private WebElement tagInput;

  @FindBy(css = "button.btn-primary")
  private WebElement publishButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".tag-list .tag-pill")
  private java.util.List<WebElement> addedTags;

  public EditorPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToNewArticle() {
    driver.get(getBaseUrl() + "/editor/new");
  }

  public void navigateToEditArticle(String slug) {
    driver.get(getBaseUrl() + "/editor/" + slug);
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

  public void addTag(String tag) {
    type(tagInput, tag);
    tagInput.sendKeys(Keys.ENTER);
  }

  public void clickPublish() {
    click(publishButton);
  }

  public void createArticle(String title, String description, String body) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    clickPublish();
  }

  public void createArticleWithTags(String title, String description, String body, String... tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    for (String tag : tags) {
      addTag(tag);
    }
    clickPublish();
  }

  public boolean isOnEditorPage() {
    try {
      return isDisplayed(titleInput) && isDisplayed(publishButton);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasErrorMessages() {
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

  public String getTitle() {
    return titleInput.getAttribute("value");
  }

  public String getDescription() {
    return descriptionInput.getAttribute("value");
  }

  public String getBody() {
    return bodyTextarea.getAttribute("value");
  }

  public int getTagCount() {
    return addedTags.size();
  }

  public void clearTitle() {
    titleInput.clear();
  }

  public void clearDescription() {
    descriptionInput.clear();
  }

  public void clearBody() {
    bodyTextarea.clear();
  }

  public void waitForEditorLoad() {
    wait.until(ExpectedConditions.visibilityOf(titleInput));
  }

  public void waitForRedirectAfterPublish() {
    wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/editor/")));
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
