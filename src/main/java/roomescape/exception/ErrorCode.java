package roomescape.exception;

public interface ErrorCode {

    String name();

    default String messageKey() {
        return "error." + name().toLowerCase();
    }
}
