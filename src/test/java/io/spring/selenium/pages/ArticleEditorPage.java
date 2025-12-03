package io.spring.selenium.pages;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticleEditorPage extends BasePage {

  private static final String NEW_ARTICLE_URL = "/editor/new";
  private static final String EDIT_ARTICLE_URL_PREFIX = "/editor/";

  @FindBy(css = "input[placeholder='Article Title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder=\"What's this article about?\"]")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder='Write your article (in markdown)']")
  private WebElement bodyInput;

  @FindBy(css = "input[placeholder='Enter tags']")
  private WebElement tagInput;

  @FindBy(css = "button.btn-primary")
  private WebElement publishButton;

  @FindBy(css = ".error-messages")
  private WebElement errorMessages;

  @FindBy(css = ".tag-list .tag-pill")
  private WebElement tagList;

  public ArticleEditorPage(WebDriver driver) {
    super(driver);
  }

  public ArticleEditorPage navigateToNewArticle(String baseUrl) {
    driver.get(baseUrl + NEW_ARTICLE_URL);
    return this;
  }

  public ArticleEditorPage navigateToEditArticle(String baseUrl, String slug) {
    driver.get(baseUrl + EDIT_ARTICLE_URL_PREFIX + slug);
    return this;
  }

  public boolean isOnEditorPage() {
    try {
      return waitForVisibility(titleInput).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void enterTitle(String title) {
    type(titleInput, title);
  }

  public void enterDescription(String description) {
    type(descriptionInput, description);
  }

  public void enterBody(String body) {
    type(bodyInput, body);
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

  public void updateArticle(String title, String description, String body) {
    if (title != null && !title.isEmpty()) {
      clearAndType(titleInput, title);
    }
    if (description != null && !description.isEmpty()) {
      clearAndType(descriptionInput, description);
    }
    if (body != null && !body.isEmpty()) {
      clearAndType(bodyInput, body);
    }
    clickPublish();
  }

  private void clearAndType(WebElement element, String text) {
    WebElement visibleElement = waitForVisibility(element);
    visibleElement.clear();
    visibleElement.sendKeys(text);
  }

  public boolean hasErrors() {
    try {
      return isDisplayed(errorMessages);
    } catch (Exception e) {
      return false;
    }
  }

  public String getTitle() {
    return waitForVisibility(titleInput).getAttribute("value");
  }

  public String getDescription() {
    return waitForVisibility(descriptionInput).getAttribute("value");
  }

  public String getBody() {
    return waitForVisibility(bodyInput).getAttribute("value");
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(titleInput));
  }

  public boolean isTimestampFieldPresent() {
    try {
      String pageSource = driver.getPageSource();
      return pageSource.contains("createdAt") || pageSource.contains("updatedAt");
    } catch (Exception e) {
      return false;
    }
  }
}
