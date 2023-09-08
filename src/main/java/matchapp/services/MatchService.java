package matchapp.services;

import matchapp.entities.Match;
import matchapp.entities.Team;
import matchapp.entities.Tournament;
import matchapp.models.MatchRequest;
import matchapp.repositories.MatchRepository;
import matchapp.repositories.TeamRepository;
import matchapp.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatchService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void create(MatchRequest request) {
        validationService.validate(request);

        Tournament tournament = tournamentRepository.findById(request.getTournament())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found."));

        Team home = teamRepository.findById(request.getHome())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Home team not found."));

        Team away = teamRepository.findById(request.getAway())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Away team not found."));

        if (!tournament.getIsFull() && (matchRepository.existsByHomeAndAway(home, away) || matchRepository.existsByHomeAndAway(away, home))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Match already exists.");
        } else if (tournament.getIsFull() && matchRepository.existsByHomeAndAway(home, away)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Match already exists.");
        }

        if (request.getDateTime().isBefore(tournament.getStartDate().atStartOfDay())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match date and time must be after tournament start date.");
        } else if (request.getDateTime().isAfter(tournament.getEndDate().atTime(23,59,59))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Match date and time must be before tournament end date.");
        }

        Match match = new Match();
        match.setTournament(tournament);
        match.setDateTime(request.getDateTime());
        match.setHome(home);
        match.setAway(away);
        match.setHomeScore(request.getHomeScore());
        match.setAwayScore(request.getAwayScore());

        matchRepository.save(match);
    }

}
