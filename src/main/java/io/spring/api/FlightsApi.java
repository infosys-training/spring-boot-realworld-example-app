package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.flight.FlightBookingQueryService;
import io.spring.core.flight.Flight;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/flights")
@AllArgsConstructor
public class FlightsApi {

  private FlightBookingQueryService queryService;

  @GetMapping
  public ResponseEntity getFlights(
      @RequestParam(value = "origin", required = false) String origin,
      @RequestParam(value = "destination", required = false) String destination,
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit) {
    return ResponseEntity.ok(queryService.findFlights(origin, destination, offset, limit));
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity getFlightById(@PathVariable("id") String id) {
    Flight flight =
        queryService.findFlightById(id).orElseThrow(() -> new ResourceNotFoundException());
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("flight", flight);
          }
        });
  }
}
