package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import roomescape.AcceptanceTest;
import roomescape.auth.dto.LoginRequest;

public class ReservationControllerTest extends AcceptanceTest {

    @Test
    void 예약을_생성한다() {
        long timeId = createTime("10:00");
        long themeId = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2026-05-05");
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
                .body("id", notNullValue())
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    void 같은_날짜_및_시간이더라도_테마가_다르면_예약_가능하다() {
        long timeId = createTime("10:00");
        long themeId1 = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long themeId2 = createTheme("방탈출2", "다함께 탈출해요 방탈출2.", "https://asdfsdf.sdfssdafdasf");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "date", "2026-05-05",
                        "memberId", memberId,
                        "timeId", timeId,
                        "themeId", themeId1
                ))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("member.name", is("브라운"));

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "date", "2026-05-05",
                        "memberId", memberId,
                        "timeId", timeId,
                        "themeId", themeId2
                ))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("member.name", is("브라운"));
    }

    @Test
    void 예약을_조회한다() {
        long timeId = createTime("10:00");
        long themeId = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");

        createReservation(sessionId, memberId, "2026-05-05", timeId, themeId);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("member.name", hasItem("브라운"));
    }

    @Test
    void 이름에_따른_예약들을_조회할_수_있다() {
        long time10Id = createTime("10:00");
        long time11Id = createTime("11:00");
        long themeId = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");
        createReservation(sessionId, memberId, "2026-05-10", time10Id, themeId);

        // 다른 유저로 예약 생성
        long member2Id = createMember("조이", "joy@email.com", "password123");
        createReservation(sessionId, member2Id, "2026-05-10", time11Id, themeId);

        RestAssured.given().log().all()
                .queryParam("name", "브라운")
                .cookie("JSESSIONID", sessionId)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("member.name", hasItem("브라운"))
                .body("member.name", not(hasItem("조이")));
    }

    @Test
    void 예약을_수정한다() {
        long timeId = createTime("10:00");
        long themeId = createTheme("방탈출1", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");
        long reservationId = createReservation(sessionId, memberId, "2026-05-10", timeId, themeId);

        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("date", "2026-05-10");
        updateParams.put("memberId", memberId);
        updateParams.put("timeId", timeId);
        updateParams.put("themeId", themeId);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(updateParams)
                .when().put("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(200)
                .body("member.name", is("브라운"));
    }

    @Test
    void 예약을_삭제한다() {
        long timeId = createTime("10:00");
        long themeId = createTheme("방탈출11", "다함께 탈출해요 방탈출.", "https://asdfsdf.sdfs");
        long memberId = createMember("브라운", "brown@email.com", "password123");

        String sessionId = login("brown@email.com", "password123");
        long reservationId = createReservation(sessionId, memberId, "2026-05-06", timeId, themeId);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().delete("/reservations/" + reservationId)
                .then().log().all()
                .statusCode(204);
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

    private long createReservation(String sessionId, long memberId, String date, long timeId, long themeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("memberId", memberId);
        params.put("timeId", timeId);
        params.put("themeId", themeId);

        return RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .jsonPath()
                .getLong("id");
    }
}
