package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = "a[href='/editor/new']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".navbar-brand")
  private WebElement brandLogo;

  @FindBy(css = ".nav-item .nav-link[href*='/profile']")
  private WebElement profileLink;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(System.getProperty("base.url", "http://localhost:3000"));
  }

  public void clickNewArticle() {
    click(newArticleLink);
  }

  public void clickSettings() {
    click(settingsLink);
  }

  public void clickSignIn() {
    click(signInLink);
  }

  public void clickSignUp() {
    click(signUpLink);
  }

  public boolean isUserLoggedIn() {
    try {
      return isDisplayed(newArticleLink) && isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isUserLoggedOut() {
    try {
      return isDisplayed(signInLink) && isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getPopularTags() {
    return popularTags;
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public String getCurrentUserName() {
    try {
      return getText(profileLink);
    } catch (Exception e) {
      return "";
    }
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".navbar-brand")));
  }

  public boolean isNewArticleLinkVisible() {
    try {
      return isDisplayed(newArticleLink);
    } catch (Exception e) {
      return false;
    }
  }
}
