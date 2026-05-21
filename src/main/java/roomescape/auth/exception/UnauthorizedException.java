package roomescape.auth.exception;

import roomescape.exception.RoomescapeException;

public class UnauthorizedException extends RoomescapeException {

    public UnauthorizedException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}
