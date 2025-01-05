package offline.simple.messenger.e2e;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import offline.simple.messenger.e2e.environment.E2ESql;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static offline.simple.messenger.e2e.environment.MessengerSteps.*;

@DisplayName("메시지 관련 E2E 테스트")
public class MessageE2ETest extends E2ESql {

    @DisplayName("메시지를 전송하고, 해당 대화방의 메시지를 조회할 수 있다.")
    @Test
    void 메시지_전송_조회() {
        // given
        회원가입_요청("userX", "passX", "이름X");
        회원가입_요청("userY", "passY", "이름Y");

        String tokenX = 토큰추출(로그인_요청("userX", "passX"));
        String tokenY = 토큰추출(로그인_요청("userY", "passY"));

        // (가정) DB에서 userX = id 1, userY = id 2라고 치고, 두 명이 들어간 방 생성
        Long userXId = 1L;
        Long userYId = 2L;
        Long roomId = 대화방_생성_요청(tokenX, "메시지방", userXId, userYId)
                .jsonPath().getLong("id");

        // when : X가 메시지를 전송
        ExtractableResponse<Response> sendResponse = 메시지_전송_요청(tokenX, roomId, "안녕하세요. X입니다.");
        Assertions.assertThat(sendResponse.statusCode()).isEqualTo(200);

        // then : Y가 해당 대화방의 메시지 조회
        ExtractableResponse<Response> messageListResponse = 메시지_목록조회_요청(tokenY, roomId);
        Assertions.assertThat(messageListResponse.statusCode()).isEqualTo(200);

        // 메시지 내용 확인
        String content = messageListResponse.jsonPath().getString("[0].content");
        Assertions.assertThat(content).isEqualTo("안녕하세요. X입니다.");
    }
}
