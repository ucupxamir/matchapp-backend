package matchapp.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import matchapp.entities.Team;
import matchapp.entities.Tournament;
import matchapp.models.BaseResponse;
import matchapp.models.TeamRequest;
import matchapp.repositories.TeamRepository;
import matchapp.repositories.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void finish() {
        teamRepository.deleteAll();
        tournamentRepository.deleteAll();
    }

    @Test
    void testCreateSuccess() throws Exception {
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        TeamRequest request = new TeamRequest();
        request.setTournament(tournament.getId());
        request.setName("TEST");

        mockMvc.perform(
                post("/api/teams")
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
        TeamRequest request = new TeamRequest();
        request.setTournament(null);
        request.setName("");

        mockMvc.perform(
                post("/api/teams")
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
    void testCreateAlreadyExists() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM
        Team team = new Team();
        team.setId(UUID.randomUUID());
        team.setTournament(tournament);
        team.setName("TEST");
        teamRepository.save(team);

        TeamRequest request = new TeamRequest();
        request.setTournament(tournament.getId());
        request.setName("TEST");

        mockMvc.perform(
                post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isConflict()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Team already exists.", response.getMessage());
        });
    }

    @Test
    void testUpdateBadRequest() throws Exception {
        TeamRequest request = new TeamRequest();
        request.setTournament(null);
        request.setName("");

        mockMvc.perform(
                post("/api/teams/" + UUID.randomUUID())
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
    void testUpdateTeamNotFound() throws Exception {
        TeamRequest request = new TeamRequest();
        request.setTournament(UUID.randomUUID());
        request.setName("TEST");

        mockMvc.perform(
                post("/api/teams/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Team not found.", response.getMessage());
        });
    }

    @Test
    void testUpdateTournamentNotFound() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM
        Team teamData = new Team();
        teamData.setId(UUID.randomUUID());
        teamData.setTournament(tournament);
        teamData.setName("TEST");
        Team team = teamRepository.save(teamData);

        TeamRequest request = new TeamRequest();
        request.setTournament(UUID.randomUUID());
        request.setName("TEST");

        mockMvc.perform(
                post("/api/teams/" + team.getId())
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
    void testUpdateTeamAlreadyExists() throws Exception {
        // CREATE NEW TOURNAMENT
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        // CREATE NEW TEAM 1
        Team teamData1 = new Team();
        teamData1.setId(UUID.randomUUID());
        teamData1.setTournament(tournament);
        teamData1.setName("TEST A");
        Team team1 = teamRepository.save(teamData1);

        // CREATE NEW TEAM 2
        Team teamData2 = new Team();
        teamData2.setId(UUID.randomUUID());
        teamData2.setTournament(tournament);
        teamData2.setName("TEST B");
        teamRepository.save(teamData2);

        TeamRequest request = new TeamRequest();
        request.setTournament(tournament.getId());
        request.setName("TEST B");

        mockMvc.perform(
                post("/api/teams/" + team1.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isConflict()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Team already exists.", response.getMessage());
        });
    }

}