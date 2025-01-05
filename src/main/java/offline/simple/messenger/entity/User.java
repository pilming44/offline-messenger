package offline.simple.messenger.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)  // 아이디(로그인용)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false) // 사용자 이름(닉네임 가능)
    private String name;

    // 비밀번호 등 중요한 정보를 변경해야 하는 경우 사용
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // 사용자 이름(닉네임) 변경
    public void changeName(String newName) {
        this.name = newName;
    }
}