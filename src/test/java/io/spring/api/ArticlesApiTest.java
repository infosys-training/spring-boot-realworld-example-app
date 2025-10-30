package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ProfileData;
import io.spring.application.export.ArticleCsvExportService;
import io.spring.core.article.Article;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ArticlesApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ArticlesApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ArticleQueryService articleQueryService;

  @MockBean private ArticleCommandService articleCommandService;

  @MockBean private ArticleCsvExportService articleCsvExportService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_create_article_success() throws Exception {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    List<String> tagList = asList("reactjs", "angularjs", "dragons");
    Map<String, Object> param = prepareParam(title, description, body, tagList);

    ArticleData articleData =
        new ArticleData(
            "123",
            slug,
            title,
            description,
            body,
            false,
            0,
            new DateTime(),
            new DateTime(),
            tagList,
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(articleCommandService.createArticle(any(), any()))
        .thenReturn(new Article(title, description, body, tagList, user.getId()));

    when(articleQueryService.findBySlug(eq(Article.toSlug(title)), any()))
        .thenReturn(Optional.empty());

    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .then()
        .statusCode(200)
        .body("article.title", equalTo(title))
        .body("article.favorited", equalTo(false))
        .body("article.body", equalTo(body))
        .body("article.favoritesCount", equalTo(0))
        .body("article.author.username", equalTo(user.getUsername()))
        .body("article.author.id", equalTo(null));

    verify(articleCommandService).createArticle(any(), any());
  }

  @Test
  public void should_get_error_message_with_wrong_parameter() throws Exception {
    String title = "How to train your dragon";
    String description = "Ever wonder how?";
    String body = "";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, body, asList(tagList));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .prettyPeek()
        .then()
        .statusCode(422)
        .body("errors.body[0]", equalTo("can't be empty"));
  }

  @Test
  public void should_get_error_message_with_duplicated_title() {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String body = "You have to believe";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, body, asList(tagList));

    ArticleData articleData =
        new ArticleData(
            "123",
            slug,
            title,
            description,
            body,
            false,
            0,
            new DateTime(),
            new DateTime(),
            asList(tagList),
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(articleQueryService.findBySlug(eq(Article.toSlug(title)), any()))
        .thenReturn(Optional.of(articleData));

    when(articleQueryService.findById(any(), any())).thenReturn(Optional.of(articleData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/articles")
        .prettyPeek()
        .then()
        .statusCode(422);
  }

  private HashMap<String, Object> prepareParam(
      final String title, final String description, final String body, final List<String> tagList) {
    return new HashMap<String, Object>() {
      {
        put(
            "article",
            new HashMap<String, Object>() {
              {
                put("title", title);
                put("description", description);
                put("body", body);
                put("tagList", tagList);
              }
            });
      }
    };
  }

  @Test
  public void should_export_articles_to_csv_with_authentication() throws Exception {
    String tag = "java";
    ProfileData profile = new ProfileData("userid", "johndoe", "bio", "image.jpg", false);
    ArticleData article =
        new ArticleData(
            "id1",
            "test-slug",
            "Test Title",
            "Test Description",
            "Test Body",
            false,
            5,
            new DateTime(),
            new DateTime(),
            asList("java", "spring"),
            profile);

    ArticleDataList articleDataList = new ArticleDataList(asList(article), 1);

    when(articleQueryService.findRecentArticles(eq(tag), eq(null), eq(null), any(), any()))
        .thenReturn(articleDataList);

    String csvContent =
        "Title,Slug,Description,Body,Author,Tags,Created At,Updated At,Favorites Count\n"
            + "Test Title,test-slug,Test Description,Test Body,johndoe,java; spring,2024-01-01T12:00:00.000Z,2024-01-01T12:00:00.000Z,5";
    when(articleCsvExportService.generateCsv(any())).thenReturn(csvContent.getBytes());

    given()
        .header("Authorization", "Token " + token)
        .param("tag", tag)
        .when()
        .get("/articles/export")
        .then()
        .statusCode(200)
        .header("Content-Type", containsString("text/csv"))
        .header("Content-Disposition", containsString("attachment"))
        .header("Content-Disposition", containsString("articles.csv"))
        .body(containsString("Test Title"))
        .body(containsString("johndoe"));

    verify(articleQueryService).findRecentArticles(eq(tag), eq(null), eq(null), any(), any());
    verify(articleCsvExportService).generateCsv(any());
  }

  @Test
  public void should_export_articles_without_authentication() throws Exception {
    ArticleDataList articleDataList = new ArticleDataList(asList(), 0);
    when(articleQueryService.findRecentArticles(eq(null), eq(null), eq(null), any(), eq(null)))
        .thenReturn(articleDataList);

    String csvContent =
        "Title,Slug,Description,Body,Author,Tags,Created At,Updated At,Favorites Count\n";
    when(articleCsvExportService.generateCsv(any())).thenReturn(csvContent.getBytes());

    given().when().get("/articles/export").then().statusCode(200);
  }

  @Test
  public void should_export_with_filters() throws Exception {
    String author = "johndoe";
    String favoritedBy = "janedoe";
    ProfileData profile = new ProfileData("userid", "johndoe", "bio", "image.jpg", false);
    ArticleData article =
        new ArticleData(
            "id1",
            "test-slug",
            "Test Title",
            "Test Description",
            "Test Body",
            true,
            10,
            new DateTime(),
            new DateTime(),
            asList("javascript"),
            profile);

    ArticleDataList articleDataList = new ArticleDataList(asList(article), 1);

    when(articleQueryService.findRecentArticles(
            eq(null), eq(author), eq(favoritedBy), any(), any()))
        .thenReturn(articleDataList);

    String csvContent = "Title,Slug\nTest Title,test-slug";
    when(articleCsvExportService.generateCsv(any())).thenReturn(csvContent.getBytes());

    given()
        .header("Authorization", "Token " + token)
        .param("author", author)
        .param("favorited", favoritedBy)
        .when()
        .get("/articles/export")
        .then()
        .statusCode(200);

    verify(articleQueryService)
        .findRecentArticles(eq(null), eq(author), eq(favoritedBy), any(), any());
  }
}
