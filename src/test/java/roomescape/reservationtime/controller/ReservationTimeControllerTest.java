package roomescape.reservationtime.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import roomescape.AcceptanceTest;
import roomescape.auth.dto.LoginRequest;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;

public class ReservationTimeControllerTest extends AcceptanceTest {

    @Test
    void 시간을_조회한다() {
        long reservationTimeId = createTime("22:00");
        long themeId = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");

        long memberId = createMember("러키", "lucky@email.com", "password123");
        String sessionId = login("lucky@email.com", "password123");

        LocalDate date = LocalDate.now();
        createReservation(sessionId, memberId, date, reservationTimeId, themeId);

        List<AvailableReservationTimeResponse> responses = RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().get("/times?themeId=" + themeId + "&date=" + date)
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", AvailableReservationTimeResponse.class);

        assertThat(responses)
                .extracting("startAt", "reserved")
                .contains(
                        tuple(LocalTime.of(22, 0), true)
                );
    }

    private long createTime(String startAt) {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", startAt);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private long createTheme(String name, String description, String thumbnail) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("description", description);
        params.put("thumbnail", thumbnail);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private long createMember(String name, String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private String login(String email, String password) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest(email, password))
                .when()
                .post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");
    }

    private void createReservation(String sessionId, long memberId, LocalDate date, long timeId, long themeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date.toString());
        params.put("memberId", memberId);
        params.put("timeId", timeId);
        params.put("themeId", themeId);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", notNullValue());
    }
}
