package app.service;

import app.api.ApiReader;
import app.dao.*;
import app.dto.*;
import app.entities.Movie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.dao.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest extends BaseTest {

    private static MovieDAO movieDAO;
    private static GenreDAO genreDAO;
    private static ActorDAO actorDAO;
    private static DirectorDAO directorDAO;

    private ApiReader mockApiReader;
    private MovieService movieService;

    @BeforeAll
    static void initDAOs() {
        movieDAO = new MovieDAO(emf);
        genreDAO = new GenreDAO(emf);
        actorDAO = new ActorDAO(emf);
        directorDAO = new DirectorDAO(emf);
    }

    @BeforeEach
    void setUp() {
        // Mock ApiReader — den rører aldrig netværket
        mockApiReader = mock(ApiReader.class);
        movieService = new MovieService(mockApiReader, movieDAO, genreDAO, actorDAO, directorDAO);
    }

    @Test
    void fetchAndSave_shouldSaveMovieFromApi() {
        // Arrange — fake API-svar
        MovieResultDTO discoverResult = new MovieResultDTO(
                1,
                List.of(new MovieDTO(550, "Test Film", "2023-01-01", 7.5, 100.0, List.of())),
                1,  // totalPages
                1   // totalResults
        );
        MovieDetailsDTO details = new MovieDetailsDTO(
                550, "Test Film", "2023-01-01", 7.5, 100.0, 120, List.of()
        );
        CreditsDTO credits = new CreditsDTO(550, List.of(), List.of());

        // Fortæl mock'en hvad den skal svare
        when(mockApiReader.buildDiscoverUrl(1, "2021-01-01", "2026-01-01"))
                .thenReturn("discover-url");
        when(mockApiReader.readAPI("discover-url")).thenReturn("{}");
        when(mockApiReader.convertFromJson("{}", MovieResultDTO.class))
                .thenReturn(discoverResult);

        when(mockApiReader.buildMovieDetailsUrl(550)).thenReturn("details-url");
        when(mockApiReader.readAPI("details-url")).thenReturn("{}");
        when(mockApiReader.convertFromJson("{}", MovieDetailsDTO.class))
                .thenReturn(details);

        when(mockApiReader.buildCreditsUrl(550)).thenReturn("credits-url");
        when(mockApiReader.readAPI("credits-url")).thenReturn("{}");
        when(mockApiReader.convertFromJson("{}", CreditsDTO.class))
                .thenReturn(credits);

        // Act — kald service-metoden
        movieService.fetchAndSaveAllDanishMoviesSequential("2021-01-01", "2026-01-01");

        // Assert — filmen ligger nu i DB
        List<Movie> movies = movieDAO.findAll();
        assertEquals(1, movies.size());
        assertEquals("Test Film", movies.get(0).getTitle());
        assertEquals(550, movies.get(0).getTmdbId());
    }

    @Test
    void fetchAndSave_shouldSkipMovieThatAlreadyExists() {
        // Arrange — filmen findes allerede med tmdbId 550
        Movie existing = new Movie();
        existing.setTmdbId(550);
        existing.setTitle("Eksisterende");
        movieDAO.create(existing);

        MovieResultDTO discoverResult = new MovieResultDTO(
                1,
                List.of(new MovieDTO(550, "Test Film", "2023-01-01", 7.5, 100.0, List.of())),
                1, 1
        );

        when(mockApiReader.buildDiscoverUrl(1, "2021-01-01", "2026-01-01"))
                .thenReturn("discover-url");
        when(mockApiReader.readAPI("discover-url")).thenReturn("{}");
        when(mockApiReader.convertFromJson("{}", MovieResultDTO.class))
                .thenReturn(discoverResult);

        // Act
        movieService.fetchAndSaveAllDanishMoviesSequential("2021-01-01", "2026-01-01");

        // Assert — stadig kun 1 film (den eksisterende), ingen ny oprettet
        List<Movie> movies = movieDAO.findAll();
        assertEquals(1, movies.size());
        assertEquals("Eksisterende", movies.get(0).getTitle());
    }
}