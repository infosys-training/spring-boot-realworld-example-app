package io.spring.core.flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository {

  void save(Flight flight);

  Optional<Flight> findById(String id);

  List<Flight> findAll(String origin, String destination, int offset, int limit);

  int count(String origin, String destination);
}
