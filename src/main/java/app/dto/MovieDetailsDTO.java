package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDetailsDTO(
        @JsonProperty("id")
        int id,
        @JsonProperty("title")
        String title,
        @JsonProperty("release_date")
        String releaseDate,
        @JsonProperty("vote_average")
        double voteAverage,
        @JsonProperty("popularity")
        double popularity,
        @JsonProperty("runtime")
        int runtime,
        @JsonProperty("genres")
        List<GenreDTO> genres
) {
}