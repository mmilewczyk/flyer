package pl.matcodem.trackingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "stopovers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stopover {

    @Id
    private String stopoverCode;

    @ManyToOne
    @JoinColumn(name = "stopover_code")
    private Airport airport;

    private Integer stopoverDurationMinutes;

    @OneToOne(mappedBy = "designator_code")
    private Segment precedingSegment;

    public boolean isShortStopover() {
        String destinationCountry = precedingSegment.getArrivalAirport().getCountry();
        boolean isTheSameCountry = airport.getCountry().equals(destinationCountry);
        return isTheSameCountry ? stopoverDurationMinutes <= 30 : stopoverDurationMinutes <= 60;
    }

    public LocalTime getStopoverDurationTime() {
        int hours = this.stopoverDurationMinutes / 60;
        int minutes = this.stopoverDurationMinutes % 60;
        return LocalTime.of(hours, minutes);
    }
}
