package offline.simple.messenger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequest {
    private String roomName;
    private Long[] userIds; // 방에 참여시킬 사용자 ID 목록
}
