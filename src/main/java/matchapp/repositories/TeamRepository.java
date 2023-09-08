package matchapp.repositories;

import matchapp.entities.Team;
import matchapp.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findByTournament(Tournament tournament);

    Boolean existsByTournamentAndName(Tournament tournament, String name);

    Optional<Team> findByTournamentAndName(Tournament tournament, String name);

}
