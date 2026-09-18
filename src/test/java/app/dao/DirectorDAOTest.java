package app.dao;

import app.entities.Director;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DirectorDAOTest extends BaseTest {

    private static DirectorDAO directorDAO;

    @BeforeAll
    static void initDAO() {
        directorDAO = new DirectorDAO(emf);
    }

    @Test
    void create_shouldPersistDirector() {
        Director director = new Director();
        director.setTmdbId(7467);
        director.setName("David Fincher");

        Director saved = directorDAO.create(director);

        assertNotNull(saved.getId());
        assertEquals("David Fincher", saved.getName());
    }

    @Test
    void findByTmdbId_shouldReturnDirector_whenItExists() {
        Director director = new Director();
        director.setTmdbId(7467);
        director.setName("David Fincher");
        directorDAO.create(director);

        Optional<Director> found = directorDAO.findByTmdbId(7467);

        assertTrue(found.isPresent());
        assertEquals("David Fincher", found.get().getName());
    }

    @Test
    void findByTmdbId_shouldReturnEmpty_whenItDoesNotExist() {
        Optional<Director> found = directorDAO.findByTmdbId(999999);
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllDirectors() {
        Director d1 = new Director();
        d1.setTmdbId(1);
        d1.setName("Instruktør Et");
        Director d2 = new Director();
        d2.setTmdbId(2);
        d2.setName("Instruktør To");
        directorDAO.create(d1);
        directorDAO.create(d2);

        List<Director> all = directorDAO.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void delete_shouldRemoveDirector() {
        Director director = new Director();
        director.setTmdbId(1);
        director.setName("Til sletning");
        Director saved = directorDAO.create(director);

        directorDAO.delete(saved.getId());

        assertTrue(directorDAO.findById(saved.getId()).isEmpty());
    }
}