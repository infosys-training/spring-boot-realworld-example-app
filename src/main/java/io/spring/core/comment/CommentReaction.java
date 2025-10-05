package io.spring.core.comment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class CommentReaction {
  private String commentId;
  private String userId;
  private ReactionType reactionType;
  private DateTime createdAt;

  public CommentReaction(String commentId, String userId, ReactionType reactionType) {
    this.commentId = commentId;
    this.userId = userId;
    this.reactionType = reactionType;
    this.createdAt = new DateTime();
  }

  public enum ReactionType {
    LIKE,
    DISLIKE
  }
}
