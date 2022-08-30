package org.spring.repo;

import org.spring.model.Field;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends CrudRepository<Field, Long> {
    Field findByName(String fieldName);
}
