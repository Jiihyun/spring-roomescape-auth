package roomescape.reservation.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.auth.dto.LoginMember;
import roomescape.member.MemberErrorCode;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;
import roomescape.member.exception.MemberException;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final MemberDao memberDao;
    private final Clock clock;

    public ReservationService(ReservationDao reservationDao, ReservationTimeDao reservationTimeDao,
                              MemberDao memberDao, ThemeDao themeDao, Clock clock) {
        this.reservationDao = reservationDao;
        this.reservationTimeDao = reservationTimeDao;
        this.themeDao = themeDao;
        this.memberDao = memberDao;
        this.clock = clock;
    }

    public ReservationResponse create(LoginMember loginMember, ReservationRequest request) {
        ReservationTime reservationTime = getTime(request.timeId());
        Theme theme = getTheme(request.themeId());
        LocalDateTime currentDateTime = LocalDateTime.now(clock);
        Member member = getMember(loginMember);

        Reservation reservation = request.toReservation(member, reservationTime, theme, currentDateTime);
        validateUniqueReservation(theme.getId(), reservation.getDate(), reservationTime.getId());

        Reservation savedReservation = reservationDao.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    private void validateUniqueReservation(long themeId, LocalDate date, long timeId) {
        boolean exists = reservationDao.existsByThemeAndDateAndTime(themeId, date, timeId);
        if (exists) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    private ReservationTime getTime(long timeId) {
        return reservationTimeDao.findById(timeId)
                .orElseThrow(() -> new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND));
    }

    private Theme getTheme(long themeId) {
        return themeDao.findById(themeId)
                .orElseThrow(() -> new ThemeException(ThemeErrorCode.THEME_NOT_FOUND));
    }

    private Member getMember(LoginMember loginMember) {
        return memberDao.findById(loginMember.id())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXISTS));
    }

    public List<ReservationResponse> getReservations(LoginMember loginMember) {
        getMember(loginMember);
        List<Reservation> reservations = reservationDao.findAll();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getReservationsByName(String name, LoginMember loginMember) {
        getMember(loginMember);
        List<Reservation> reservations = reservationDao.findAllByName(name);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse update(LoginMember loginMember, long reservationId, ReservationRequest request) {
        Member member = getMember(loginMember);
        Reservation reservation = getReservation(reservationId);
        validateModifiable(reservation);

        ReservationTime reservationTime = getTime(request.timeId());
        Theme theme = getTheme(request.themeId());
        validateNotPastDateTime(request.date(), reservationTime, LocalDateTime.now(clock));
        validateUniqueReservationForUpdate(reservationId, theme, request.date(), reservationTime);

        Reservation updatedReservation = new Reservation(
                reservationId, member,
                request.date(), reservationTime, theme);
        reservationDao.update(updatedReservation);
        return ReservationResponse.from(updatedReservation);
    }

    private void validateNotPastDateTime(LocalDate date, ReservationTime time, LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time.getStartAt());
        if (reservationDateTime.isBefore(now)) {
            throw new ReservationException(ReservationErrorCode.PAST_DATE_NOT_ALLOWED);
        }
    }

    private void validateModifiable(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now(clock);

        if (reservation.isNotModifiableAt(now)) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_DEADLINE_PASSED);
        }
    }

    private void validateUniqueReservationForUpdate(long reservationId, Theme theme,
                                                    LocalDate date, ReservationTime reservationTime) {
        boolean exists = reservationDao.existsByThemeAndDateAndTimeAndIdNot(
                theme.getId(), date,
                reservationTime.getId(), reservationId);
        if (exists) {
            throw new ReservationException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    public void delete(long reservationId) {
        Reservation reservation = getReservation(reservationId);
        validateModifiable(reservation);
        reservationDao.delete(reservationId);
    }

    private Reservation getReservation(long reservationId) {
        return reservationDao.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }
}
