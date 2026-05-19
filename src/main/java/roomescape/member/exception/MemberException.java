package roomescape.member.exception;

import roomescape.exception.RoomescapeException;
import roomescape.member.MemberErrorCode;

public class MemberException extends RoomescapeException {

    public MemberException(MemberErrorCode memberErrorCode) {
        super(memberErrorCode);
    }
}
