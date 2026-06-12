package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.flight.Flight;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FlightMapper {
  void insert(@Param("flight") Flight flight);

  Flight findById(@Param("id") String id);

  List<Flight> findAll(
      @Param("origin") String origin,
      @Param("destination") String destination,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int count(@Param("origin") String origin, @Param("destination") String destination);

  void update(@Param("flight") Flight flight);
}
