package roomescape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.exception.code.CommonErrorCode;
import roomescape.member.exception.MemberErrorCode;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.theme.exception.ThemeErrorCode;

@Component
public class DomainErrorHttpMapper {

    public HttpStatus statusOf(ErrorCode errorCode) {
        if (errorCode instanceof MemberErrorCode memberErrorCode) {
            return memberStatusOf(memberErrorCode);
        }

        if (errorCode instanceof ReservationErrorCode reservationErrorCode) {
            return reservationStatusOf(reservationErrorCode);
        }

        if (errorCode instanceof ReservationTimeErrorCode reservationTimeErrorCode) {
            return reservationTimeStatusOf(reservationTimeErrorCode);
        }

        if (errorCode instanceof ThemeErrorCode themeErrorCode) {
            return themeStatusOf(themeErrorCode);
        }

        if (errorCode instanceof AuthErrorCode authErrorCode) {
            return authStatusOf(authErrorCode);
        }

        if (errorCode instanceof CommonErrorCode commonErrorCode) {
            return commonStatusOf(commonErrorCode);
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private HttpStatus memberStatusOf(MemberErrorCode errorCode) {
        return switch (errorCode) {
            case MEMBER_NOT_EXISTS -> HttpStatus.NOT_FOUND;
            case MEMBER_ALREADY_EXISTS -> HttpStatus.CONFLICT;
        };
    }

    private HttpStatus reservationStatusOf(ReservationErrorCode errorCode) {
        return switch (errorCode) {
            case RESERVATION_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case RESERVATION_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case PAST_DATE_NOT_ALLOWED, THEME_STORE_MISMATCH -> HttpStatus.BAD_REQUEST;
            case RESERVATION_UPDATE_DEADLINE_PASSED -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    private HttpStatus reservationTimeStatusOf(ReservationTimeErrorCode errorCode) {
        return switch (errorCode) {
            case RESERVATION_TIME_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case RESERVATION_TIME_HAS_RESERVATION, RESERVATION_TIME_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case INVALID_RESERVATION_TIME_RANGE -> HttpStatus.UNPROCESSABLE_ENTITY;
            case INVALID_RESERVATION_TIME_UNIT -> HttpStatus.BAD_REQUEST;
        };
    }

    private HttpStatus themeStatusOf(ThemeErrorCode errorCode) {
        return switch (errorCode) {
            case THEME_ALREADY_EXISTS, THEME_HAS_RESERVATION -> HttpStatus.CONFLICT;
            case THEME_NOT_FOUND, STORE_NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
    }

    private HttpStatus authStatusOf(AuthErrorCode errorCode) {
        return switch (errorCode) {
            case AUTHENTICATION_NEEDED, INVALID_LOGIN -> HttpStatus.UNAUTHORIZED;
            case ADMIN_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case MANAGER_NOT_ASSIGNED_TO_STORE -> HttpStatus.NOT_FOUND;
        };
    }

    private HttpStatus commonStatusOf(CommonErrorCode errorCode) {
        return switch (errorCode) {
            case INVALID_REQUEST_BODY, INVALID_REQUEST_PARAMETER_TYPE -> HttpStatus.BAD_REQUEST;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
