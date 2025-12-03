package io.spring.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = "a[href='/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".navbar-brand")
  private WebElement homeLink;

  @FindBy(css = ".nav-link[href*='@']")
  private WebElement profileLink;

  @FindBy(css = ".article-preview")
  private WebElement articlePreview;

  @FindBy(css = ".article-preview h1")
  private WebElement articleTitle;

  @FindBy(css = ".article-preview a")
  private WebElement articleLink;

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

  public void clickHome() {
    click(homeLink);
  }

  public boolean isSignInLinkDisplayed() {
    return isDisplayed(signInLink);
  }

  public boolean isNewArticleLinkDisplayed() {
    return isDisplayed(newArticleLink);
  }

  public boolean isUserLoggedIn() {
    return isDisplayed(newArticleLink) && !isDisplayed(signInLink);
  }

  public String getFirstArticleTitle() {
    return getText(articleTitle);
  }

  public void clickFirstArticle() {
    click(articleLink);
  }
}
