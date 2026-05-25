package roomescape.exception.response;

public record ErrorResponse(
        String exceptionCode,
        String message
) {
}
