package roomescape.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.SessionManager;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.exception.RoomescapeException;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final SessionManager sessionManager;

    public AdminAuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        LoginMember member = sessionManager.getLoginMember(request);

        if (!member.role().isAdmin()) {
            throw new RoomescapeException(AuthErrorCode.ADMIN_ACCESS_DENIED);
        }
        return true;
    }
}
