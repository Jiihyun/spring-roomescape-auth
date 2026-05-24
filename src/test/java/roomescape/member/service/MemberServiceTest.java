package roomescape.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.ServiceTest;
import roomescape.member.MemberErrorCode;
import roomescape.member.dto.request.MemberRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.exception.MemberException;

class MemberServiceTest extends ServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void 회원을_생성할_수_있다() {
        // given
        MemberRequest request = new MemberRequest("브라운", "brown@email.com", "password123");

        // when
        MemberResponse response = memberService.createUser(request);

        // then
        assertThat(response)
                .extracting(MemberResponse::name, MemberResponse::email)
                .containsExactly("브라운", "brown@email.com");
    }

    @Test
    void 이미_존재하는_이메일로_회원_생성시_예외가_발생한다() {
        // given
        MemberRequest request = new MemberRequest("브라운", "brown@email.com", "password123");
        memberService.createUser(request);

        // when & then
        assertThatThrownBy(() -> memberService.createUser(request))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.MEMBER_ALREADY_EXISTS.getMessage());
    }
}
