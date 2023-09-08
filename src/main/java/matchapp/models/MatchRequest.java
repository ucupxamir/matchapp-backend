package matchapp.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MatchRequest {

    @NotNull
    private UUID tournament;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private UUID home;

    @NotNull
    private UUID away;

    private Integer homeScore;

    private Integer awayScore;

}
