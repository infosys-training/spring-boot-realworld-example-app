package io.spring.api;

import io.spring.application.ArticleQueryService;
import io.spring.application.Page;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.data.ArticleDataList;
import io.spring.application.export.ArticleCsvExportService;
import io.spring.core.article.Article;
import io.spring.core.user.User;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/articles")
@AllArgsConstructor
public class ArticlesApi {
  private ArticleCommandService articleCommandService;
  private ArticleQueryService articleQueryService;
  private ArticleCsvExportService articleCsvExportService;

  @PostMapping
  public ResponseEntity createArticle(
      @Valid @RequestBody NewArticleParam newArticleParam, @AuthenticationPrincipal User user) {
    Article article = articleCommandService.createArticle(newArticleParam, user);
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("article", articleQueryService.findById(article.getId(), user).get());
          }
        });
  }

  @GetMapping(path = "feed")
  public ResponseEntity getFeed(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(articleQueryService.findUserFeed(user, new Page(offset, limit)));
  }

  @GetMapping
  public ResponseEntity getArticles(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "favorited", required = false) String favoritedBy,
      @RequestParam(value = "author", required = false) String author,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(
        articleQueryService.findRecentArticles(
            tag, author, favoritedBy, new Page(offset, limit), user));
  }

  @GetMapping(path = "export")
  public ResponseEntity<byte[]> exportArticles(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "favorited", required = false) String favoritedBy,
      @RequestParam(value = "author", required = false) String author,
      @AuthenticationPrincipal User user) {
    try {
      ArticleDataList articleDataList =
          articleQueryService.findRecentArticles(
              tag, author, favoritedBy, new Page(offset, limit), user);
      byte[] csvBytes = articleCsvExportService.generateCsv(articleDataList.getArticleDatas());

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
      headers.setContentDispositionFormData("attachment", "articles.csv");

      return ResponseEntity.ok().headers(headers).body(csvBytes);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
