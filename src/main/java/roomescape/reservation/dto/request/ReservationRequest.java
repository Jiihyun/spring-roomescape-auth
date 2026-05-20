package roomescape.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public record ReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "날짜를 입력해 주세요.")
        LocalDate date,

        @NotNull(message = "시간을 선택해 주세요.")
        Long timeId,

        @NotNull(message = "테마를 선택해 주세요.")
        Long themeId
) {
    public Reservation toReservation(Member member, ReservationTime reservationTime, Theme theme, LocalDateTime dateTime) {
        return Reservation.createFutureReservation(member, date, reservationTime, theme, dateTime);
    }
}
