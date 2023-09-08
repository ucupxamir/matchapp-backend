package matchapp.repositories;

import matchapp.entities.Match;
import matchapp.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {

    Boolean existsByHomeAndAway(Team home, Team away);

}