package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.cache.ArticleCacheService;
import io.spring.infrastructure.mybatis.mapper.ArticleFavoriteMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MyBatisArticleFavoriteRepository implements ArticleFavoriteRepository {
  private ArticleFavoriteMapper mapper;
  private ArticleCacheService articleCacheService;

  @Autowired
  public MyBatisArticleFavoriteRepository(
      ArticleFavoriteMapper mapper,
      @Autowired(required = false) ArticleCacheService articleCacheService) {
    this.mapper = mapper;
    this.articleCacheService = articleCacheService;
  }

  @Override
  @Transactional
  public void save(ArticleFavorite articleFavorite) {
    try {
      mapper.insert(articleFavorite);
      if (articleCacheService != null) {
        articleCacheService.invalidateArticle(articleFavorite.getArticleId());
      }
    } catch (DuplicateKeyException e) {
      if (articleCacheService != null) {
        articleCacheService.invalidateArticle(articleFavorite.getArticleId());
      }
    }
  }

  @Override
  public Optional<ArticleFavorite> find(String articleId, String userId) {
    return Optional.ofNullable(mapper.find(articleId, userId));
  }

  @Override
  @Transactional
  public void remove(ArticleFavorite favorite) {
    mapper.delete(favorite);
    if (articleCacheService != null) {
      articleCacheService.invalidateArticle(favorite.getArticleId());
    }
  }
}
