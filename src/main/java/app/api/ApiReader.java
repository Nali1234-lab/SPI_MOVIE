package app.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiReader {
    private final String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    public ApiReader(String apiKey) {
        this.apiKey = apiKey;
    }

    // Henter rå JSON fra en fuld URL (uden api_key - den tilføjes her)
    public String readAPI(String fullUrl) {
        try {
            String separator = fullUrl.contains("?") ? "&" : "?";
            String buildUrl = fullUrl + separator + "api_key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(buildUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("GET request failed. Status code: " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // Generisk konvertering - kan bruges til alle vores DTO-typer
    public <T> T convertFromJson(String json, Class<T> dtoClass) {
        try {
            return objectMapper.readValue(json, dtoClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Praktiske hjælpemetoder til de tre endpoints, I skal bruge:

    public String buildDiscoverUrl(int page, String fromDate, String toDate) {
        return BASE_URL + "/discover/movie"
                + "?with_origin_country=DK"
                + "&primary_release_date.gte=" + fromDate
                + "&primary_release_date.lte=" + toDate
                + "&sort_by=primary_release_date.desc"
                + "&page=" + page;
    }

    public String buildMovieDetailsUrl(int movieId) {
        return BASE_URL + "/movie/" + movieId;
    }

    public String buildCreditsUrl(int movieId) {
        return BASE_URL + "/movie/" + movieId + "/credits";
    }
}