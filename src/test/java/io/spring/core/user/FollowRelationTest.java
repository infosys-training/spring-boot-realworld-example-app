package io.spring.core.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FollowRelationTest {

  @Test
  public void should_create_follow_relation_with_user_and_target() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");

    Assertions.assertEquals("user-123", relation.getUserId());
    Assertions.assertEquals("target-456", relation.getTargetId());
  }

  @Test
  public void should_create_with_no_args_constructor() {
    FollowRelation relation = new FollowRelation();

    Assertions.assertNull(relation.getUserId());
    Assertions.assertNull(relation.getTargetId());
  }

  @Test
  public void should_set_user_id() {
    FollowRelation relation = new FollowRelation();
    relation.setUserId("user-789");

    Assertions.assertEquals("user-789", relation.getUserId());
  }

  @Test
  public void should_set_target_id() {
    FollowRelation relation = new FollowRelation();
    relation.setTargetId("target-789");

    Assertions.assertEquals("target-789", relation.getTargetId());
  }

  @Test
  public void should_be_equal_when_all_fields_match() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-456");

    Assertions.assertEquals(relation1, relation2);
  }

  @Test
  public void should_not_be_equal_when_user_ids_differ() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-999", "target-456");

    Assertions.assertNotEquals(relation1, relation2);
  }

  @Test
  public void should_not_be_equal_when_target_ids_differ() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-999");

    Assertions.assertNotEquals(relation1, relation2);
  }

  @Test
  public void should_have_same_hash_code_when_equal() {
    FollowRelation relation1 = new FollowRelation("user-123", "target-456");
    FollowRelation relation2 = new FollowRelation("user-123", "target-456");

    Assertions.assertEquals(relation1.hashCode(), relation2.hashCode());
  }

  @Test
  public void should_support_to_string() {
    FollowRelation relation = new FollowRelation("user-123", "target-456");

    String result = relation.toString();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.contains("user-123"));
    Assertions.assertTrue(result.contains("target-456"));
  }
}
