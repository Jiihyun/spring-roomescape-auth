package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservation.exception.ReservationErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.theme.domain.Theme;

public class Reservation {

    private static final int RESERVATION_CHANGE_DEADLINE_PASSED = 1;

    private Long id;
    private final Member member;
    private final Long storeId;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    public static Reservation createFutureReservation(Member member, LocalDate date,
                                                      long storeId, ReservationTime time, Theme theme, LocalDateTime now) {
        validateNotPastDateTime(date, time, now);
        return new Reservation(null, member, storeId, date, time, theme);
    }

    private static void validateNotPastDateTime(LocalDate date, ReservationTime time, LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time.getStartAt());
        if (reservationDateTime.isBefore(now)) {
            throw new RoomescapeException(ReservationErrorCode.PAST_DATE_NOT_ALLOWED);
        }
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme) {
        this(null, member, theme.getStoreId(), date, time, theme);
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme) {
        this(id, member, theme.getStoreId(), date, time, theme);
    }

    public Reservation(Long id, Member member, Long storeId, LocalDate date, ReservationTime time, Theme theme) {
        this.id = id;
        this.member = member;
        this.storeId = storeId;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public Reservation createWithId(long id) {
        return new Reservation(id, this.member, this.storeId, this.date, this.time, this.theme);
    }

    public boolean isNotModifiableAt(LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(
                date,
                time.getStartAt()
        );
        LocalDateTime cancelDeadline = reservationDateTime.minusDays(RESERVATION_CHANGE_DEADLINE_PASSED);
        return now.isAfter(cancelDeadline);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getStoreId() {
        return storeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Reservation reservation = (Reservation) object;
        if (id != null && reservation.id != null) {
            return Objects.equals(id, reservation.id);
        }
        return Objects.equals(member, reservation.member)
                && Objects.equals(storeId, reservation.storeId)
                && Objects.equals(date, reservation.date) && Objects.equals(time, reservation.time)
                && Objects.equals(theme, reservation.theme);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(member, storeId, date, time, theme);
    }
}
