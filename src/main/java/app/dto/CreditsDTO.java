package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditsDTO(
        @JsonProperty("id")
        int id,
        @JsonProperty("cast")
        List<CastMemberDTO> cast,
        @JsonProperty("crew")
        List<CrewMemberDTO> crew
) {
}