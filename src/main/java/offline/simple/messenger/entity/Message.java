package offline.simple.messenger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Message {
    private static final int MAX_CONTENT_LENGTH = 10000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메시지 보낸 사용자
    @ManyToOne
    private User sender;

    // 어떤 방에 속한 메시지인지
    @ManyToOne
    private ChatRoom chatRoom;

    // 메시지 내용
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 메시지 전송 시각
    private LocalDateTime createdAt;

    public static Message createMessage(ChatRoom chatRoom, User sender, String content) {
        validateContentLength(content);
        return Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 메시지 길이 검증
    private static void validateContentLength(String content) {
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("메시지 길이는 최대 " + MAX_CONTENT_LENGTH + "자를 초과할 수 없습니다.");
        }
    }
}
