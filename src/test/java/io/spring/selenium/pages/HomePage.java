package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Page object for the home page. */
public class HomePage extends BasePage {

  @FindBy(css = "a.navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".navbar .nav-item")
  private List<WebElement> navItems;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    return this;
  }

  public boolean isLoggedIn() {
    try {
      return !isDisplayed(signInLink);
    } catch (Exception e) {
      return true;
    }
  }

  public boolean isLoggedOut() {
    try {
      return isDisplayed(signInLink) && isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
  }

  public LoginPage clickSignIn() {
    click(signInLink);
    return new LoginPage(driver);
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

  public ProfilePage navigateToProfile(String username) {
    WebElement profileLink =
        driver.findElement(By.cssSelector("a[href='/profile/" + username + "']"));
    click(profileLink);
    return new ProfilePage(driver);
  }

  public void clickYourFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
        click(tab);
        break;
      }
    }
  }

  public void clickGlobalFeed() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Global Feed")) {
        click(tab);
        break;
      }
    }
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public boolean isArticleByAuthorDisplayed(String authorUsername) {
    for (WebElement preview : articlePreviews) {
      try {
        WebElement authorLink = preview.findElement(By.cssSelector(".author"));
        if (authorLink.getText().equals(authorUsername)) {
          return true;
        }
      } catch (Exception e) {
        continue;
      }
    }
    return false;
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        break;
      }
    }
  }

  public String getLoggedInUsername() {
    for (WebElement navItem : navItems) {
      try {
        WebElement profileLink = navItem.findElement(By.cssSelector("a[href^='/profile/']"));
        String href = profileLink.getAttribute("href");
        return href.substring(href.lastIndexOf("/") + 1);
      } catch (Exception e) {
        continue;
      }
    }
    return null;
  }

  public void waitForPageLoad() {
    wait.until(ExpectedConditions.visibilityOf(navbarBrand));
  }

  public boolean isHomePageDisplayed() {
    try {
      return isDisplayed(navbarBrand);
    } catch (Exception e) {
      return false;
    }
  }
}
