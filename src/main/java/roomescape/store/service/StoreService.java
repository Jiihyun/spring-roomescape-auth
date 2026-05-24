package roomescape.store.service;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.store.dao.AdminStoreDao;
import roomescape.store.dao.StoreDao;
import roomescape.store.domain.Store;
import roomescape.store.dto.request.StoreRequest;
import roomescape.store.dto.response.StoreResponse;

@Service
public class StoreService {

    private final StoreDao storeDao;
    private final AdminStoreDao adminStoreDao;

    public StoreService(StoreDao storeDao, AdminStoreDao adminStoreDao) {
        this.storeDao = storeDao;
        this.adminStoreDao = adminStoreDao;
    }

    public StoreResponse create(long memberId, StoreRequest request) {
        Store savedStore = storeDao.save(request.toStore());
        adminStoreDao.save(memberId, savedStore.getId());
        return StoreResponse.from(savedStore);
    }

    public List<StoreResponse> getStores() {
        return storeDao.findAll().stream()
                .map(StoreResponse::from)
                .toList();
    }
}
