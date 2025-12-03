package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Page object for the Home page. */
public class HomePage extends BasePage {

  @FindBy(css = ".navbar-brand")
  private WebElement navbarBrand;

  @FindBy(css = "a[href='/user/login']")
  private WebElement signInLink;

  @FindBy(css = "a[href='/user/register']")
  private WebElement signUpLink;

  @FindBy(css = "a[href='/user/settings']")
  private WebElement settingsLink;

  @FindBy(css = ".nav-link img")
  private WebElement navbarUserImage;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".article-preview .author img")
  private List<WebElement> articleAuthorImages;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

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

  public void clickSettings() {
    click(settingsLink);
  }

  public boolean isNavbarUserImageDisplayed() {
    try {
      return waitForVisibility(navbarUserImage).isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getNavbarUserImageSrc() {
    try {
      return waitForVisibility(navbarUserImage).getAttribute("src");
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isSignInLinkDisplayed() {
    return isDisplayed(signInLink);
  }

  public boolean isSignUpLinkDisplayed() {
    return isDisplayed(signUpLink);
  }

  public boolean isSettingsLinkDisplayed() {
    return isDisplayed(settingsLink);
  }

  public int getArticlePreviewCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticleAuthorImages() {
    return articleAuthorImages;
  }

  public String getArticleAuthorImageSrc(int index) {
    if (index < articleAuthorImages.size()) {
      return articleAuthorImages.get(index).getAttribute("src");
    }
    return null;
  }

  public void clickYourFeedTab() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Your Feed")) {
        click(tab);
        return;
      }
    }
  }

  public void clickGlobalFeedTab() {
    for (WebElement tab : feedTabs) {
      if (tab.getText().contains("Global Feed")) {
        click(tab);
        return;
      }
    }
  }

  public String getPageTitle() {
    return driver.getTitle();
  }
}
