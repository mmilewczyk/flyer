package pl.matcodem.trackingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.matcodem.trackingservice.entity.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, String> {

    List<Flight> findFlightsByDepartureAndArrivalAirports(String departureIcao, String arrivalIcao);
    List<Flight> findFlightsByDepartureIcaoCodeAndDateTimeAfter(String departureIcao, LocalDateTime date);
    Page<Flight> findFlightsByDepartureIcaoCodeAndDateTimeAfter(String departureIcao, LocalDateTime date, Pageable pageable);
    List<Flight> findFlightsByArrivalAirport(String arrivalIcao);
    Page<Flight> findFlightsByDepartureAndArrivalAirportsAndDate(String departureIcao, String arrivalIcao, LocalDateTime date, Pageable pageable);
}
