package offline.simple.messenger.e2e.environment;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class MessengerSteps {

    // 회원가입 요청
    public static ExtractableResponse<Response> 회원가입_요청(String username, String password, String name) {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        param.put("password", password);
        param.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/auth/signup")
                .then().log().all()
                .extract();
    }

    // 로그인 요청 -> JWT 토큰 받기
    public static ExtractableResponse<Response> 로그인_요청(String username, String password) {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        param.put("password", password);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/auth/login")
                .then().log().all()
                .extract();
    }

    // 로그아웃 요청 (서버에서 특별한 동작이 없다면 단순 200 응답)
    public static ExtractableResponse<Response> 로그아웃_요청(String token) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/auth/logout")
                .then().log().all()
                .extract();
    }

    // 사용자 목록 조회
    public static ExtractableResponse<Response> 사용자목록_조회_요청(String token) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/users")
                .then().log().all()
                .extract();
    }

    // 대화방 생성
    public static ExtractableResponse<Response> 대화방_생성_요청(String token, String roomName, Long... userIds) {
        Map<String, Object> param = new HashMap<>();
        param.put("roomName", roomName);
        param.put("userIds", userIds);

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/rooms")
                .then().log().all()
                .extract();
    }

    // 특정 대화방 조회
    public static ExtractableResponse<Response> 대화방_조회_요청(String token, Long roomId) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/rooms/{roomId}", roomId)
                .then().log().all()
                .extract();
    }

    // 모든 대화방 조회
    public static ExtractableResponse<Response> 대화방_목록조회_요청(String token) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/rooms")
                .then().log().all()
                .extract();
    }

    // 대화방 나가기
    public static ExtractableResponse<Response> 대화방_나가기_요청(String token, Long roomId, Long userId) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/rooms/{roomId}/users/{userId}", roomId, userId)
                .then().log().all()
                .extract();
    }

    // 메시지 전송
    public static ExtractableResponse<Response> 메시지_전송_요청(String token, Long chatRoomId, String content) {
        Map<String, Object> param = new HashMap<>();
        param.put("chatRoomId", chatRoomId);
        param.put("content", content);

        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().post("/messages")
                .then().log().all()
                .extract();
    }

    // 특정 대화방의 메시지 조회
    public static ExtractableResponse<Response> 메시지_목록조회_요청(String token, Long roomId) {
        return RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/messages/room/{roomId}", roomId)
                .then().log().all()
                .extract();
    }

    // 편의 메서드 (JWT 토큰 추출용)
    public static String 토큰추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getString("token"); // 예: 로그인 응답에서 단순 문자열(JWT)로 토큰이 넘어온다고 가정
    }
}