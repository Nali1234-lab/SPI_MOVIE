package app.dao;

import app.entities.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class GenreDAO implements IDAO<Genre, Long> {

    private final EntityManagerFactory emf;

    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Genre create(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();
            return genre;
        }
    }

    @Override
    public Optional<Genre> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(Genre.class, id));
        }
    }

    @Override
    public List<Genre> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
        }
    }

    @Override
    public Genre update(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Genre merged = em.merge(genre);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Genre genre = em.find(Genre.class, id);
            if (genre != null) {
                em.remove(genre);
            }
            em.getTransaction().commit();
        }
    }

    // Krav 4: find alle film inden for en bestemt genre
    public List<app.entities.Movie> findMoviesByGenre(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM Movie m JOIN m.genres g WHERE g.name = :name", app.entities.Movie.class)
                    .setParameter("name", genreName)
                    .getResultList();
        }
    }
}