package roomescape.theme.domain;

import java.util.Objects;

public class Theme {

    private Long id;
    private final Long storeId;
    private final String name;
    private final String description;
    private final String thumbnail;

    public Theme(String name, String description, String thumbnail) {
        this(null, name, description, thumbnail);
    }

    public Theme(Long id, String name, String description, String thumbnail) {
        this(id, null, name, description, thumbnail);
    }

    public Theme(Long id, Long storeId, String name, String description, String thumbnail) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public Theme createWithId(Long id) {
        return new Theme(id, this.storeId, this.name, this.description, this.thumbnail);
    }

    public Long getId() {
        return id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Theme theme = (Theme) object;
        if (id != null && theme.id != null) {
            return Objects.equals(id, theme.id);
        }
        return Objects.equals(storeId, theme.storeId) && Objects.equals(name, theme.name) && Objects.equals(description, theme.description)
                && Objects.equals(thumbnail, theme.thumbnail);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(storeId, name, description, thumbnail);
    }
}
