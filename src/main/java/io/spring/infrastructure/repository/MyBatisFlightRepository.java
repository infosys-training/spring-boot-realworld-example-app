package io.spring.infrastructure.repository;

import io.spring.core.flight.Flight;
import io.spring.core.flight.FlightRepository;
import io.spring.infrastructure.mybatis.mapper.FlightMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisFlightRepository implements FlightRepository {
  private FlightMapper flightMapper;

  public MyBatisFlightRepository(FlightMapper flightMapper) {
    this.flightMapper = flightMapper;
  }

  @Override
  public void save(Flight flight) {
    if (flightMapper.findById(flight.getId()) == null) {
      flightMapper.insert(flight);
    } else {
      flightMapper.update(flight);
    }
  }

  @Override
  public Optional<Flight> findById(String id) {
    return Optional.ofNullable(flightMapper.findById(id));
  }

  @Override
  public List<Flight> findAll(String origin, String destination, int offset, int limit) {
    return flightMapper.findAll(origin, destination, offset, limit);
  }

  @Override
  public int count(String origin, String destination) {
    return flightMapper.count(origin, destination);
  }
}
