package io.spring.selenium.pages;

import java.util.stream.Collectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Article Editor page (create/update). */
public class ArticleEditorPage extends BasePage {

  @FindBy(css = "input.form-control-lg[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input.form-control[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea.form-control[placeholder='Write your article (in markdown)']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagInput;

  @FindBy(css = "button.btn-primary")
  private WebElement submitButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".tag-list .tag-pill")
  private java.util.List<WebElement> tagPills;

  public ArticleEditorPage(WebDriver driver) {
    super(driver);
  }

  public void navigateToNew(String baseUrl) {
    driver.get(baseUrl + "/editor/new");
  }

  public void navigateToEdit(String baseUrl, String slug) {
    driver.get(baseUrl + "/editor/" + slug);
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
    type(tagInput, tag);
    tagInput.sendKeys(org.openqa.selenium.Keys.ENTER);
  }

  public void clickSubmit() {
    click(submitButton);
  }

  public String getTitle() {
    try {
      return waitForVisibility(titleInput).getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getDescription() {
    try {
      return waitForVisibility(descriptionInput).getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public String getBody() {
    try {
      return waitForVisibility(bodyTextarea).getAttribute("value");
    } catch (Exception e) {
      return "";
    }
  }

  public void clearTitle() {
    waitForVisibility(titleInput).clear();
  }

  public void clearDescription() {
    waitForVisibility(descriptionInput).clear();
  }

  public void clearBody() {
    waitForVisibility(bodyTextarea).clear();
  }

  public void updateTitle(String newTitle) {
    clearTitle();
    enterTitle(newTitle);
  }

  public void updateDescription(String newDescription) {
    clearDescription();
    enterDescription(newDescription);
  }

  public void updateBody(String newBody) {
    clearBody();
    enterBody(newBody);
  }

  public void updateArticle(String title, String description, String body) {
    if (title != null && !title.isEmpty()) {
      updateTitle(title);
    }
    if (description != null && !description.isEmpty()) {
      updateDescription(description);
    }
    if (body != null && !body.isEmpty()) {
      updateBody(body);
    }
    clickSubmit();
  }

  public boolean hasErrors() {
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
      return driver.getCurrentUrl().contains("/editor/");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnNewArticlePage() {
    try {
      return driver.getCurrentUrl().contains("/editor/new");
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isOnEditArticlePage() {
    try {
      String url = driver.getCurrentUrl();
      return url.contains("/editor/") && !url.contains("/editor/new");
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForPageToLoad() {
    wait.until(ExpectedConditions.visibilityOf(titleInput));
  }

  public String getSubmitButtonText() {
    try {
      return getText(submitButton);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isSubmitButtonEnabled() {
    try {
      return submitButton.isEnabled();
    } catch (Exception e) {
      return false;
    }
  }

  public int getTagCount() {
    return tagPills.size();
  }

  public java.util.List<String> getTags() {
    return tagPills.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public void removeTag(String tagName) {
    for (WebElement tag : tagPills) {
      if (tag.getText().contains(tagName)) {
        WebElement closeButton =
            tag.findElement(org.openqa.selenium.By.cssSelector("i.ion-close-round"));
        click(closeButton);
        return;
      }
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  public String getPageSource() {
    return driver.getPageSource();
  }
}
