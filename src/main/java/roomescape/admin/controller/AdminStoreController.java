package roomescape.admin.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.store.dto.request.StoreRequest;
import roomescape.store.dto.response.StoreResponse;
import roomescape.store.service.StoreService;

@RestController
@RequestMapping("/admin/stores")
public class AdminStoreController {

    private static final String LOCATION_DEFAULT_VALUE = "/admin/stores/";

    private final StoreService storeService;

    public AdminStoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<StoreResponse> create(@Valid @RequestBody StoreRequest request) {
        StoreResponse response = storeService.create(request);
        return ResponseEntity.created(URI.create(LOCATION_DEFAULT_VALUE + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> readStores() {
        List<StoreResponse> responses = storeService.getStores();
        return ResponseEntity.ok(responses);
    }
}
