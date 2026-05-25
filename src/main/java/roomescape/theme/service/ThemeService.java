package roomescape.theme.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.RoomescapeException;
import roomescape.reservation.dao.ReservationDao;
import roomescape.store.dao.StoreDao;
import roomescape.storetheme.dao.StoreThemeDao;
import roomescape.storetheme.domain.StoreTheme;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.request.ThemeRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;

@Service
public class ThemeService {

    private static final int POPULAR_THEME_PERIOD_DAYS = 7;
    private static final int BASE_DATE_EXCLUDED_DAYS = 1;

    private final ThemeDao themeDao;
    private final ReservationDao reservationDao;
    private final StoreDao storeDao;
    private final StoreThemeDao storeThemeDao;
    private final Clock clock;

    public ThemeService(ThemeDao themeDao, ReservationDao reservationDao, StoreDao storeDao,
                        StoreThemeDao storeThemeDao, Clock clock) {
        this.themeDao = themeDao;
        this.reservationDao = reservationDao;
        this.storeDao = storeDao;
        this.storeThemeDao = storeThemeDao;
        this.clock = clock;
    }

    public ThemeResponse create(ThemeRequest request) {
        validateStoreExists(request.storeId());
        Theme theme = themeDao.findByName(request.name())
                .orElseGet(() -> themeDao.save(request.toTheme()));
        validateUniqueStoreTheme(request.storeId(), theme.getId());
        storeThemeDao.save(new StoreTheme(request.storeId(), theme.getId()));
        return ThemeResponse.from(theme);
    }

    private void validateStoreExists(long storeId) {
        if (!storeDao.existsById(storeId)) {
            throw new RoomescapeException(ThemeErrorCode.STORE_NOT_FOUND);
        }
    }

    private void validateUniqueStoreTheme(long storeId, long themeId) {
        boolean exists = storeThemeDao.existsByStoreIdAndThemeId(storeId, themeId);
        if (exists) {
            throw new RoomescapeException(ThemeErrorCode.THEME_ALREADY_EXISTS);
        }
    }

    public List<ThemeResponse> getThemes() {
        return themeDao.findAll().stream()
                .map(themeWithStore -> ThemeResponse.from(themeWithStore.theme()))
                .toList();
    }

    public List<ThemeResponse> getThemeRankings() {
        LocalDate baseDate = LocalDate.now(clock);
        LocalDate startDate = baseDate.minusDays(POPULAR_THEME_PERIOD_DAYS);
        LocalDate endDate = baseDate.minusDays(BASE_DATE_EXCLUDED_DAYS);
        List<Theme> popularThemes = themeDao.findPopularThemesByPeriod(startDate, endDate);
        return popularThemes.stream()
                .map(ThemeResponse::from)
                .toList();
    }

    public void delete(long themeId) {
        validateReservationNotExistsBy(themeId);
        storeThemeDao.deleteByThemeId(themeId);
        int affectedRows = themeDao.delete(themeId);

        if (affectedRows == 0) {
            throw new RoomescapeException(ThemeErrorCode.THEME_NOT_FOUND);
        }
    }

    private void validateReservationNotExistsBy(long themeId) {
        if (reservationDao.existsByTheme(themeId)) {
            throw new RoomescapeException(ThemeErrorCode.THEME_HAS_RESERVATION);
        }
    }
}
