package roomescape.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.auth.exception.AuthErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.member.exception.MemberErrorCode;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.store.dao.AdminStoreDao;
import roomescape.store.dao.StoreDao;
import roomescape.store.domain.Store;
import roomescape.storetheme.dao.StoreThemeDao;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeErrorCode;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final StoreDao storeDao;
    private final StoreThemeDao storeThemeDao;
    private final MemberDao memberDao;
    private final AdminStoreDao adminStoreDao;
    private final Clock clock;

    public ReservationService(ReservationDao reservationDao, ReservationTimeDao reservationTimeDao,
                              MemberDao memberDao, ThemeDao themeDao, StoreDao storeDao,
                              StoreThemeDao storeThemeDao, AdminStoreDao adminStoreDao, Clock clock) {
        this.reservationDao = reservationDao;
        this.reservationTimeDao = reservationTimeDao;
        this.themeDao = themeDao;
        this.storeDao = storeDao;
        this.storeThemeDao = storeThemeDao;
        this.memberDao = memberDao;
        this.adminStoreDao = adminStoreDao;
        this.clock = clock;
    }

    public ReservationResponse create(LoginMember loginMember, ReservationRequest request) {
        ReservationTime reservationTime = getTime(request.timeId());
        Store store = getStore(request.storeId());
        Theme theme = getTheme(request.themeId());
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        Member member = getMember(loginMember);
        validateStoreThemeExists(request.storeId(), theme.getId());
        validateManagerStore(member, request.storeId());

        Reservation reservation = request.toReservation(member, store, reservationTime, theme, currentDateTime);
        validateUniqueReservation(store.getId(), theme.getId(), reservation.getDate(), reservationTime.getId());

        Reservation savedReservation = reservationDao.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    private ReservationTime getTime(long timeId) {
        return reservationTimeDao.findById(timeId)
                .orElseThrow(() -> new RoomescapeException(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND));
    }

    private Theme getTheme(long themeId) {
        return themeDao.findById(themeId)
                .orElseThrow(() -> new RoomescapeException(ThemeErrorCode.THEME_NOT_FOUND));
    }

    private Store getStore(long storeId) {
        return storeDao.findById(storeId)
                .orElseThrow(() -> new RoomescapeException(ThemeErrorCode.STORE_NOT_FOUND));
    }

    private Member getMember(LoginMember loginMember) {
        return memberDao.findById(loginMember.id())
                .orElseThrow(() -> new RoomescapeException(MemberErrorCode.MEMBER_NOT_EXISTS));
    }

    private void validateStoreThemeExists(long storeId, long themeId) {
        if (!storeThemeDao.existsByStoreIdAndThemeId(storeId, themeId)) {
            throw new RoomescapeException(ReservationErrorCode.THEME_STORE_MISMATCH);
        }
    }

    private void validateManagerStore(Member member, long storeId) {
        if (!adminStoreDao.existsByMemberIdAndStoreId(member.getId(), storeId)) {
            log.warn("관리자가 담당하지 않은 매장에 접근 시도 - memberId={}, storeId={}", member.getId(), storeId);
            throw new RoomescapeException(AuthErrorCode.ADMIN_ACCESS_DENIED);
        }
    }

    private void validateUniqueReservation(long storeId, long themeId, LocalDate date, long timeId) {
        boolean exists = reservationDao.existsByStoreAndThemeAndDateAndTime(storeId, themeId, date, timeId);
        if (exists) {
            throw new RoomescapeException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    public List<ReservationResponse> getReservations(LoginMember loginMember) {
        getMember(loginMember);
        List<Reservation> reservations = findReservations(loginMember);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<Reservation> findReservations(LoginMember loginMember) {
        if (loginMember.role().isAdmin()) {
            return reservationDao.findAllByAdminId(loginMember.id());
        }
        return reservationDao.findAll();
    }

    public List<ReservationResponse> getReservationsByName(String name, LoginMember loginMember) {
        getMember(loginMember);
        List<Reservation> reservations = findReservationsByName(name, loginMember);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<Reservation> findReservationsByName(String name, LoginMember loginMember) {
        if (loginMember.role().isAdmin()) {
            return reservationDao.findAllByNameAndMemberId(name, loginMember.id());
        }
        return reservationDao.findAllByName(name);
    }

    public ReservationResponse update(LoginMember loginMember, long reservationId, ReservationRequest request) {
        Member member = getMember(loginMember);
        Reservation reservation = getReservation(reservationId);
        validateModifiable(reservation);

        ReservationTime reservationTime = getTime(request.timeId());
        Store store = getStore(request.storeId());
        Theme theme = getTheme(request.themeId());
        validateStoreThemeExists(request.storeId(), theme.getId());
        validateManagerStore(member, request.storeId());
        validateUniqueReservationForUpdate(reservationId, store, theme, request.date(), reservationTime);

        Reservation updatedReservation = Reservation.createFutureReservation(
                member, store, request.date(), reservationTime, theme, LocalDateTime.now(clock)
        ).createWithId(reservationId);
        reservationDao.update(updatedReservation);
        return ReservationResponse.from(updatedReservation);
    }

    private Reservation getReservation(long reservationId) {
        return reservationDao.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    private void validateModifiable(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now(clock);

        if (reservation.isNotModifiableAt(now)) {
            throw new RoomescapeException(ReservationErrorCode.RESERVATION_UPDATE_DEADLINE_PASSED);
        }
    }

    private void validateUniqueReservationForUpdate(long reservationId, Store store, Theme theme,
                                                    LocalDate date, ReservationTime reservationTime) {
        boolean exists = reservationDao.existsByStoreAndThemeAndDateAndTimeAndIdNot(
                store.getId(), theme.getId(), date,
                reservationTime.getId(), reservationId);
        if (exists) {
            throw new RoomescapeException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    public void delete(LoginMember loginMember, long reservationId) {
        Member member = getMember(loginMember);
        Reservation reservation = getReservation(reservationId);
        validateModifiable(reservation);
        validateStoreThemeExists(reservation.getStoreId(), reservation.getTheme().getId());
        validateManagerStore(member, reservation.getStoreId());
        reservationDao.delete(reservationId);
    }
}
