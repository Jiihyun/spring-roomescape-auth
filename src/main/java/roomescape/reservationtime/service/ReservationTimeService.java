package roomescape.reservationtime.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.theme.dao.ThemeDao;
import roomescape.reservationtime.dao.dto.ReservationTimeAvailability;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.request.ReservationTimeRequest;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.exception.ThemeException;

@Service
public class ReservationTimeService {

    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final ReservationDao reservationDao;
    private final Clock clock;


    public ReservationTimeService(ReservationTimeDao reservationTimeDao, ThemeDao themeDao, ReservationDao reservationDao, Clock clock) {
        this.reservationTimeDao = reservationTimeDao;
        this.themeDao = themeDao;
        this.reservationDao = reservationDao;
        this.clock = clock;
    }

    public ReservationTimeResponse create(ReservationTimeRequest request) {
        ReservationTime reservationTime = request.toReservationTime();
        validateUniqueTime(reservationTime.getStartAt());
        ReservationTime newReservationTime = reservationTimeDao.save(reservationTime);
        return ReservationTimeResponse.from(newReservationTime);
    }

    private void validateUniqueTime(LocalTime startAt) {
        boolean exists = reservationTimeDao.existsByStartAt(startAt);
        if (exists) {
            throw new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_ALREADY_EXISTS);
        }
    }

    public List<AvailableReservationTimeResponse> getReservationTimes(long themeId, LocalDate date) {
        validateTheme(themeId);
        validateDate(date);
        List<ReservationTimeAvailability> timeAvailabilities = reservationTimeDao.findAvailabilitiesByThemeIdAndDate(themeId, date);
        return timeAvailabilities.stream()
                .map(AvailableReservationTimeResponse::from)
                .toList();
    }

    private void validateTheme(long themeId) {
        boolean exists = themeDao.existsById(themeId);
        if (!exists) {
            throw new ThemeException(ThemeErrorCode.THEME_NOT_FOUND);
        }
    }

    private void validateDate(LocalDate date) {
        boolean exists = date.isBefore(LocalDate.now(clock));
        if (exists) {
            throw new ReservationException(ReservationErrorCode.PAST_DATE_NOT_ALLOWED);
        }
    }

    public void delete(long reservationTimeId) {
        validateReservationNotExistsBy(reservationTimeId);
        int affectedRows = reservationTimeDao.delete(reservationTimeId);

        if (affectedRows == 0) {
            throw new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND);
        }
    }

    private void validateReservationNotExistsBy(long reservationTimeId) {
        if (reservationDao.existsByReservationTime(reservationTimeId)) {
            throw new ReservationTimeException(ReservationTimeErrorCode.RESERVATION_TIME_HAS_RESERVATION);
        }
    }
}
