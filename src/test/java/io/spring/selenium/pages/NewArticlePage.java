package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the New Article (Editor) page. */
public class NewArticlePage extends BasePage {

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagsInput;

  @FindBy(css = "button[type='button']")
  private WebElement publishButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> addedTags;

  public NewArticlePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(System.getProperty("base.url", "http://localhost:3000") + "/editor/new");
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

  public void enterTag(String tag) {
    waitForVisibility(tagsInput);
    tagsInput.sendKeys(tag);
    tagsInput.sendKeys(Keys.ENTER);
  }

  public void enterTags(List<String> tags) {
    for (String tag : tags) {
      enterTag(tag);
    }
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

  public void createArticleWithTags(
      String title, String description, String body, List<String> tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    enterTags(tags);
    clickPublish();
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

  public boolean isEditorPageDisplayed() {
    try {
      return isDisplayed(titleInput) && isDisplayed(bodyTextarea);
    } catch (Exception e) {
      return false;
    }
  }

  public List<String> getAddedTags() {
    List<String> tagNames = new ArrayList<>();
    for (WebElement tag : addedTags) {
      tagNames.add(tag.getText().trim());
    }
    return tagNames;
  }

  public void clearTitle() {
    waitForVisibility(titleInput);
    titleInput.clear();
  }

  public void clearDescription() {
    waitForVisibility(descriptionInput);
    descriptionInput.clear();
  }

  public void clearBody() {
    waitForVisibility(bodyTextarea);
    bodyTextarea.clear();
  }

  public boolean isTitleFieldEmpty() {
    return titleInput.getAttribute("value").isEmpty();
  }

  public boolean isDescriptionFieldEmpty() {
    return descriptionInput.getAttribute("value").isEmpty();
  }

  public boolean isBodyFieldEmpty() {
    return bodyTextarea.getAttribute("value").isEmpty();
  }

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[placeholder='Article Title']")));
  }

  public boolean isPublishButtonEnabled() {
    try {
      return publishButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public String getTitleValue() {
    return titleInput.getAttribute("value");
  }

  public String getDescriptionValue() {
    return descriptionInput.getAttribute("value");
  }

  public String getBodyValue() {
    return bodyTextarea.getAttribute("value");
  }

  public void removeTag(String tagName) {
    for (WebElement tag : addedTags) {
      if (tag.getText().trim().contains(tagName)) {
        WebElement closeButton = tag.findElement(By.cssSelector("i.ion-close-round"));
        click(closeButton);
        return;
      }
    }
  }
}
