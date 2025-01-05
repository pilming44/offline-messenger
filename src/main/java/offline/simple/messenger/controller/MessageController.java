package offline.simple.messenger.controller;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.MessageRequest;
import offline.simple.messenger.dto.MessageResponse;
import offline.simple.messenger.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 특정 대화방의 메시지 조회
    @GetMapping("/room/{roomId}")
    public List<MessageResponse> getMessages(@PathVariable("roomId") Long roomId) {
        return messageService.getMessages(roomId);
    }

    // 메시지 전송
    @PostMapping
    public MessageResponse sendMessage(Authentication authentication, @RequestBody MessageRequest request) {
        String senderUsername = (String) authentication.getPrincipal();
        return messageService.sendMessage(senderUsername, request);
    }
}
