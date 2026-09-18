package app.dao;

import app.config.HibernateConfig;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDAOTest {
    private static MovieDAO movieDAO;
    @BeforeEach
    void setUp() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        movieDAO =new MovieDAO((emf));
    }
    @Test

    void findAll_shouldReturnMovies() {
        List<Movie> movies = movieDAO.findAll();
        assertFalse(movies.isEmpty());
        System.out.println("Antal film i databasen: " + movies.size());
    }
    @Test
    void searchByTitle_shouldBeCaseInsensitive() {
        // Act — søg med småt
        List<Movie> lower = movieDAO.searchByTitle("kærlighed");
        // Act — søg med stort
        List<Movie> upper = movieDAO.searchByTitle("KÆRLIGHED");

        // Assert — samme antal resultater uanset store/små bogstaver
        assertEquals(lower.size(), upper.size(),
                "Case-insensitive søgning burde give samme antal resultater");
    }
    @Test
    void searchByTitle_shouldMatchSubstring() {
        List<Movie> full = movieDAO.searchByTitle("kærlighed");
        List<Movie> part = movieDAO.searchByTitle("k");

        // Hvis "kærlighed" findes, skal "kær" finde mindst lige så mange
        assertTrue(part.size() >= full.size(),
                "Substring-søgning burde finde mindst lige så mange som fuld søgning");
    }
}