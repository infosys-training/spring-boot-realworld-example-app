package io.spring.core.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {

  void save(Booking booking);

  Optional<Booking> findById(String id);

  List<Booking> findByUserId(String userId, int offset, int limit);

  int countByUserId(String userId);

  void update(Booking booking);
}
