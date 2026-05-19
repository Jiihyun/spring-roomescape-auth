package roomescape.auth.resolver;

import static roomescape.auth.AuthController.SESSION_KEY;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.auth.exception.UnauthorizedException;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new UnauthorizedException(AuthErrorCode.UNAUTHORIZED_MEMBER);
        }

        Object member = session.getAttribute(SESSION_KEY);
        if (!(member instanceof LoginMember loginMember)) {
            throw new UnauthorizedException(AuthErrorCode.UNAUTHORIZED_MEMBER);
        }
        return loginMember;
    }
}
