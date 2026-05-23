package roomescape.store.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.store.dao.StoreDao;
import roomescape.store.domain.Store;
import roomescape.store.dto.request.StoreRequest;
import roomescape.store.dto.response.StoreResponse;

@Service
public class StoreService {

    private final StoreDao storeDao;

    public StoreService(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    public StoreResponse create(StoreRequest request) {
        Store savedStore = storeDao.save(request.toStore());
        return StoreResponse.from(savedStore);
    }

    public List<StoreResponse> getStores() {
        return storeDao.findAll().stream()
                .map(StoreResponse::from)
                .toList();
    }
}
