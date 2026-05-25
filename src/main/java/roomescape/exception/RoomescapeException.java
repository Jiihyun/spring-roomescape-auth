package roomescape.exception;

public class RoomescapeException extends RuntimeException {

    private final ErrorCode errorCode;

    public RoomescapeException(final ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }

    public ErrorCode getExceptionCode() {
        return errorCode;
    }
}
