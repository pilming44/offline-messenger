package offline.simple.messenger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "CHAT_ROOM")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대화방 이름
    private String name;

    // 기존: @ManyToMany로 선언했던 부분 제거
    // 새로: @OneToMany(mappedBy="chatRoom")로 ChatRoomMember와 연관관계를 맺는다
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    public void changeName(String newName) {
        this.name = newName;
    }

    // 사용자를 방에 추가할 때, ChatRoomMember 엔티티를 생성하여 List에 넣는다
    public void addUser(User user) {
        // ChatRoomMember를 새로 만들어서 자기 자신과 user를 연결
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(this)
                .user(user)
                .build();

        chatRoomMembers.add(member);
    }

    // 사용자를 방에서 제거
    public void removeUser(User user) {
        // user가 동일한 ChatRoomMember를 찾아서 제거
        chatRoomMembers.removeIf(member -> member.getUser().equals(user));
    }
}
