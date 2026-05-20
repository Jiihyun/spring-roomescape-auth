package roomescape.reservationtime.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.ServiceTest;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;
import roomescape.reservation.dao.ReservationDao;
import roomescape.reservation.domain.Role;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.theme.dao.ThemeDao;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.reservationtime.dto.request.ReservationTimeRequest;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.exception.ThemeException;

class ReservationTimeServiceTest extends ServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private ReservationTimeDao reservationTimeDao;

    @Autowired
    private ThemeDao themeDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private Clock clock;

    @Test
    void 예약_시간을_생성할_수_있다() {
        // given
        LocalTime startAt = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(startAt);

        // when
        ReservationTimeResponse response = reservationTimeService.create(request);

        // then
        assertThat(response.startAt()).isEqualTo(startAt);
    }

    @Test
    void 이미_존재하는_예약_시간을_저장_시_예외를_반환한다() {
        // given
        LocalTime startAt = LocalTime.of(10, 0);
        saveReservationTime(startAt);
        ReservationTimeRequest request = new ReservationTimeRequest(startAt);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.create(request))
                .isInstanceOf(ReservationTimeException.class)
                .hasMessage(ReservationTimeErrorCode.RESERVATION_TIME_ALREADY_EXISTS.getMessage());
    }

    @Test
    void 테마_및_날짜에_따른_예약시간을_조회할_수_있다() {
        // given
        Theme theme = saveTheme("테마1");
        LocalDate date = LocalDate.now();

        ReservationTime reservedTime = saveReservationTime(LocalTime.of(10, 0));
        ReservationTime notReservedTime = saveReservationTime(LocalTime.of(11, 0));

        Member member = saveMember("러키");

        Reservation reservation = new Reservation(
                member,
                date,
                reservedTime,
                theme
        );
        reservationDao.save(reservation);

        // when
        List<AvailableReservationTimeResponse> responses =
                reservationTimeService.getReservationTimes(theme.getId(), date);

        // then
        assertThat(responses)
                .extracting(
                        AvailableReservationTimeResponse::id,
                        AvailableReservationTimeResponse::startAt,
                        AvailableReservationTimeResponse::reserved
                )
                .containsExactlyInAnyOrder(
                        tuple(reservedTime.getId(), reservedTime.getStartAt(), true),
                        tuple(notReservedTime.getId(), notReservedTime.getStartAt(), false)
                );
    }

    @Test
    void 예약시간_조회시_날짜가_오늘_이전이면_예외가_발생한다() {
        // given
        Theme theme = saveTheme("테마1");
        LocalDate invalidDate = LocalDate.now(clock).minusDays(1);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.getReservationTimes(theme.getId(), invalidDate))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.PAST_DATE_NOT_ALLOWED.getMessage());
    }

    @Test
    void 예약시간_조회시_테마가_존재하지_않으면_예외가_발생한다() {
        // given
        long invalidThemeId = 0L;
        LocalDate date = LocalDate.now().minusDays(1);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.getReservationTimes(invalidThemeId, date))
                .isInstanceOf(ThemeException.class)
                .hasMessage(ThemeErrorCode.THEME_NOT_FOUND.getMessage());
    }

    @Test
    void 예약시간을_삭제할_수_있다() {
        // given
        Theme theme = saveTheme("테마1");
        LocalDate date = LocalDate.now();
        LocalTime startAt = LocalTime.of(10, 0);

        ReservationTimeRequest request = new ReservationTimeRequest(startAt);
        ReservationTimeResponse response = reservationTimeService.create(request);

        int beforeSize = reservationTimeService.getReservationTimes(theme.getId(), date).size();

        // when
        reservationTimeService.delete(response.id());

        // then
        List<AvailableReservationTimeResponse> reservations = reservationTimeService.getReservationTimes(theme.getId(), date);
        assertAll(
                () -> assertThat(reservations).hasSize(beforeSize - 1),
                () -> assertThat(reservations)
                        .extracting(AvailableReservationTimeResponse::id)
                        .doesNotContain(response.id())
        );
    }

    @Test
    void 예약시간_삭제시_관련_예약이_존재하면_예외를_반환한다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(10, 0));
        Member member = saveMember("러키");
        Reservation reservation = new Reservation(
                member,
                LocalDate.of(2026, 5, 8),
                reservationTime,
                saveTheme("테마1")
        );
        reservationDao.save(reservation);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.delete(reservationTime.getId()))
                .isInstanceOf(ReservationTimeException.class)
                .hasMessage(ReservationTimeErrorCode.RESERVATION_TIME_HAS_RESERVATION.getMessage());
    }

    @Test
    void 삭제할_예약시간이_존재하지_않으면_예외를_반환한다() {
        // when & then
        assertThatThrownBy(() -> reservationTimeService.delete(0L))
                .isInstanceOf(ReservationTimeException.class)
                .hasMessage(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND.getMessage());
    }

    private Theme saveTheme(String name) {
        Theme theme = new Theme(name, "설명", "https://adsf.dsaf");
        return themeDao.save(theme);
    }

    private ReservationTime saveReservationTime(LocalTime startAt) {
        ReservationTime reservationTime = new ReservationTime(startAt);
        return reservationTimeDao.save(reservationTime);
    }

    private Member saveMember(String name) {
        Member member = new Member(null, name, name + "@email.com", "password", Role.USER);
        return memberDao.save(member);
    }
}
