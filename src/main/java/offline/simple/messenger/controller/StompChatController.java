package offline.simple.messenger.controller;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.MessageRequest;
import offline.simple.messenger.dto.MessageResponse;
import offline.simple.messenger.service.MessageService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(Message<?> message, MessageRequest request) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal principal = (Principal) accessor.getSessionAttributes().get("principal");
        System.out.println("authentication from method argument: " + principal);
        System.out.println("authentication from SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
        if (principal == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // WebSocket 연결 시 HandshakeHandler에서 설정한 username
        String senderUsername = principal.getName();

        // DB 저장
        MessageResponse saved = messageService.sendMessage(senderUsername, request);

        // 메시지 전송
        simpMessagingTemplate.convertAndSend("/topic/room/" + request.getChatRoomId(), saved);
    }
}
