package matchapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Tournament tournament;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Team home;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Team away;

    private Integer homeScore;

    private Integer awayScore;

}