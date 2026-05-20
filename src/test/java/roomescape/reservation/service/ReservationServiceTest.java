package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.ServiceTest;
import roomescape.auth.dto.LoginMember;
import roomescape.member.dao.MemberDao;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Role;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservationtime.dao.ReservationTimeDao;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.reservationtime.exception.ReservationTimeErrorCode;
import roomescape.reservationtime.exception.ReservationTimeException;
import roomescape.theme.dao.ThemeDao;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.exception.ThemeErrorCode;
import roomescape.theme.exception.ThemeException;

class ReservationServiceTest extends ServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeDao reservationTimeDao;

    @Autowired
    private ThemeDao themeDao;

    @Autowired
    private MemberDao memberDao;

    @Test
    void 예약을_생성할_수_있다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(13, 0));
        Theme theme = saveTheme("테마1", "로지와 러키의 방탈출", "https:fsof/ommff");
        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest request = createReservationRequest(reservationTime.getId(), theme.getId(), LocalDate.of(2026, 5, 8));

        // when
        ReservationResponse response = reservationService.create(loginMember, request);

        // then
        assertThat(response)
                .extracting(
                        reservationResponse -> reservationResponse.member().name(),
                        ReservationResponse::date,
                        reservationResponse -> reservationResponse.time().id()
                )
                .containsExactly(
                        loginMember.name(),
                        request.date(),
                        request.timeId()
                );
    }

    @Test
    void 예약_생성시_예약시간이_존재하지_않으면_예외가_발생한다() {
        // given
        Theme theme = saveTheme("테마1", "로지와 러키의 방탈출", "https:fsof/ommff");
        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
        ReservationRequest request = createReservationRequest(0L, theme.getId(), LocalDate.of(2026, 5, 8));

        // when & then
        assertThatThrownBy(() -> reservationService.create(loginMember, request))
                .isInstanceOf(ReservationTimeException.class)
                .hasMessage(ReservationTimeErrorCode.RESERVATION_TIME_NOT_FOUND.getMessage());
    }

    @Test
    void 예약_생성시_테마가_존재하지_않으면_예외가_발생한다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(13, 0));
        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
        ReservationRequest request = createReservationRequest(reservationTime.getId(), 0L, LocalDate.of(2026, 5, 8));

        // when & then
        assertThatThrownBy(() -> reservationService.create(loginMember, request))
                .isInstanceOf(ThemeException.class)
                .hasMessage(ThemeErrorCode.THEME_NOT_FOUND.getMessage());
    }

    @Test
    void 예약_생성시_동일한_조건의_예약이_이미_존재하면_예외가_발생한다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(13, 0));
        Theme theme = saveTheme("테마1", "로지와 러키의 방탈출", "https:fsof/ommff");
        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
        ReservationRequest request = createReservationRequest(reservationTime.getId(), theme.getId(), LocalDate.of(2026, 5, 8));
        reservationService.create(loginMember, request);

        // when & then
        assertThatThrownBy(() -> reservationService.create(loginMember, request))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.RESERVATION_ALREADY_EXISTS.getMessage());
    }

    @Test
    void 예약을_수정할_수_있다() {
        // given
        ReservationTime originalTime = saveReservationTime(LocalTime.of(10, 0));
        ReservationTime changedTime = saveReservationTime(LocalTime.of(20, 0));

        Theme originalTheme = saveTheme("방탈출1", "로지와 러키의 방탈출", "https:fsof/ommff");
        Theme changedTheme = saveTheme("방탈출2", "밤밤과 러로의 방탈출", "https:fsof/sdafjifdsmmff");

        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest createRequest = createReservationRequest(
                originalTime.getId(),
                originalTheme.getId(),
                LocalDate.of(2026, 5, 10)
        );
        ReservationResponse savedReservation = reservationService.create(loginMember, createRequest);

        ReservationRequest updateRequest = new ReservationRequest(
                LocalDate.of(2026, 5, 12),
                changedTime.getId(),
                changedTheme.getId()
        );

        // when
        ReservationResponse response = reservationService.update(loginMember, savedReservation.id(), updateRequest);

        // then
        assertAll(
                () -> assertThat(response)
                        .extracting(
                                ReservationResponse::id,
                                reservationResponse -> reservationResponse.member().name(),
                                ReservationResponse::date
                        )
                        .containsExactly(
                                savedReservation.id(),
                                loginMember.name(),
                                LocalDate.of(2026, 5, 12)
                        ),
                () -> assertThat(response.time())
                        .extracting(
                                ReservationTimeResponse::id,
                                ReservationTimeResponse::startAt
                        )
                        .containsExactly(
                                changedTime.getId(),
                                changedTime.getStartAt()
                        ),
                () -> assertThat(response.theme())
                        .extracting(
                                ThemeResponse::id,
                                ThemeResponse::name,
                                ThemeResponse::description,
                                ThemeResponse::thumbnail
                        )
                        .containsExactly(
                                changedTheme.getId(),
                                changedTheme.getName(),
                                changedTheme.getDescription(),
                                changedTheme.getThumbnail()
                        )
        );
    }

    @Test
    void 이미_예약된_날짜_시간_테마로는_예약을_수정할_수_없다() {
        // given
        ReservationTime alreadyReservedTime = saveReservationTime(LocalTime.of(20, 0));
        Theme alreadyReservedTheme = saveTheme("방탈출2", "밤밤과 러로의 방탈출", "https:fsof/sdafjifdsmmff");
        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest alreadyReservedRequest = new ReservationRequest(
                LocalDate.of(2026, 5, 12),
                alreadyReservedTime.getId(),
                alreadyReservedTheme.getId()
        );

        reservationService.create(loginMember, alreadyReservedRequest);

        ReservationTime originalTime = saveReservationTime(LocalTime.of(10, 0));
        Theme originalTheme = saveTheme("방탈출1", "로지와 러키의 방탈출", "https:fsof/ommff");
        ReservationRequest createRequest = createReservationRequest(
                originalTime.getId(),
                originalTheme.getId(),
                LocalDate.of(2026, 5, 10)
        );
        ReservationResponse savedReservation = reservationService.create(loginMember, createRequest);

        ReservationRequest updateRequest = new ReservationRequest(
                LocalDate.of(2026, 5, 12),
                alreadyReservedTime.getId(),
                alreadyReservedTheme.getId()
        );

        // when & then
        assertThatThrownBy(() -> reservationService.update(loginMember, savedReservation.id(), updateRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.RESERVATION_ALREADY_EXISTS.getMessage());
    }

    @Test
    void 현재_시간보다_이전_날짜로_예약을_수정하면_예외가_발생한다() {
        ReservationTime originalTime = saveReservationTime(LocalTime.of(11, 0));
        Theme originalTheme = saveTheme("방탈출1", "로지와 러키의 방탈출", "https:fsof/ommff");

        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest createRequest = new ReservationRequest(
                LocalDate.of(2026, 5, 25),
                originalTime.getId(),
                originalTheme.getId()
        );
        ReservationResponse savedReservation = reservationService.create(loginMember, createRequest);

        ReservationTime pastTime = saveReservationTime(LocalTime.of(10, 0));
        Theme updateTheme = saveTheme("방탈출2", "밤밤과 러로의 방탈출", "https:fsof/sdafjifdsmmff");

        ReservationRequest updateRequest = new ReservationRequest(
                LocalDate.of(2026, 5, 4),
                pastTime.getId(),
                updateTheme.getId()
        );

        // when & then
        assertThatThrownBy(() -> reservationService.update(loginMember, savedReservation.id(), updateRequest))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.PAST_DATE_NOT_ALLOWED.getMessage());
    }

    @Test
    void 예약을_삭제할_수_있다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(13, 0));
        Theme theme = saveTheme("테마1", "로지와 러키의 방탈출", "https:fsof/ommff");

        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest request = createReservationRequest(reservationTime.getId(), theme.getId(), LocalDate.of(2026, 5, 8));
        ReservationResponse response = reservationService.create(loginMember, request);

        int beforeSize = reservationService.getReservations(loginMember).size();

        // when
        reservationService.delete(response.id());

        // then
        List<ReservationResponse> reservations = reservationService.getReservations(loginMember);
        assertAll(
                () -> assertThat(reservations).hasSize(beforeSize - 1),
                () -> assertThat(reservations)
                        .extracting(ReservationResponse::id)
                        .doesNotContain(response.id())
        );
    }

    @Test
    void 삭제할_예약이_존재하지_않으면_예외를_반환한다() {
        // when & then
        assertThatThrownBy(() -> reservationService.delete(0L))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage());
    }

    @Test
    void 예약_취소_마감_기한이_지난_예약은_삭제할_수_없다() {
        // given
        ReservationTime reservationTime = saveReservationTime(LocalTime.of(13, 0));
        Theme theme = saveTheme("테마1", "로지와 러키의 방탈출", "https:fsof/ommff");

        Member member = saveMember("브라운", "brown@email.com", "password");
        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());

        ReservationRequest request = createReservationRequest(
                reservationTime.getId(),
                theme.getId(),
                LocalDate.of(2026, 5, 5)
        );
        ReservationResponse response = reservationService.create(loginMember, request);

        int beforeSize = reservationService.getReservations(loginMember).size();

        // when & then
        assertThatThrownBy(() -> reservationService.delete(response.id()))
                .isInstanceOf(ReservationException.class)
                .hasMessage(ReservationErrorCode.RESERVATION_CANCEL_DEADLINE_PASSED.getMessage());

        List<ReservationResponse> reservations = reservationService.getReservations(loginMember);
        assertAll(
                () -> assertThat(reservations).hasSize(beforeSize),
                () -> assertThat(reservations)
                        .extracting(ReservationResponse::id)
                        .contains(response.id())
        );
    }

    private ReservationTime saveReservationTime(LocalTime startAt) {
        ReservationTime reservationTime = new ReservationTime(startAt);
        return reservationTimeDao.save(reservationTime);
    }

    private Theme saveTheme(String name, String description, String thumbnail) {
        Theme theme = new Theme(name, description, thumbnail);
        return themeDao.save(theme);
    }

    private Member saveMember(String name, String email, String password) {
        Member member = new Member(null, name, email, password, Role.USER);
        return memberDao.save(member);
    }

    private ReservationRequest createReservationRequest(long timeId, long themeId, LocalDate localDate) {
        return new ReservationRequest(
                localDate,
                timeId,
                themeId
        );
    }
}
