package io.spring.selenium.pages;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class EditorPage extends BasePage {

  private static final String EDITOR_URL = "http://localhost:3000/editor/new";

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyInput;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagsInput;

  @FindBy(css = "button[type='submit']")
  private WebElement publishButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> addedTags;

  public EditorPage(WebDriver driver) {
    super(driver);
  }

  public EditorPage navigate() {
    driver.get(EDITOR_URL);
    waitForPageLoad();
    return this;
  }

  public void waitForPageLoad() {
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[placeholder='Article Title']")));
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
    type(bodyInput, body);
    return this;
  }

  public EditorPage enterTag(String tag) {
    type(tagsInput, tag);
    tagsInput.sendKeys(Keys.ENTER);
    return this;
  }

  public EditorPage enterTags(List<String> tags) {
    for (String tag : tags) {
      enterTag(tag);
    }
    return this;
  }

  public ArticlePage clickPublish() {
    click(publishButton);
    try {
      wait.until(ExpectedConditions.urlContains("/article/"));
    } catch (Exception e) {
      // Publish might have failed
    }
    return new ArticlePage(driver);
  }

  public EditorPage clickPublishExpectingError() {
    click(publishButton);
    return this;
  }

  public ArticlePage createArticle(
      String title, String description, String body, List<String> tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    if (tags != null && !tags.isEmpty()) {
      enterTags(tags);
    }
    return clickPublish();
  }

  public boolean hasErrorMessages() {
    try {
      return errorMessages.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessages() {
    try {
      return getText(errorMessages);
    } catch (Exception e) {
      return "";
    }
  }

  public List<String> getAddedTags() {
    List<String> tags = new ArrayList<>();
    try {
      for (WebElement tag : addedTags) {
        tags.add(tag.getText().trim());
      }
    } catch (Exception e) {
      // No tags added
    }
    return tags;
  }

  public boolean isOnEditorPage() {
    return driver.getCurrentUrl().contains("/editor");
  }
}
