package io.spring.application;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  public void should_create_with_datetime() {
    DateTime now = DateTime.now();
    DateTimeCursor cursor = new DateTimeCursor(now);

    Assertions.assertEquals(now, cursor.getData());
  }

  @Test
  public void should_return_datetime_as_millis_string() {
    DateTime now = DateTime.now();
    DateTimeCursor cursor = new DateTimeCursor(now);

    String result = cursor.toString();

    Assertions.assertNotNull(result);
    Assertions.assertEquals(String.valueOf(now.getMillis()), result);
  }

  @Test
  public void should_parse_cursor_string_back_to_datetime() {
    DateTime original = DateTime.now();
    DateTimeCursor cursor = new DateTimeCursor(original);
    
    String cursorString = cursor.toString();
    DateTime parsed = DateTimeCursor.parse(cursorString);

    Assertions.assertNotNull(parsed);
    Assertions.assertEquals(original.getMillis(), parsed.getMillis());
  }

  @Test
  public void should_parse_null_cursor_as_null() {
    DateTime result = DateTimeCursor.parse(null);

    Assertions.assertNull(result);
  }
}
