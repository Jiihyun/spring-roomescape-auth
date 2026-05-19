package roomescape.auth.exception;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
