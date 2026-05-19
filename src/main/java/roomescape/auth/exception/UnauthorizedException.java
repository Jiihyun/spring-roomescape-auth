package roomescape.auth.exception;

import roomescape.exception.RoomescapeException;
import roomescape.reservation.exception.ReservationErrorCode;

public class UnauthorizedException extends RoomescapeException {

    public UnauthorizedException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}
