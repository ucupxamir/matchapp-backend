package matchapp.models;

import lombok.Getter;
import lombok.Setter;
import matchapp.entities.Team;
import matchapp.entities.Tournament;

import java.util.List;

@Getter
@Setter
public class TournamentByIdResponse extends Tournament {

    List<Team> teams;

}
