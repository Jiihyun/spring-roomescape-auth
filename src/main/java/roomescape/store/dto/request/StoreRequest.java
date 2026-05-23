package roomescape.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import roomescape.store.domain.Store;

public record StoreRequest(
        @NotBlank(message = "매장명을 입력해 주세요.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9 ]{2,30}$",
                message = "매장명은 2자 이상 30자 이하의 한글, 영문, 숫자 또는 공백만 사용할 수 있습니다."
        )
        String name
) {
    public Store toStore() {
        return new Store(name);
    }
}
