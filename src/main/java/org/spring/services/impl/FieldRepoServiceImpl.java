package org.spring.services.impl;


import org.spring.model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.spring.repo.FieldRepository;
import org.spring.services.repo.FieldRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class FieldRepoServiceImpl implements FieldRepo {

    private final FieldRepository fieldRepository;
    @Autowired
    public FieldRepoServiceImpl(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public Field getFieldByName(String fieldName) {
        return fieldRepository.findByName(fieldName);
    }
    @Override
    public synchronized void save(Field field) {
        fieldRepository.save(field);
    }

    @Override
    public List<Field> getAllField() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = fieldRepository.findAll();
        iterable.forEach(list::add);
        return list;
    }
}
