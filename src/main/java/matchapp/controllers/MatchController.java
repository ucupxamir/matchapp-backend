package matchapp.controllers;

import matchapp.models.BaseResponse;
import matchapp.models.MatchRequest;
import matchapp.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> create(@RequestBody MatchRequest request) {
        matchService.create(request);
        return BaseResponse.<String>builder()
                .status("success")
                .message("Match has been successfully created.")
                .build();
    }

}
