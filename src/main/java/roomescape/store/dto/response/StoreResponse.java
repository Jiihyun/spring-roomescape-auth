package roomescape.store.dto.response;

import roomescape.store.domain.Store;

public record StoreResponse(
        Long id,
        String name
) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(store.getId(), store.getName());
    }
}
