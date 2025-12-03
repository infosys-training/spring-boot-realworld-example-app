package io.spring.selenium.pages;

import java.util.List;
import java.util.stream.Collectors;
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

  @FindBy(css = "a[href='/editor']")
  private WebElement newArticleLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link.active")
  private WebElement activeNavLink;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public HomePage navigateTo(String baseUrl) {
    driver.get(baseUrl);
    return this;
  }

  public boolean isLoggedIn() {
    try {
      return isDisplayed(newArticleLink) || isDisplayed(settingsLink);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLoggedOut() {
    try {
      return isDisplayed(signInLink) && isDisplayed(signUpLink);
    } catch (Exception e) {
      return false;
    }
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

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public void clickFirstArticle() {
    if (!articlePreviews.isEmpty()) {
      click(articlePreviews.get(0));
    }
  }

  public List<String> getPopularTags() {
    return popularTags.stream().map(WebElement::getText).collect(Collectors.toList());
  }

  public void clickTag(String tagName) {
    for (WebElement tag : popularTags) {
      if (tag.getText().equals(tagName)) {
        click(tag);
        return;
      }
    }
  }

  public boolean isHomePageLoaded() {
    try {
      return wait.until(ExpectedConditions.visibilityOf(navbarBrand)) != null;
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForArticlesToLoad() {
    try {
      wait.until(ExpectedConditions.visibilityOfAllElements(articlePreviews));
    } catch (Exception e) {
      // Articles may not be present
    }
  }

  public String getLoggedInUsername() {
    try {
      WebElement profileLink =
          driver.findElements(org.openqa.selenium.By.cssSelector(".nav-link")).stream()
              .filter(
                  el ->
                      el.getAttribute("href") != null
                          && el.getAttribute("href").contains("/profile/"))
              .findFirst()
              .orElse(null);
      return profileLink != null ? profileLink.getText().trim() : "";
    } catch (Exception e) {
      return "";
    }
  }
}
