package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final String LOCATION_DEFAULT_VALUE = "/reservations/";

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(LoginMember loginMember,
                                                      @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.create(loginMember, request);
        return ResponseEntity.created(URI.create(LOCATION_DEFAULT_VALUE + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readReservations(LoginMember loginMember,
                                                                      @RequestParam(required = false, value = "name") String name) {
        if (name == null || name.isBlank()) {
            List<ReservationResponse> responses = reservationService.getReservations(loginMember);
            return ResponseEntity.ok(responses);
        }
        List<ReservationResponse> responses = reservationService.getReservationsByName(name, loginMember);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> update(LoginMember loginMember,
                                                      @PathVariable("reservationId") long reservationId,
                                                      @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.update(loginMember,reservationId, request);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> delete(LoginMember loginMember,
                                       @PathVariable("reservationId") long reservationId) {
        reservationService.delete(reservationId);
        return ResponseEntity.noContent()
                .build();
    }
}
