package app.dao;

import app.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<Director, Long> {

    private final EntityManagerFactory emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Director create(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(director);
            em.getTransaction().commit();
            return director;
        }
    }

    @Override
    public Optional<Director> findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(Director.class, id));
        }
    }

    @Override
    public List<Director> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
        }
    }

    @Override
    public Director update(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Director merged = em.merge(director);
            em.getTransaction().commit();
            return merged;
        }
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Director director = em.find(Director.class, id);
            if (director != null) {
                em.remove(director);
            }
            em.getTransaction().commit();
        }
    }
}