package roomescape.auth.exception;

import roomescape.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    AUTHENTICATION_NEEDED,
    INVALID_LOGIN,
    ADMIN_ACCESS_DENIED,
    MANAGER_NOT_ASSIGNED_TO_STORE,
    ;
}
