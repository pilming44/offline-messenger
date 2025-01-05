package offline.simple.messenger.service;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.configure.JwtTokenProvider;
import offline.simple.messenger.dto.UserLoginRequest;
import offline.simple.messenger.dto.UserSignupRequest;
import offline.simple.messenger.entity.User;
import offline.simple.messenger.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원가입
    public void signup(UserSignupRequest request) {
        // username(아이디) 중복 확인
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        userRepository.save(user);
    }

    // 로그인
    public String login(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // JWT 토큰 반환
        return jwtTokenProvider.generateToken(user.getUsername());
    }
}
