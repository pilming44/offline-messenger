package offline.simple.messenger.service;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.ChatRoomRequest;
import offline.simple.messenger.dto.ChatRoomResponse;
import offline.simple.messenger.dto.UserResponse;
import offline.simple.messenger.entity.ChatRoom;
import offline.simple.messenger.entity.ChatRoomMember;
import offline.simple.messenger.entity.User;
import offline.simple.messenger.repository.ChatRoomMemberRepository;
import offline.simple.messenger.repository.ChatRoomRepository;
import offline.simple.messenger.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    public ChatRoomResponse createRoom(ChatRoomRequest request) {
        // 1. 방 엔티티 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getRoomName())
                .build();

        // 2. 참여할 사용자들
        List<User> users = userRepository.findAllById(Arrays.asList(request.getUserIds()));

        // 3. 각 사용자마다 ChatRoomMember 생성 (chatRoom.addUser(...) 내부에서)
        for (User user : users) {
            chatRoom.addUser(user);
        }

        // 4. 저장 (cascade = ALL 로 인해 chatRoomMembers도 같이 저장됨)
        chatRoomRepository.save(chatRoom);

        // 5. Response 변환
        return convertToResponse(chatRoom);
    }

    public ChatRoomResponse getRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대화방입니다."));
        return convertToResponse(chatRoom);
    }

    public List<ChatRoomResponse> getUsersRoom(Long userId) {
        List<ChatRoom> chatRooms = chatRoomMemberRepository.findChatRoomsByUserId(userId);
        return chatRooms.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<ChatRoomResponse> getAllRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<UserResponse> getAllUsersInRoom(Long roomId) {
        List<User> users = chatRoomMemberRepository.findByChatRoomId(roomId)
                .stream()
                .map(ChatRoomMember::getUser)
                .collect(Collectors.toList());

        return users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .build())
                .toList();
    }

    public ChatRoomResponse leaveRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 대화방입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        chatRoom.removeUser(user);

        chatRoomRepository.save(chatRoom);

        return convertToResponse(chatRoom);
    }

    private ChatRoomResponse convertToResponse(ChatRoom chatRoom) {
        // ChatRoomMember 리스트를 UserResponse로 변환
        List<UserResponse> userResponses = chatRoom.getChatRoomMembers().stream()
                .map(m -> {
                    User u = m.getUser();
                    return UserResponse.builder()
                            .id(u.getId())
                            .username(u.getUsername())
                            .name(u.getName())
                            .build();
                })
                .toList();

        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .users(userResponses)
                .build();
    }
}
