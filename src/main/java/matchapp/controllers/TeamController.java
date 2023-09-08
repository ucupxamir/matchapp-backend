package matchapp.controllers;

import matchapp.models.BaseResponse;
import matchapp.models.TeamRequest;
import matchapp.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> create(@RequestBody TeamRequest request) {
        teamService.create(request);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Team has been successfully created.")
                .build();
    }

    @PostMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> update(@PathVariable UUID id, @RequestBody TeamRequest request) {
        teamService.update(id, request);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Team has been successfully updated.")
                .build();
    }

}
