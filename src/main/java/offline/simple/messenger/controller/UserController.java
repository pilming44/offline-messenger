package offline.simple.messenger.controller;

import lombok.RequiredArgsConstructor;
import offline.simple.messenger.dto.UserResponse;
import offline.simple.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String username = (String) authentication.getPrincipal();
        UserResponse userResponse = userService.getCurrentUser(username);
        return ResponseEntity.ok(userResponse);
    }
}
