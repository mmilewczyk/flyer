package pl.matcodem.reservationservice.infrastructure.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.matcodem.reservationservice.domain.model.Reservation;
import pl.matcodem.reservationservice.domain.model.valueobjects.*;
import pl.matcodem.reservationservice.exceptions.ReservationModificationNotAllowedException;
import pl.matcodem.reservationservice.util.DateCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificationPeriodValidatorTest {

    @Mock
    private DateCalculator dateCalculator;

    private ModificationPeriodValidator modificationPeriodValidator;

    @BeforeEach
    void setUp() {
        dateCalculator = Mockito.mock(DateCalculator.class);
        modificationPeriodValidator = new ModificationPeriodValidator(dateCalculator, 3);
    }

    @Test
    void shouldAllowModificationWhenWithinAllowedPeriod() {
        LocalDate currentDate = LocalDate.now();
        LocalDate flightDate = LocalDate.now().plusDays(5); // Within the allowed period

        Mockito.when(dateCalculator.getCurrentDate()).thenReturn(currentDate);
        Mockito.when(dateCalculator.calculatePeriod(currentDate, flightDate)).thenReturn(Period.ofDays(5));

        Reservation reservation = new Reservation(
                new ReservationId(UUID.randomUUID().toString()),
                new ReservationCode(UUID.randomUUID().toString()),
                new ReservationDate(flightDate),
                Collections.singletonList(new Passenger(
                        "John",
                        "Doe",
                        "12345678901",
                        null,
                        "john@example.com",
                        "123456789",
                        LocalDate.of(2000, 4, 3),
                        23,
                        "None",
                        "Economy",
                        "Window"
                )),
                new FlightNumber(UUID.randomUUID().toString()),
                new Cost(new BigDecimal(450), new BigDecimal(20)),
                FlightReservationStatus.CONFIRMED
        );

        // No exception should be thrown
        modificationPeriodValidator.isReservationWithinAllowedModificationPeriod(reservation);
        assertDoesNotThrow(() -> modificationPeriodValidator.isReservationWithinAllowedModificationPeriod(reservation));
    }

    @Test
    void shouldDisallowModificationWhenOutsideAllowedPeriod() {
        LocalDate currentDate = LocalDate.now();
        LocalDate flightDate = LocalDate.now().plusDays(2); // Outside the allowed period

        Mockito.when(dateCalculator.getCurrentDate()).thenReturn(currentDate);
        Mockito.when(dateCalculator.calculatePeriod(currentDate, flightDate)).thenReturn(Period.ofDays(2));

        Reservation reservation = new Reservation(
                new ReservationId(UUID.randomUUID().toString()),
                new ReservationCode(UUID.randomUUID().toString()),
                new ReservationDate(flightDate),
                Collections.singletonList(new Passenger(
                        "John",
                        "Doe",
                        "12345678901",
                        null,
                        "john@example.com",
                        "123456789",
                        LocalDate.of(2000, 4, 3),
                        23,
                        "None",
                        "Economy",
                        "Window"
                )),
                new FlightNumber(UUID.randomUUID().toString()),
                new Cost(new BigDecimal(450), new BigDecimal(20)),
                FlightReservationStatus.CONFIRMED
        );

        // Expect a ReservationModificationNotAllowedException to be thrown
        assertThrows(ReservationModificationNotAllowedException.class, () -> modificationPeriodValidator.isReservationWithinAllowedModificationPeriod(reservation));
    }
}
