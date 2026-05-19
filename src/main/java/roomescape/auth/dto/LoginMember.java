package roomescape.auth.dto;

import roomescape.reservation.domain.Role;

public record LoginMember(
        long id,
        String name,
        String email,
        Role role
) {
}
