package offline.simple.messenger.controller;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.ChatRoomRequest;
import offline.simple.messenger.dto.ChatRoomResponse;
import offline.simple.messenger.dto.UserResponse;
import offline.simple.messenger.service.ChatRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 대화방 생성
    @PostMapping
    public ChatRoomResponse createRoom(@RequestBody ChatRoomRequest request) {
        return chatRoomService.createRoom(request);
    }

    // 특정 대화방 조회
    @GetMapping("/{roomId}")
    public ChatRoomResponse getRoom(@PathVariable("roomId") Long roomId) {
        return chatRoomService.getRoom(roomId);
    }

    // 모든 대화방 조회
    @GetMapping
    public List<ChatRoomResponse> getAllRooms() {
        return chatRoomService.getAllRooms();
    }

    // 사용자 대화방 조회
    @GetMapping("/users/{userId}")
    public List<ChatRoomResponse> getUsersRoom (@PathVariable("userId") Long userId) {
        return chatRoomService.getUsersRoom(userId);
    }

    // 대화방 사용자 조회
    @GetMapping("/{roomId}/users")
    public List<UserResponse> getAllUsersInRoom (@PathVariable("roomId") Long roomId) {
        return chatRoomService.getAllUsersInRoom(roomId);
    }

    // 대화방 나가기
    @DeleteMapping("/{roomId}/users/{userId}")
    public ChatRoomResponse leaveRoom(@PathVariable("roomId") Long roomId, @PathVariable("userId") Long userId) {
        return chatRoomService.leaveRoom(roomId, userId);
    }
}
