package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.booking.Booking;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookingMapper {
  void insert(@Param("booking") Booking booking);

  Booking findById(@Param("id") String id);

  List<Booking> findByUserId(
      @Param("userId") String userId, @Param("offset") int offset, @Param("limit") int limit);

  int countByUserId(@Param("userId") String userId);

  void update(@Param("booking") Booking booking);
}
