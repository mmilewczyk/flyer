package pl.matcodem.trackingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "segments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "Segment.findSegmentsByDepartureAndArrivalAirports",
        query = """
                SELECT s FROM Segment s WHERE
                s.arrivalAirport.icaoCode =:arrivalIcao
                AND
                s.departureAirport.icaoCode =:departureIcao
                """)
@NamedQuery(name = "Segment.findSegmentsByArrivalAirport",
        query = "SELECT s FROM Segment s WHERE s.arrivalAirport.icaoCode =:arrivalIcao")
@NamedQuery(name = "Segment.findSegmentsByDepartureAirport",
        query = "SELECT s FROM Segment s WHERE s.departureAirport.icaoCode =:departureIcao")
public class Segment {

    @Id
    private String designatorCode;

    @ManyToOne
    @JoinColumn(name = "departure_icao_code")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_icao_code")
    private Airport arrivalAirport;

    @ManyToOne
    @JoinColumn(name = "airline_code")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "call_sign")
    private Aircraft aircraft;

    private String cabin;

    private Integer durationMinutes;
    private LocalDateTime departureDateTime;

    @OneToOne
    @JoinColumn(name = "stopover_code")
    private Stopover stopover;

    public boolean isFinalDestination() {
        return stopover == null;
    }

    public LocalDateTime getArrivalDateTime() {
        return departureDateTime.plusMinutes(durationMinutes);
    }
}
