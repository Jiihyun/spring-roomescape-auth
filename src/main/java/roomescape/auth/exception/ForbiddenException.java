package roomescape.auth.exception;

import roomescape.exception.RoomescapeException;

public class ForbiddenException extends RoomescapeException {

    public ForbiddenException(AuthErrorCode authErrorCode) {
        super(authErrorCode);
    }
}
