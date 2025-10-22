package io.spring.infrastructure.favorite;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisArticleFavoriteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({MyBatisArticleFavoriteRepository.class})
public class MyBatisArticleFavoriteRepositoryTest extends DbTestBase {
  @Autowired private ArticleFavoriteRepository articleFavoriteRepository;

  @Autowired
  private io.spring.infrastructure.mybatis.mapper.ArticleFavoriteMapper articleFavoriteMapper;

  @Test
  public void should_save_and_fetch_articleFavorite_success() {
    ArticleFavorite articleFavorite = new ArticleFavorite("123", "456");
    articleFavoriteRepository.save(articleFavorite);
    Assertions.assertNotNull(
        articleFavoriteMapper.find(articleFavorite.getArticleId(), articleFavorite.getUserId()));
  }

  @Test
  public void should_remove_favorite_success() {
    ArticleFavorite articleFavorite = new ArticleFavorite("123", "456");
    articleFavoriteRepository.save(articleFavorite);
    articleFavoriteRepository.remove(articleFavorite);
    Assertions.assertFalse(articleFavoriteRepository.find("123", "456").isPresent());
  }

  @Test
  public void should_handle_duplicate_favorite_inserts_idempotently() {
    String articleId = "duplicate-test-article";
    String userId = "duplicate-test-user";

    ArticleFavorite articleFavorite = new ArticleFavorite(articleId, userId);
    articleFavoriteRepository.save(articleFavorite);
    articleFavoriteRepository.save(articleFavorite);

    ArticleFavorite result = articleFavoriteMapper.find(articleId, userId);
    Assertions.assertNotNull(result, "Favorite should exist after duplicate saves");
    Assertions.assertEquals(articleId, result.getArticleId());
    Assertions.assertEquals(userId, result.getUserId());
  }
}
