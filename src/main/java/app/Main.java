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


/*package app;

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
}*/
package app;

import app.config.HibernateConfig;
import app.dao.*;
import app.entities.*;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);

        // Krav 2: "We would like to be able to see a list of all movies pulled from the database."
        List<Movie> allMovies = movieDAO.findAll();
        System.out.println("Antal film i alt: " + allMovies.size());
        //allMovies.stream().limit(5).forEach(m -> System.out.println(m.getTitle() + " (" + m.getReleaseDate() + ")"));

        // Krav 3: "Each movie has a list of actors and a director. We would like to be able to see
        // a list of all actors and directors as well that have been part of those movies."
        List<Actor> allActors = actorDAO.findAll();
        List<Director> allDirectors = directorDAO.findAll();
        System.out.println("Antal skuespillere: " + allActors.size());
        System.out.println("Antal instruktører: " + allDirectors.size());

        // Krav 4: "Each movie has a list of genres. We would like to be able to see a list of all
        // genres as well. Also be able to list all movies within a particular genre."
        List<Genre> allGenres = genreDAO.findAll();
        System.out.println("Genrer: " + allGenres.stream().map(Genre::getName).toList());
        List<Movie> dramaMovies = genreDAO.findMoviesByGenre("Drama");
        System.out.println("Antal dramafilm: " + dramaMovies.size());

        // Krav 6: "We would like to be able to search for a movie by title. The search should be
        // case insensitive and should return all movies that contain the search string in the title."
        List<Movie> searchResult = movieDAO.searchByTitle("kærlighed");
        System.out.println("Film der matcher 'kærlighed': " + searchResult.size());

        // Krav 7: "We would like to be able to get the total average rating of all movies in the
        // database, the top-10 lowest and highest rated movies, and the top-10 most popular movies."
        System.out.println("Gennemsnitsrating: " + movieDAO.getAverageRating());
        System.out.println("Top-10 højeste rating:");
        movieDAO.getTop10HighestRated().forEach(m -> System.out.println(" - " + m.getTitle() + " (" + m.getVoteAverage() + ")"));
        System.out.println("Top-10 laveste rating:");
        movieDAO.getTop10LowestRated().forEach(m -> System.out.println(" - " + m.getTitle() + " (" + m.getVoteAverage() + ")"));
        System.out.println("Top-10 mest populære:");
        movieDAO.getTop10MostPopular().forEach(m -> System.out.println(" - " + m.getTitle() + " (" + m.getPopularity() + ")"));

        // Krav 5: "In case you want to add a new movie to the database, you should be able to do
        // that as well. You should also be able to update and delete movies from the database.
        // Not necessarily all fields, but at least the title and the release date."
        Movie firstMovie = allMovies.get(0);
        String originalTitle = firstMovie.getTitle();
        firstMovie.setTitle(originalTitle + " (opdateret)");
        movieDAO.update(firstMovie);
        System.out.println("Opdateret titel: " + movieDAO.findById(firstMovie.getId()).get().getTitle());

        emf.close();
    }
}