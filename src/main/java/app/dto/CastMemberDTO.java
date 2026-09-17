package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CastMemberDTO(
        @JsonProperty("id")
        int id,
        @JsonProperty("name")
        String name,
        @JsonProperty("character")
        String character
) {
}