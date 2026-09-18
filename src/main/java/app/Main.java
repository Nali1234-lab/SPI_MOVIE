/*package app;

import app.api.ApiReader;
import app.config.HibernateConfig;
import app.dao.*;
import app.service.MovieService;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        String apiKey = Utils.getPropertyValue("TMDB_API_KEY", "config.properties");
        ApiReader apiReader = new ApiReader(apiKey);

        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);

        MovieService movieService = new MovieService(apiReader, movieDAO, genreDAO, actorDAO, directorDAO);

        Instant start = Instant.now();

        movieService.fetchAndSaveAllDanishMoviesSequential("2021-09-17", "2026-09-17");

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Sekventiel hentning tog: " + duration.toMinutes() + " min " + (duration.toSeconds() % 60) + " sek");

        emf.close();
    }
}*/


package app;

import app.api.ApiReader;
import app.config.HibernateConfig;
import app.dao.*;
import app.service.MovieServiceThreaded;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        String apiKey = Utils.getPropertyValue("TMDB_API_KEY", "config.properties");
        ApiReader apiReader = new ApiReader(apiKey);

        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);

        MovieServiceThreaded movieServiceThreaded =
                new MovieServiceThreaded(apiReader, movieDAO, genreDAO, actorDAO, directorDAO);

        Instant start = Instant.now();

        movieServiceThreaded.fetchAndSaveAllDanishMoviesParallel("2021-09-17", "2026-09-17", 10);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Parallel hentning (10 tråde) tog: " + duration.toMinutes() + " min " + (duration.toSeconds() % 60) + " sek");

        emf.close();
    }
}