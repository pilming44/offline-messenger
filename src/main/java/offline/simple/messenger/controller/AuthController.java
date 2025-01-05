package offline.simple.messenger.controller;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.UserLoginRequest;
import offline.simple.messenger.dto.UserSignupRequest;
import offline.simple.messenger.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 실제로 JWT를 서버에서 무효화하려면, blacklist 등을 사용해야 함
        return ResponseEntity.ok("로그아웃 처리 완료");
    }
}
