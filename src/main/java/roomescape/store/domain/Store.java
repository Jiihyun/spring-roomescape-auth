package roomescape.store.domain;

import java.util.Objects;

public class Store {

    private final Long id;
    private final String name;

    public Store(String name) {
        this(null, name);
    }

    public Store(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Store createWithId(Long id) {
        return new Store(id, this.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Store store = (Store) object;
        if (id != null && store.id != null) {
            return Objects.equals(id, store.id);
        }
        return Objects.equals(name, store.name);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }
}
