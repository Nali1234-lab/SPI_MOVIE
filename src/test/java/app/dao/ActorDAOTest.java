package app.dao;

import app.entities.Actor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ActorDAOTest extends BaseTest {

    private static ActorDAO actorDAO;

    @BeforeAll
    static void initDAO() {
        actorDAO = new ActorDAO(emf);
    }

    @Test
    void create_shouldPersistActor() {
        Actor actor = new Actor();
        actor.setTmdbId(819);
        actor.setName("Edward Norton");

        Actor saved = actorDAO.create(actor);

        assertNotNull(saved.getId());
        assertEquals("Edward Norton", saved.getName());
    }

    @Test
    void findByTmdbId_shouldReturnActor_whenItExists() {
        Actor actor = new Actor();
        actor.setTmdbId(819);
        actor.setName("Edward Norton");
        actorDAO.create(actor);

        Optional<Actor> found = actorDAO.findByTmdbId(819);

        assertTrue(found.isPresent());
        assertEquals("Edward Norton", found.get().getName());
    }

    @Test
    void findByTmdbId_shouldReturnEmpty_whenItDoesNotExist() {
        Optional<Actor> found = actorDAO.findByTmdbId(999999);
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllActors() {
        Actor a1 = new Actor();
        a1.setTmdbId(1);
        a1.setName("Skuespiller Et");
        Actor a2 = new Actor();
        a2.setTmdbId(2);
        a2.setName("Skuespiller To");
        actorDAO.create(a1);
        actorDAO.create(a2);

        List<Actor> all = actorDAO.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void delete_shouldRemoveActor() {
        Actor actor = new Actor();
        actor.setTmdbId(1);
        actor.setName("Til sletning");
        Actor saved = actorDAO.create(actor);

        actorDAO.delete(saved.getId());

        assertTrue(actorDAO.findById(saved.getId()).isEmpty());
    }
}