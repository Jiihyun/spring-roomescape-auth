package roomescape.storetheme.domain;

public class StoreTheme {

    private final Long storeId;
    private final Long themeId;

    public StoreTheme(Long storeId, Long themeId) {
        this.storeId = storeId;
        this.themeId = themeId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Long getThemeId() {
        return themeId;
    }
}
