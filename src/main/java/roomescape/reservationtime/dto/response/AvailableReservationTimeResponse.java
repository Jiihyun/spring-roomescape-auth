package roomescape.reservationtime.dto.response;

import java.time.LocalTime;
import roomescape.reservationtime.dao.dto.ReservationTimeAvailability;

public record AvailableReservationTimeResponse(
        long id,
        LocalTime startAt,
        boolean reserved
) {
    public static AvailableReservationTimeResponse from(ReservationTimeAvailability reservationTimeAvailability) {
        return new AvailableReservationTimeResponse(
                reservationTimeAvailability.id(),
                reservationTimeAvailability.startAt(),
                reservationTimeAvailability.reserved()
        );
    }
}
