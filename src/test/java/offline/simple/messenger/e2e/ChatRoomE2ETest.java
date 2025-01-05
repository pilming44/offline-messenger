package offline.simple.messenger.e2e;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import offline.simple.messenger.e2e.environment.E2ESql;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static offline.simple.messenger.e2e.environment.MessengerSteps.*;

@DisplayName("대화방 관련 E2E 테스트")
public class ChatRoomE2ETest extends E2ESql {

    @DisplayName("대화방을 생성하고, 생성된 대화방을 조회할 수 있다.")
    @Test
    void 대화방_생성_조회() {
        // given
        회원가입_요청("userA", "passA", "이름A");
        회원가입_요청("userB", "passB", "이름B");

        String tokenA = 토큰추출(로그인_요청("userA", "passA"));
        String tokenB = 토큰추출(로그인_요청("userB", "passB"));

        // 사용자 A, B의 id를 알아야 대화방 생성 시 넣을 수 있다고 가정
        // 실제로는 사용자 목록 조회 -> id 파싱 과정을 거치거나, 회원가입 API에서 반환된 id를 활용
        // 여기서는 예시로 1번, 2번이라고 가정
        Long userAId = 1L;
        Long userBId = 2L;

        // when : A가 대화방 생성
        ExtractableResponse<Response> createResponse = 대화방_생성_요청(tokenA, "테스트방", userAId, userBId);
        Assertions.assertThat(createResponse.statusCode()).isEqualTo(200);

        Long createdRoomId = createResponse.jsonPath().getLong("id");

        // then : A, B가 대화방 조회
        ExtractableResponse<Response> getRoomResponseA = 대화방_조회_요청(tokenA, createdRoomId);
        ExtractableResponse<Response> getRoomResponseB = 대화방_조회_요청(tokenB, createdRoomId);

        Assertions.assertThat(getRoomResponseA.statusCode()).isEqualTo(200);
        Assertions.assertThat(getRoomResponseB.statusCode()).isEqualTo(200);

        String roomName = getRoomResponseA.jsonPath().getString("name");
        Assertions.assertThat(roomName).isEqualTo("테스트방");
    }

    @DisplayName("대화방 나가기를 하면 해당 대화방의 사용자 목록에서 빠진다.")
    @Test
    void 대화방_나가기() {
        // given
        회원가입_요청("userA", "passA", "이름A");
        회원가입_요청("userB", "passB", "이름B");

        String tokenA = 토큰추출(로그인_요청("userA", "passA"));

        // (가정) 1번, 2번 유저를 동시에 대화방에 추가
        Long userAId = 1L;
        Long userBId = 2L;
        Long roomId = 대화방_생성_요청(tokenA, "테스트방2", userAId, userBId)
                .jsonPath().getLong("id");

        // when : userB가 대화방에서 나간다고 가정
        String tokenB = 토큰추출(로그인_요청("userB", "passB"));
        ExtractableResponse<Response> leaveResponse = 대화방_나가기_요청(tokenB, roomId, userBId);
        Assertions.assertThat(leaveResponse.statusCode()).isEqualTo(200);

        // then : 조회했을 때 userB가 빠졌는지 확인
        ExtractableResponse<Response> getRoomResponse = 대화방_조회_요청(tokenA, roomId);
        Assertions.assertThat(getRoomResponse.statusCode()).isEqualTo(200);

        // 방의 users 배열 중에 userB가 있는지 확인
        String allUsers = getRoomResponse.jsonPath().getString("users.toString()");
        Assertions.assertThat(allUsers).doesNotContain("userB");
    }
}
