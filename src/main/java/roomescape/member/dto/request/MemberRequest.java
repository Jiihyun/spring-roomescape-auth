package roomescape.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import roomescape.member.domain.Member;

public record MemberRequest(
        @NotBlank(message = "이름을 입력해 주세요.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z]{2,30}$",
                message = "닉네임은 2자 이상 30자 이하의 한글 또는 영문만 사용할 수 있습니다."
        )
        String name,

        @NotBlank(message = "이메일을 입력해 주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$",
                message = "비밀번호는 영문과 숫자를 포함한 8~20자여야 합니다."
        )
        String password
) {
    public Member toMember(String encodedPassword) {
        return Member.createUser(name, email, encodedPassword);
    }

    public Member toAdmin(String encodedPassword) {
        return Member.createAdmin(name, email, encodedPassword);
    }
}
