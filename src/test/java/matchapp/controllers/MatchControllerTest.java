package matchapp.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import matchapp.entities.Match;
import matchapp.entities.Team;
import matchapp.entities.Tournament;
import matchapp.models.BaseResponse;
import matchapp.models.MatchRequest;
import matchapp.repositories.MatchRepository;
import matchapp.repositories.TeamRepository;
import matchapp.repositories.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void finish() {
        matchRepository.deleteAll();
        teamRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    void testCreateSuccess() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("success", response.getStatus());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testCreateBadRequest() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setTournament(null);
        request.setHome(null);
        request.setAway(null);
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(null);

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testCreateTournamentNotFound() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setTournament(UUID.randomUUID());
        request.setHome(UUID.randomUUID());
        request.setAway(UUID.randomUUID());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Tournament not found.", response.getMessage());
        });
    }

    @Test
    void testCreateHomeTeamNotFound() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(UUID.randomUUID());
        request.setAway(UUID.randomUUID());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Home team not found.", response.getMessage());
        });
    }

    @Test
    void testCreateAwayTeamNotFound() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(UUID.randomUUID());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Away team not found.", response.getMessage());
        });
    }

    @Test
    void testCreateHalfAlreadyExists() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(false);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        // CREATE NEW MATCH
        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setTournament(tournament);
        match.setHome(team1);
        match.setAway(team2);
        match.setDateTime(LocalDateTime.now().plusHours(2));
        matchRepository.save(match);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isConflict()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Match already exists.", response.getMessage());
        });
    }

    @Test
    void testCreateHalfSwapHomeAwayAlreadyExists() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(false);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        // CREATE NEW MATCH
        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setTournament(tournament);
        match.setHome(team2);
        match.setAway(team1);
        match.setDateTime(LocalDateTime.now().plusHours(2));
        matchRepository.save(match);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isConflict()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Match already exists.", response.getMessage());
        });
    }

    @Test
    void testCreateFullSuccess() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        // CREATE NEW MATCH
        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setTournament(tournament);
        match.setHome(team2);
        match.setAway(team1);
        match.setDateTime(LocalDateTime.now().plusHours(2));
        matchRepository.save(match);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("success", response.getStatus());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testCreateFullAlreadyExist() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        // CREATE NEW MATCH
        Match match1 = new Match();
        match1.setId(UUID.randomUUID());
        match1.setTournament(tournament);
        match1.setHome(team1);
        match1.setAway(team2);
        match1.setDateTime(LocalDateTime.now().plusHours(2));
        matchRepository.save(match1);

        // CREATE NEW MATCH
        Match match2 = new Match();
        match2.setId(UUID.randomUUID());
        match2.setTournament(tournament);
        match2.setHome(team2);
        match2.setAway(team1);
        match2.setDateTime(LocalDateTime.now().plusHours(2));
        matchRepository.save(match2);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isConflict()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Match already exists.", response.getMessage());
        });
    }

    @Test
    void testCreateBeforeStartDate() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().minusDays(1));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Match date and time must be after tournament start date.", response.getMessage());
        });
    }

    @Test
    void testCreateAfterEndDate() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(7));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEAM A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEAM B");
        Team team2 = teamRepository.save(teamData2);

        MatchRequest request = new MatchRequest();
        request.setTournament(tournament.getId());
        request.setHome(team1.getId());
        request.setAway(team2.getId());
        request.setHomeScore(null);
        request.setAwayScore(null);
        request.setDateTime(LocalDateTime.now().plusDays(8));

        mockMvc.perform(
                post("/api/matches")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Match date and time must be before tournament end date.", response.getMessage());
        });
    }

}