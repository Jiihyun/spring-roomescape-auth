package roomescape.auth;

import static roomescape.auth.AuthController.SESSION_KEY;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.auth.exception.UnauthorizedException;

@Component
public class SessionManager {

    public LoginMember getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new UnauthorizedException(AuthErrorCode.AUTHENTICATION_NEEDED);
        }

        Object member = session.getAttribute(SESSION_KEY);

        if (!(member instanceof LoginMember loginMember)) {
            throw new UnauthorizedException(AuthErrorCode.AUTHENTICATION_NEEDED);
        }

        return loginMember;
    }
}
