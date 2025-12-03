package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ArticleEditorPage extends BasePage {

  @FindBy(css = "input[placeholder*='Article Title'], input[name='title']")
  private WebElement titleInput;

  @FindBy(css = "input[placeholder*='about'], input[name='description']")
  private WebElement descriptionInput;

  @FindBy(css = "textarea[placeholder*='article'], textarea[name='body']")
  private WebElement bodyTextarea;

  @FindBy(css = "input[placeholder*='tags'], input[name='tags']")
  private WebElement tagsInput;

  @FindBy(css = "button[type='submit']")
  private WebElement publishButton;

  @FindBy(css = ".error-messages li, .error-message")
  private WebElement errorMessage;

  @FindBy(css = "h1")
  private WebElement pageTitle;

  public ArticleEditorPage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl + "/editor");
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

  public void enterTags(String tags) {
    type(tagsInput, tags);
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

  public void createArticleWithTags(String title, String description, String body, String tags) {
    enterTitle(title);
    enterDescription(description);
    enterBody(body);
    enterTags(tags);
    clickPublish();
  }

  public void updateTitle(String newTitle) {
    type(titleInput, newTitle);
    clickPublish();
  }

  public void clearTitle() {
    waitForVisibility(titleInput).clear();
  }

  public boolean isErrorDisplayed() {
    try {
      return isDisplayed(errorMessage);
    } catch (Exception e) {
      return false;
    }
  }

  public String getErrorMessage() {
    return getText(errorMessage);
  }

  public String getCurrentTitle() {
    return waitForVisibility(titleInput).getAttribute("value");
  }

  public boolean isTitleInputDisplayed() {
    return isDisplayed(titleInput);
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(titleInput));
  }
}
