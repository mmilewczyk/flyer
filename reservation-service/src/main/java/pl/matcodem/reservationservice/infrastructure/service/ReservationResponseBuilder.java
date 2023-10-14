package pl.matcodem.reservationservice.infrastructure.service;

import org.springframework.stereotype.Service;
import pl.matcodem.reservationservice.application.response.ReservationResponse;
import pl.matcodem.reservationservice.domain.model.valueobjects.Passenger;
import pl.matcodem.reservationservice.domain.model.valueobjects.ReservationDate;

import java.math.BigDecimal;

@Service
public class ReservationResponseBuilder {
    public ReservationResponse build(
            ReservationDate reservationDate,
            Passenger passenger,
            ReservationResponse.FlightInfo flightInfo
    ) {
        return ReservationResponse.builder()
                .reservationDate(reservationDate.date())
                .passenger(passenger)
                .flightInfo(flightInfo)
                .price(BigDecimal.ZERO)
                .build();
    }
}
