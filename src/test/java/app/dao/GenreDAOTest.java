package app.dao;

import app.entities.Genre;
import app.entities.Movie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GenreDAOTest extends BaseTest {

    private static GenreDAO genreDAO;
    private static MovieDAO movieDAO;

    @BeforeAll
    static void initDAO() {
        genreDAO = new GenreDAO(emf);
        movieDAO = new MovieDAO(emf);
    }

    @Test
    void create_shouldPersistGenre() {
        Genre genre = new Genre();

        genre.setTmdbId(1);
        genre.setName("Drama");
        Genre saved = genreDAO.create(genre);

        assertNotNull(saved.getId());
        assertEquals("Drama", saved.getName());
    }

    @Test
    void findByTmdbId_shouldReturnGenre_whenItExists() {
        Genre genre = new Genre();
        genre.setTmdbId(18);
        genre.setName("Drama");
        genreDAO.create(genre);

        Optional<Genre> found = genreDAO.findByTmdbId(18);

        assertTrue(found.isPresent());
        assertEquals("Drama", found.get().getName());
    }

    @Test
    void findByTmdbId_shouldReturnEmpty_whenItDoesNotExist() {
        Optional<Genre> found = genreDAO.findByTmdbId(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllGenres() {
        Genre g1 = new Genre();
        g1.setTmdbId(1);
        g1.setName("Drama");
        Genre g2 = new Genre();
        g2.setTmdbId(2);
        g2.setName("Comedy");
        genreDAO.create(g1);
        genreDAO.create(g2);

        List<Genre> all = genreDAO.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void findMoviesByGenre_shouldReturnOnlyMoviesInThatGenre() {
        // Arrange: to genrer, to film - kun én film skal matche "Drama"
        Genre drama = new Genre();
        drama.setName("Drama");
        drama = genreDAO.create(drama);

        Genre comedy = new Genre();
        comedy.setName("Comedy");
        comedy = genreDAO.create(comedy);

        Movie dramaMovie = new Movie();
        dramaMovie.setTmdbId(1);
        dramaMovie.setTitle("Drama Film");
        dramaMovie.getGenres().add(drama);
        movieDAO.create(dramaMovie);

        Movie comedyMovie = new Movie();
        comedyMovie.setTmdbId(2);
        comedyMovie.setTitle("Comedy Film");
        comedyMovie.getGenres().add(comedy);
        movieDAO.create(comedyMovie);

        // Act
        List<Movie> dramaMovies = genreDAO.findMoviesByGenre("Drama");

        // Assert: kun Drama Film skal komme med, ikke Comedy Film
        assertEquals(1, dramaMovies.size());
        assertEquals("Drama Film", dramaMovies.get(0).getTitle());
    }

    @Test
    void delete_shouldRemoveGenre() {
        Genre genre = new Genre();
        genre.setTmdbId(1);
        genre.setName("Til sletning");
        Genre saved = genreDAO.create(genre);

        genreDAO.delete(saved.getId());

        assertTrue(genreDAO.findById(saved.getId()).isEmpty());
    }
}