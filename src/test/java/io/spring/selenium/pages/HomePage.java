package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page Object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = ".nav-link[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".feed-toggle")
  private WebElement feedToggle;

  @FindBy(css = ".tag-list")
  private WebElement tagList;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo(String baseUrl) {
    driver.get(baseUrl);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(settingsLink) || isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isSignInLinkDisplayed() {
    try {
      return isDisplayed(signInLink);
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickFirstArticle() {
    if (!articlePreviews.isEmpty()) {
      WebElement firstArticle = articlePreviews.get(0);
      WebElement articleLink = firstArticle.findElement(By.cssSelector("a.preview-link"));
      click(articleLink);
    }
  }

  public void clickArticleByIndex(int index) {
    if (index < articlePreviews.size()) {
      WebElement article = articlePreviews.get(index);
      WebElement articleLink = article.findElement(By.cssSelector("a.preview-link"));
      click(articleLink);
    }
  }

  public String getFirstArticleTitle() {
    if (!articlePreviews.isEmpty()) {
      WebElement firstArticle = articlePreviews.get(0);
      WebElement titleElement = firstArticle.findElement(By.cssSelector("h1"));
      return getText(titleElement);
    }
    return "";
  }

  public void waitForArticlesToLoad() {
    wait.until(
        ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".article-preview")),
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'No articles')]"))));
  }

  public boolean hasArticles() {
    return !articlePreviews.isEmpty();
  }
}
