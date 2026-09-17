package app.dao;

import java.util.List;
import java.util.Optional;

public interface IDAO<T, ID> {
    T create(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T update(T entity);
    void delete(ID id);
}