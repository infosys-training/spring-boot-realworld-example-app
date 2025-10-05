package io.spring.application;

import io.spring.application.CursorPager.Direction;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CursorPageParameterTest {

  @Test
  public void should_create_with_valid_parameters() {
    DateTime cursor = DateTime.now();
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(cursor, 10, Direction.NEXT);

    Assertions.assertEquals(cursor, param.getCursor());
    Assertions.assertEquals(10, param.getLimit());
    Assertions.assertEquals(Direction.NEXT, param.getDirection());
  }

  @Test
  public void should_create_with_default_constructor() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>();

    Assertions.assertNull(param.getCursor());
    Assertions.assertEquals(20, param.getLimit());
    Assertions.assertNull(param.getDirection());
  }

  @Test
  public void should_enforce_max_limit() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 2000, Direction.NEXT);

    Assertions.assertEquals(1000, param.getLimit());
  }

  @Test
  public void should_ignore_negative_limit() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, -10, Direction.NEXT);

    Assertions.assertEquals(20, param.getLimit());
  }

  @Test
  public void should_ignore_zero_limit() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 0, Direction.NEXT);

    Assertions.assertEquals(20, param.getLimit());
  }

  @Test
  public void should_accept_limit_at_max_boundary() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 1000, Direction.NEXT);

    Assertions.assertEquals(1000, param.getLimit());
  }

  @Test
  public void should_accept_limit_of_one() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 1, Direction.NEXT);

    Assertions.assertEquals(1, param.getLimit());
  }

  @Test
  public void should_return_query_limit_as_limit_plus_one() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 10, Direction.NEXT);

    Assertions.assertEquals(11, param.getQueryLimit());
  }

  @Test
  public void should_identify_next_direction() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 10, Direction.NEXT);

    Assertions.assertTrue(param.isNext());
  }

  @Test
  public void should_identify_prev_direction() {
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(null, 10, Direction.PREV);

    Assertions.assertFalse(param.isNext());
  }

  @Test
  public void should_support_equals() {
    DateTime cursor = DateTime.now();
    CursorPageParameter<DateTime> param1 = new CursorPageParameter<>(cursor, 10, Direction.NEXT);
    CursorPageParameter<DateTime> param2 = new CursorPageParameter<>(cursor, 10, Direction.NEXT);

    Assertions.assertEquals(param1, param2);
  }

  @Test
  public void should_support_hash_code() {
    DateTime cursor = DateTime.now();
    CursorPageParameter<DateTime> param1 = new CursorPageParameter<>(cursor, 10, Direction.NEXT);
    CursorPageParameter<DateTime> param2 = new CursorPageParameter<>(cursor, 10, Direction.NEXT);

    Assertions.assertEquals(param1.hashCode(), param2.hashCode());
  }

  @Test
  public void should_support_to_string() {
    DateTime cursor = DateTime.now();
    CursorPageParameter<DateTime> param = new CursorPageParameter<>(cursor, 10, Direction.NEXT);

    String result = param.toString();

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.contains("10"));
  }
}
