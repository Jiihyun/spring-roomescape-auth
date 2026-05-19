package roomescape.reservationtime.exception;

import roomescape.exception.RoomescapeException;

public class ReservationTimeException extends RoomescapeException {

    public ReservationTimeException(ReservationTimeErrorCode reservationTimeErrorCode) {
        super(reservationTimeErrorCode);
    }
}
