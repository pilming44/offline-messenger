package offline.simple.messenger.repository;

import offline.simple.messenger.entity.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @EntityGraph(attributePaths = {"chatRoomMembers", "chatRoomMembers.user"})
    Optional<ChatRoom> findWithMembersById(Long id);
}
