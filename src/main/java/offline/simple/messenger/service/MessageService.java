package offline.simple.messenger.service;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.MessageRequest;
import offline.simple.messenger.dto.MessageResponse;
import offline.simple.messenger.entity.ChatRoom;
import offline.simple.messenger.entity.Message;
import offline.simple.messenger.entity.User;
import offline.simple.messenger.repository.ChatRoomRepository;
import offline.simple.messenger.repository.MessageRepository;
import offline.simple.messenger.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 특정 대화방의 메시지 목록 조회
    public List<MessageResponse> getMessages(Long chatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        return messages.stream().map(this::convertToResponse).toList();
    }

    // 메시지 전송
    public MessageResponse sendMessage(String senderUsername, MessageRequest request) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomRepository.findWithMembersById(request.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("대화방을 찾을 수 없습니다."));
        boolean isUserInRoom = chatRoom.getChatRoomMembers().stream()
                .anyMatch(member -> member.getUser().equals(sender));
        if (!isUserInRoom) {
            throw new RuntimeException("해당 사용자에게 권한이 없습니다.");
        }

        // 엔티티에서 메시지 생성 및 검증
        Message message = Message.createMessage(chatRoom, sender, request.getContent());

        Message saved = messageRepository.save(message);
        return convertToResponse(saved);
    }

    // Message 엔티티 -> MessageResponse 변환
    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
