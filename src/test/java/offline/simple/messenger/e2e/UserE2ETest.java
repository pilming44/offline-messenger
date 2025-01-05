package offline.simple.messenger.e2e;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import offline.simple.messenger.e2e.environment.E2ESql;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static offline.simple.messenger.e2e.environment.MessengerSteps.*;

@DisplayName("사용자 관련 E2E 테스트")
public class UserE2ETest extends E2ESql {

    @DisplayName("사용자 목록 조회 시 가입된 사용자 리스트를 받아온다.")
    @Test
    void 사용자목록_조회() {
        // given
        회원가입_요청("user1", "pass1", "사용자1");
        회원가입_요청("user2", "pass2", "사용자2");

        // 로그인
        String token = 토큰추출(로그인_요청("user1", "pass1"));

        // when
        ExtractableResponse<Response> listResponse = 사용자목록_조회_요청(token);

        // then
        Assertions.assertThat(listResponse.statusCode()).isEqualTo(200);

        // 예: 응답이 [{"id":1,"username":"user1","name":"사용자1"}, ... ] 형태라고 가정
        String firstUserName = listResponse.jsonPath().getString("[0].username");
        Assertions.assertThat(firstUserName).isEqualTo("user1");
    }
}
