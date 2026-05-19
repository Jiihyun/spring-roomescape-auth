package roomescape.member.dto.response;

import roomescape.member.domain.Member;

public record MemberResponse(
        long id,
        String name,
        String email
) {
    public static MemberResponse from(Member savedMember) {
        return new MemberResponse(savedMember.getId(), savedMember.getName(),savedMember.getEmail());
    }
}
