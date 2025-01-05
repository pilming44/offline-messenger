package offline.simple.messenger.e2e;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import offline.simple.messenger.e2e.environment.E2ESql;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static offline.simple.messenger.e2e.environment.MessengerSteps.*;

@DisplayName("인증 관련 E2E 테스트")
public class AuthE2ETest extends E2ESql {

    @DisplayName("회원가입 시 정상적으로 가입이 되고, 로그인 후 토큰을 발급받을 수 있다.")
    @Test
    void 회원가입_로그인() {
        // given
        String username = "userA";
        String password = "passA";
        String name = "테스트사용자A";

        // when : 회원가입
        ExtractableResponse<Response> signupResponse = 회원가입_요청(username, password, name);
        // then
        Assertions.assertThat(signupResponse.statusCode()).isEqualTo(200); // "회원가입 성공"

        // when : 로그인
        ExtractableResponse<Response> loginResponse = 로그인_요청(username, password);
        // then
        Assertions.assertThat(loginResponse.statusCode()).isEqualTo(200);

        // 발급받은 토큰 확인
        String token = 토큰추출(loginResponse);
        Assertions.assertThat(token).isNotBlank();
    }

    @DisplayName("로그아웃 요청 시 200을 반환한다(서버 내 별도 무효화 로직은 없음).")
    @Test
    void 로그아웃() {
        // given
        회원가입_요청("testUser", "testPassword", "최재현");
        String token = 토큰추출(로그인_요청("testUser", "testPassword"));

        // when
        ExtractableResponse<Response> logoutResponse = 로그아웃_요청(token);

        // then
        Assertions.assertThat(logoutResponse.statusCode()).isEqualTo(200);
    }
}
