package matchapp.services;

import matchapp.entities.Team;
import matchapp.entities.Tournament;
import matchapp.models.TournamentByIdResponse;
import matchapp.models.TournamentRequest;
import matchapp.repositories.TeamRepository;
import matchapp.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void create(TournamentRequest request) {
        validationService.validate(request);

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date.");
        }

        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setLocation(request.getLocation());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setIsFull(request.getIsFull());

        tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TournamentByIdResponse findById(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found."));

        List<Team> teams = teamRepository.findByTournament(tournament);

        TournamentByIdResponse response = new TournamentByIdResponse();
        response.setId(tournament.getId());
        response.setName(tournament.getName());
        response.setLocation(tournament.getLocation());
        response.setStartDate(tournament.getStartDate());
        response.setEndDate(tournament.getEndDate());
        response.setIsFull(tournament.getIsFull());
        response.setTeams(teams);

        return response;
    }

    @Transactional
    public void update(UUID id, TournamentRequest request) {
        validationService.validate(request);

        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found."));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date.");
        }

        tournament.setName(request.getName());
        tournament.setLocation(request.getLocation());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setIsFull(request.getIsFull());

        tournamentRepository.save(tournament);
    }

}
