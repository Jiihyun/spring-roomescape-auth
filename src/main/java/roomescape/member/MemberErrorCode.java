package roomescape.member;

import org.springframework.http.HttpStatus;
import roomescape.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
    
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 이메일을 지닌 멤버가 이미 존재합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    MemberErrorCode(HttpStatus httpStatus, String message) {
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
