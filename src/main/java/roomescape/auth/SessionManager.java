package roomescape.auth;

import static roomescape.auth.AuthController.SESSION_KEY;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.exception.RoomescapeException;

@Component
public class SessionManager {

    public LoginMember getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new RoomescapeException(AuthErrorCode.AUTHENTICATION_NEEDED);
        }

        Object member = session.getAttribute(SESSION_KEY);

        if (!(member instanceof LoginMember loginMember)) {
            throw new RoomescapeException(AuthErrorCode.AUTHENTICATION_NEEDED);
        }

        return loginMember;
    }
}
