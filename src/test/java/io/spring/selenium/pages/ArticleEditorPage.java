package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ArticleEditorPage extends BasePage {

  private static final String EDITOR_URL = "/editor/new";

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagsInput;

  @FindBy(css = "button.btn-primary")
  private WebElement publishButton;

  @FindBy(css = "ul.error-messages li")
  private List<WebElement> errorMessages;

  @FindBy(css = "ul.error-messages")
  private WebElement errorMessagesContainer;

  @FindBy(css = ".editor-page")
  private WebElement editorContainer;

  public ArticleEditorPage(WebDriver driver) {
    super(driver);
  }

  public ArticleEditorPage navigateTo(String baseUrl) {
    driver.get(baseUrl + EDITOR_URL);
    waitForVisibility(editorContainer);
    return this;
  }

  public ArticleEditorPage enterTitle(String title) {
    type(titleInput, title);
    return this;
  }

  public ArticleEditorPage enterDescription(String description) {
    type(descriptionInput, description);
    return this;
  }

  public ArticleEditorPage enterBody(String body) {
    type(bodyTextarea, body);
    return this;
  }

  public ArticleEditorPage enterTags(String tags) {
    type(tagsInput, tags);
    return this;
  }

  public ArticleEditorPage clickPublish() {
    click(publishButton);
    return this;
  }

  public ArticleEditorPage clearTitle() {
    waitForVisibility(titleInput).clear();
    return this;
  }

  public ArticleEditorPage clearDescription() {
    waitForVisibility(descriptionInput).clear();
    return this;
  }

  public ArticleEditorPage clearBody() {
    waitForVisibility(bodyTextarea).clear();
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

  public boolean isOnEditorPage() {
    try {
      return editorContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}
