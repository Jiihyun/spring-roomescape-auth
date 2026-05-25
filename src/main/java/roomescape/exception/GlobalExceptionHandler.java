package roomescape.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import roomescape.exception.code.CommonErrorCode;
import roomescape.exception.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;
    private final DomainErrorHttpMapper httpMapper;

    public GlobalExceptionHandler(final MessageSource messageSource, final DomainErrorHttpMapper httpMapper) {
        this.messageSource = messageSource;
        this.httpMapper = httpMapper;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_BODY;
        String message = extractFieldErrorMessage(exception);
        return ResponseEntity
                .status(httpMapper.statusOf(errorCode))
                .body(new ErrorResponse(errorCode.name(), message));
    }

    private String extractFieldErrorMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElseGet(() -> messageSource.getMessage(
                        CommonErrorCode.INVALID_REQUEST_BODY.messageKey(),
                        null,
                        LocaleContextHolder.getLocale()
                ));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_BODY;
        String message = messageSource.getMessage(
                errorCode.messageKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        return ResponseEntity
                .status(httpMapper.statusOf(errorCode))
                .body(new ErrorResponse(errorCode.name(), message));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        CommonErrorCode errorCode = CommonErrorCode.INVALID_REQUEST_PARAMETER_TYPE;
        String message = messageSource.getMessage(
                errorCode.messageKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        return ResponseEntity
                .status(httpMapper.statusOf(errorCode))
                .body(new ErrorResponse(errorCode.name(), message));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        if (!isAlreadyLogged(exception)) {
            log.warn("스프링 기본 예외 처리: path={}, status={}, exception={}, message={}",
                    getPath(request),
                    statusCode.value(),
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
        }
        return ResponseEntity
                .status(statusCode)
                .body(new ErrorResponse(HttpStatus.valueOf(statusCode.value()).name(), exception.getMessage()));
    }

    private boolean isAlreadyLogged(Exception exception) {
        return exception instanceof MethodArgumentNotValidException
                || exception instanceof HttpMessageNotReadableException
                || exception instanceof TypeMismatchException;
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(RoomescapeException roomescapeException,
                                                                 Locale locale) {
        ErrorCode errorCode = roomescapeException.getExceptionCode();
        String message = messageSource.getMessage(
                errorCode.messageKey(),
                null,
                locale
        );
        return ResponseEntity
                .status(httpMapper.statusOf(errorCode))
                .body(new ErrorResponse(
                        errorCode.name(),
                        message
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        log.error("예상하지 못한 서버 오류 발생: method={}, path={}",
                request.getMethod(), request.getRequestURI(), exception);

        CommonErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        String message = messageSource.getMessage(
                errorCode.messageKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        return ResponseEntity
                .status(httpMapper.statusOf(errorCode))
                .body(new ErrorResponse(
                        errorCode.name(),
                        message
                ));
    }
}
