package matchapp.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TeamRequest {

    @NotNull
    private UUID tournament;

    @NotBlank
    private String name;

}
