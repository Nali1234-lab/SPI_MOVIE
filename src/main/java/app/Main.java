package app;

import app.api.ApiReader;
import app.config.HibernateConfig;
import app.dto.MovieResultDTO;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        // 1) Trigger Hibernate til at oprette tabellerne i movie_sp1
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("Hibernate er sat op, EntityManagerFactory oprettet.");

        // 2) Test TMDb-kaldet
        String apiKey = Utils.getPropertyValue("TMDB_API_KEY", "config.properties");
        ApiReader reader = new ApiReader(apiKey);

        String url = reader.buildDiscoverUrl(1, "2021-09-17", "2026-09-17");
        String json = reader.readAPI(url);
        MovieResultDTO result = reader.convertFromJson(json, MovieResultDTO.class);

        System.out.println("Total resultater: " + result.totalResults());
        System.out.println("Total sider: " + result.totalPages());
        result.results().forEach(movie ->
                System.out.println(movie.title() + " (" + movie.releaseDate() + ")")
        );

        emf.close();
    }
}
