package offline.simple.messenger.configure;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("[DEBUG] Interceptor preSend called. Command: " + accessor.getCommand());
        System.out.println("[DEBUG] Authorization = " + accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION));
        // STOMP CONNECT 명령일 때만 인증 검증 수행
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String username = validateTokenAndExtractUsername(accessor);
            UsernamePasswordAuthenticationToken principal =
                    new UsernamePasswordAuthenticationToken(username, null, null);

            accessor.setUser(principal);
            accessor.getSessionAttributes().put("principal", principal);
            System.out.println("[DEBUG] Principal set: " + principal.getName());
        }

        return message;
    }

    private String validateTokenAndExtractUsername(StompHeaderAccessor accessor) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String accessToken = extractBearerToken(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION));

        if (accessToken == null) {
            throw new IllegalArgumentException("Missing Authorization header.");
        }

        // 토큰 검증 및 사용자 이름 추출
        return jwtTokenProvider.getUsernameFromToken(accessToken);
    }

    private String extractBearerToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 이후의 문자열 반환
        }
        return null;
    }
}
