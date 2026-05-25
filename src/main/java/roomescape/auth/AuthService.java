package roomescape.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;

@Service
public class AuthService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberDao memberDao, PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginMember login(LoginRequest request) {
        Member member = memberDao.findByEmail(request.email())
                .orElseThrow(() -> new RoomescapeException(AuthErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new RoomescapeException(AuthErrorCode.INVALID_LOGIN);
        }
        return new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
    }
}
