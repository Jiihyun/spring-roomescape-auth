package roomescape.member.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import roomescape.AcceptanceTest;

public class MemberControllerTest extends AcceptanceTest {

    @Test
    void 회원을_생성한다() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("email", "brown@email.com");
        params.put("password", "password123");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("브라운"))
                .body("email", is("brown@email.com"));
    }
}
