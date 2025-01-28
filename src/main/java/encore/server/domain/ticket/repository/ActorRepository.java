package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByNameContaining(String keyword);
    Optional<Actor> findByActorImageUrl(String actorImageUrl);
}
