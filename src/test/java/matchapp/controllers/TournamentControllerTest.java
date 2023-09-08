package matchapp.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import matchapp.entities.Tournament;
import matchapp.models.BaseResponse;
import matchapp.models.TournamentByIdResponse;
import matchapp.models.TournamentRequest;
import matchapp.repositories.TournamentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void finish() {
        tournamentRepository.deleteAll();
    }

    @Test
    void testCreateSuccess() throws Exception {
        TournamentRequest request = new TournamentRequest();
        request.setName("TEST");
        request.setLocation("TEST");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setIsFull(true);

        mockMvc.perform(
                post("/api/tournaments")
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
        TournamentRequest request = new TournamentRequest();
        request.setName("");
        request.setLocation("");
        request.setStartDate(null);
        request.setEndDate(null);
        request.setIsFull(null);

        mockMvc.perform(
                post("/api/tournaments")
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
    void testCreateStartDateAfterEndDate() throws Exception {
        TournamentRequest request = new TournamentRequest();
        request.setName("TEST");
        request.setLocation("TEST");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().minusDays(1));
        request.setIsFull(true);

        mockMvc.perform(
                post("/api/tournaments")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Start date must be before end date.", response.getMessage());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testFindAllSuccess() throws Exception {
        for (int i = 0; i < 5; i++) {
            testCreateSuccess();
        }

        mockMvc.perform(
                get("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andExpectAll(result -> {
            BaseResponse<List<Tournament>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("success", response.getStatus());
            assertEquals(5,response.getData().size());
        });
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        mockMvc.perform(
                get("/api/tournaments/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<List<Tournament>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testFindByIdSuccess() throws Exception {
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        mockMvc.perform(
                get("/api/tournaments/" + tournament.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            BaseResponse<TournamentByIdResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("success", response.getStatus());
            assertEquals(tournament.getName(), response.getData().getName());
            assertEquals(tournament.getLocation(), response.getData().getLocation());
            assertEquals(tournament.getStartDate(), response.getData().getStartDate());
            assertEquals(tournament.getEndDate(), response.getData().getEndDate());
            assertEquals(0, response.getData().getTeams().size());
        });
    }

    @Test
    void testUpdateBadRequest() throws Exception {
        TournamentRequest request = new TournamentRequest();
        request.setName("");
        request.setLocation("");
        request.setStartDate(null);
        request.setEndDate(null);
        request.setIsFull(null);

        mockMvc.perform(
                post("/api/tournaments/" + UUID.randomUUID())
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
    void testUpdateNotFound() throws Exception {
        TournamentRequest request = new TournamentRequest();
        request.setName("TEST");
        request.setLocation("TEST");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setIsFull(true);

        mockMvc.perform(
                post("/api/tournaments/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testUpdateStartDateAfterEndDate() throws Exception {
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        TournamentRequest request = new TournamentRequest();
        request.setName("TEST");
        request.setLocation("TEST");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().minusDays(7));
        request.setIsFull(true);

        mockMvc.perform(
                post("/api/tournaments/" + tournament.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            BaseResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("error", response.getStatus());
            assertEquals("Start date must be before end date.", response.getMessage());
            assertNotNull(response.getMessage());
        });
    }

    @Test
    void testUpdateSuccess() throws Exception {
        Tournament tournamentData = new Tournament();
        tournamentData.setId(UUID.randomUUID());
        tournamentData.setName("TEST");
        tournamentData.setLocation("TEST");
        tournamentData.setStartDate(LocalDate.now());
        tournamentData.setEndDate(LocalDate.now().plusDays(1));
        tournamentData.setIsFull(true);
        Tournament tournament = tournamentRepository.save(tournamentData);

        TournamentRequest request = new TournamentRequest();
        request.setName("TEST");
        request.setLocation("TEST");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));
        request.setIsFull(false);

        mockMvc.perform(
                post("/api/tournaments/" + tournament.getId())
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

}