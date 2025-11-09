package encore.server.domain.ticket.repository;

import encore.server.domain.ticket.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByNameContaining(String keyword);
    List<Actor> findAllByIdIn(List<Long> ids);

    @Query("""
      select a from Actor a
      where a.name = :name
        and (
             (a.birthYear = :birthYear)
             or (a.birthYear is null and :birthYear is null)
        )
    """)
    Optional<Actor> findByNameAndBirthYearNullable(String name, Integer birthYear);
}
