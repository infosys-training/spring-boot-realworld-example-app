package io.spring.application.export;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArticleCsvExportServiceTest {
  private ArticleCsvExportService csvExportService;

  @BeforeEach
  public void setUp() {
    csvExportService = new ArticleCsvExportService();
  }

  @Test
  public void should_generate_csv_with_headers() throws Exception {
    List<ArticleData> articles = Arrays.asList();
    byte[] csvBytes = csvExportService.generateCsv(articles);
    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    assertTrue(csv.contains("Title"));
    assertTrue(csv.contains("Slug"));
    assertTrue(csv.contains("Description"));
    assertTrue(csv.contains("Author"));
    assertTrue(csv.contains("Tags"));
  }

  @Test
  public void should_generate_csv_with_article_data() throws Exception {
    ProfileData profile = new ProfileData("user1", "johndoe", "Bio", "image.jpg", false);
    ArticleData article =
        new ArticleData(
            "id1",
            "test-article",
            "Test Article",
            "A test description",
            "Article body content",
            false,
            5,
            new DateTime(2024, 1, 1, 12, 0),
            new DateTime(2024, 1, 2, 12, 0),
            Arrays.asList("java", "spring"),
            profile);

    List<ArticleData> articles = Arrays.asList(article);
    byte[] csvBytes = csvExportService.generateCsv(articles);
    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    assertTrue(csv.contains("Test Article"));
    assertTrue(csv.contains("test-article"));
    assertTrue(csv.contains("A test description"));
    assertTrue(csv.contains("johndoe"));
    assertTrue(csv.contains("java; spring"));
    assertTrue(csv.contains("5"));
  }

  @Test
  public void should_handle_empty_tags() throws Exception {
    ProfileData profile = new ProfileData("user1", "johndoe", "Bio", "image.jpg", false);
    ArticleData article =
        new ArticleData(
            "id1",
            "test-article",
            "Test Article",
            "Description",
            "Body",
            false,
            0,
            new DateTime(),
            new DateTime(),
            Arrays.asList(),
            profile);

    List<ArticleData> articles = Arrays.asList(article);
    byte[] csvBytes = csvExportService.generateCsv(articles);
    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    assertTrue(csv.contains("Test Article"));
    assertFalse(csv.contains("; "));
  }

  @Test
  public void should_handle_multiple_articles() throws Exception {
    ProfileData profile1 = new ProfileData("user1", "john", "Bio1", "img1.jpg", false);
    ProfileData profile2 = new ProfileData("user2", "jane", "Bio2", "img2.jpg", false);

    ArticleData article1 =
        new ArticleData(
            "id1",
            "article-1",
            "Article 1",
            "Desc 1",
            "Body 1",
            false,
            1,
            new DateTime(),
            new DateTime(),
            Arrays.asList("tag1"),
            profile1);

    ArticleData article2 =
        new ArticleData(
            "id2",
            "article-2",
            "Article 2",
            "Desc 2",
            "Body 2",
            true,
            10,
            new DateTime(),
            new DateTime(),
            Arrays.asList("tag2", "tag3"),
            profile2);

    List<ArticleData> articles = Arrays.asList(article1, article2);
    byte[] csvBytes = csvExportService.generateCsv(articles);
    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    assertTrue(csv.contains("Article 1"));
    assertTrue(csv.contains("Article 2"));
    assertTrue(csv.contains("john"));
    assertTrue(csv.contains("jane"));
    assertTrue(csv.contains("tag2; tag3"));
  }
}
