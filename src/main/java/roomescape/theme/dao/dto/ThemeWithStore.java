package roomescape.theme.dao.dto;

import roomescape.theme.domain.Theme;

public record ThemeWithStore(
        Theme theme,
        Long storeId
) {
}
