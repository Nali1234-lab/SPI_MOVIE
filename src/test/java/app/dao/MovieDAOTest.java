package app.dao;

import app.entities.Movie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest extends BaseTest {

    private static MovieDAO movieDAO;

    @BeforeAll
    static void initDAO() {
        movieDAO = new MovieDAO(emf);
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoMoviesExist() {
        List<Movie> movies = movieDAO.findAll();
        assertTrue(movies.isEmpty());
    }

    @Test
    void create_shouldPersistMovie() {
        Movie movie = new Movie();
        movie.setTmdbId(1);
        movie.setTitle("Test Film");

        Movie saved = movieDAO.create(movie);

        assertNotNull(saved.getId());
        assertEquals("Test Film", saved.getTitle());
    }

    @Test
    void findById_shouldReturnMovie_whenItExists() {
        Movie movie = new Movie();
        movie.setTmdbId(2);
        movie.setTitle("Findbar Film");
        Movie saved = movieDAO.create(movie);

        Optional<Movie> found = movieDAO.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Findbar Film", found.get().getTitle());
    }

    @Test
    void searchByTitle_shouldBeCaseInsensitive() {
        Movie movie = new Movie();
        movie.setTmdbId(3);
        movie.setTitle("Kærlighed ved Havet");
        movieDAO.create(movie);

        List<Movie> result = movieDAO.searchByTitle("KÆRLIGHED");

        assertEquals(1, result.size());
    }

    @Test
    void delete_shouldRemoveMovie() {
        Movie movie = new Movie();
        movie.setTmdbId(4);
        movie.setTitle("Film der skal slettes");
        Movie saved = movieDAO.create(movie);

        movieDAO.delete(saved.getId());

        assertTrue(movieDAO.findById(saved.getId()).isEmpty());
    }
}