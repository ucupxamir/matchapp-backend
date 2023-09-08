package matchapp.controllers;

import matchapp.entities.Tournament;
import matchapp.models.BaseResponse;
import matchapp.models.TournamentByIdResponse;
import matchapp.models.TournamentRequest;
import matchapp.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> create(@RequestBody TournamentRequest request) {
        tournamentService.create(request);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Tournament has been successfully created.")
                .build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<List<Tournament>> findAll() {
        List<Tournament> tournaments = tournamentService.findAll();
        return BaseResponse.<List<Tournament>>builder()
                .status("success")
                .data(tournaments)
                .build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<TournamentByIdResponse> findById(@PathVariable UUID id) {
        TournamentByIdResponse response = tournamentService.findById(id);
        return BaseResponse.<TournamentByIdResponse>builder()
                .status("success")
                .data(response)
                .build();
    }

    @PostMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> update(@PathVariable UUID id, @RequestBody TournamentRequest request) {
        tournamentService.update(id, request);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Tournament has been successfully updated.")
                .build();
    }

}
