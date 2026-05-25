package roomescape.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import roomescape.member.exception.MemberErrorCode;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;
import roomescape.member.dto.request.MemberRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.exception.RoomescapeException;

@Service
public class MemberService {

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberDao memberDao, PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberResponse createUser(MemberRequest request) {
        validateUniqueEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = request.toMember(encodedPassword);
        Member savedMember = memberDao.save(member);
        return MemberResponse.from(savedMember);
    }

    public MemberResponse createAdmin(MemberRequest request) {
        validateUniqueEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = request.toAdmin(encodedPassword);
        Member savedMember = memberDao.save(member);
        return MemberResponse.from(savedMember);
    }

    public void validateUniqueEmail(String email) {
        boolean exists = memberDao.existsByEmail(email);
        if (exists) {
            throw new RoomescapeException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }
}
