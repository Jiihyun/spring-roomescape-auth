package roomescape.reservation.exception;

import roomescape.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {

    RESERVATION_ALREADY_EXISTS,
    RESERVATION_NOT_FOUND,
    PAST_DATE_NOT_ALLOWED,
    THEME_STORE_MISMATCH,
    RESERVATION_UPDATE_DEADLINE_PASSED,
    ;
}
