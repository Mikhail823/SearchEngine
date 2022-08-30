package org.spring.services.repo;

import org.spring.model.Field;

import java.util.List;

public interface FieldRepo {
    Field getFieldByName(String fieldName);
    void save(Field field);
    List<Field> getAllField();
}
