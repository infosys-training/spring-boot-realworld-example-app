package io.spring.infrastructure.repository;

import io.spring.core.booking.Booking;
import io.spring.core.booking.BookingRepository;
import io.spring.infrastructure.mybatis.mapper.BookingMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisBookingRepository implements BookingRepository {
  private BookingMapper bookingMapper;

  public MyBatisBookingRepository(BookingMapper bookingMapper) {
    this.bookingMapper = bookingMapper;
  }

  @Override
  public void save(Booking booking) {
    bookingMapper.insert(booking);
  }

  @Override
  public Optional<Booking> findById(String id) {
    return Optional.ofNullable(bookingMapper.findById(id));
  }

  @Override
  public List<Booking> findByUserId(String userId, int offset, int limit) {
    return bookingMapper.findByUserId(userId, offset, limit);
  }

  @Override
  public int countByUserId(String userId) {
    return bookingMapper.countByUserId(userId);
  }

  @Override
  public void update(Booking booking) {
    bookingMapper.update(booking);
  }
}
