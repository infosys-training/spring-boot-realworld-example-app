package io.spring.selenium.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(css = ".banner h1")
  private WebElement bannerTitle;

  @FindBy(css = ".feed-toggle .nav-link")
  private List<WebElement> feedTabs;

  @FindBy(css = ".nav-link[href='/']")
  private WebElement globalFeedTab;

  @FindBy(css = ".article-preview")
  private List<WebElement> articlePreviews;

  @FindBy(css = ".tag-list .tag-pill")
  private List<WebElement> popularTags;

  @FindBy(css = ".sidebar")
  private WebElement sidebar;

  public HomePage(WebDriver driver) {
    super(driver);
  }

  public void navigateTo() {
    driver.get(getBaseUrl());
  }

  public String getBannerTitle() {
    try {
      return getText(bannerTitle);
    } catch (Exception e) {
      return "";
    }
  }

  public boolean isBannerDisplayed() {
    return isDisplayed(bannerTitle);
  }

  public void clickGlobalFeed() {
    click(globalFeedTab);
  }

  public int getArticleCount() {
    return articlePreviews.size();
  }

  public List<WebElement> getArticlePreviews() {
    return articlePreviews;
  }

  public int getPopularTagsCount() {
    return popularTags.size();
  }

  public List<WebElement> getPopularTags() {
    return popularTags;
  }

  public void clickTag(int index) {
    if (index < popularTags.size()) {
      click(popularTags.get(index));
    }
  }

  public boolean isSidebarDisplayed() {
    return isDisplayed(sidebar);
  }

  public boolean isPageLoaded() {
    try {
      return isBannerDisplayed() || getArticleCount() > 0;
    } catch (Exception e) {
      return false;
    }
  }

  public int getFeedTabCount() {
    return feedTabs.size();
  }

  private String getBaseUrl() {
    return System.getProperty("base.url", "http://localhost:3000");
  }
}
