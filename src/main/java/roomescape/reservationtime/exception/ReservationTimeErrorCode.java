package roomescape.reservationtime.exception;

import roomescape.exception.ErrorCode;

public enum ReservationTimeErrorCode implements ErrorCode {

    RESERVATION_TIME_NOT_FOUND,
    RESERVATION_TIME_HAS_RESERVATION,
    RESERVATION_TIME_ALREADY_EXISTS,
    INVALID_RESERVATION_TIME_RANGE,
    INVALID_RESERVATION_TIME_UNIT,
    ;
}
