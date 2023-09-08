package matchapp.services;

import matchapp.entities.Team;
import matchapp.entities.Tournament;
import matchapp.models.TeamRequest;
import matchapp.repositories.TeamRepository;
import matchapp.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    ValidationService validationService;

    @Transactional
    public void create(TeamRequest request) {
        validationService.validate(request);

        Tournament tournament = tournamentRepository.findById(request.getTournament())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found."));

        if (teamRepository.existsByTournamentAndName(tournament, request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team already exists.");
        }

        Team team = new Team();
        team.setTournament(tournament);
        team.setName(request.getName());

        teamRepository.save(team);
    }

    @Transactional
    public void update(UUID id, TeamRequest request) {
        validationService.validate(request);

        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found."));

        Tournament tournament = tournamentRepository.findById(request.getTournament())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found."));

        Optional<Team> existingTeam = teamRepository.findByTournamentAndName(tournament, request.getName());

        if (existingTeam.isPresent() && existingTeam.get().getId() != id) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team already exists.");
        }

        team.setTournament(tournament);
        team.setName(request.getName());

        teamRepository.save(team);
    }

}
