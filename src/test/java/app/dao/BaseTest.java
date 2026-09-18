package app.dao;

import app.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Properties;

@Testcontainers
public abstract class BaseTest {

    @Container
    protected static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16.2")
                    .withDatabaseName("test_movie_db")
                    .withUsername("test")
                    .withPassword("test");

    protected static EntityManagerFactory emf;

    @BeforeAll
    static void setUpAll() {
        Properties props = new Properties();
        props.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.put("hibernate.connection.url", postgres.getJdbcUrl());
        props.put("hibernate.connection.username", postgres.getUsername());
        props.put("hibernate.connection.password", postgres.getPassword());
        props.put("hibernate.hbm2ddl.auto", "create-drop");

        Configuration configuration = new Configuration();
        configuration.setProperties(props);
        configuration.addAnnotatedClass(Movie.class);
        configuration.addAnnotatedClass(Genre.class);
        configuration.addAnnotatedClass(Actor.class);
        configuration.addAnnotatedClass(Director.class);
        configuration.addAnnotatedClass(MovieCast.class);

        emf = configuration.buildSessionFactory().unwrap(EntityManagerFactory.class);
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void cleanDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM MovieCast").executeUpdate();
            em.createQuery("DELETE FROM Movie").executeUpdate();
            em.createQuery("DELETE FROM Genre").executeUpdate();
            em.createQuery("DELETE FROM Actor").executeUpdate();
            em.createQuery("DELETE FROM Director").executeUpdate();
            em.getTransaction().commit();
        }
    }
}