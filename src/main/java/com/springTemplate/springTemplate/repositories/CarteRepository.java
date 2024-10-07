package com.springTemplate.springTemplate.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springTemplate.springTemplate.models.Carte;

public interface CarteRepository extends CrudRepository<Carte, Integer> {
    
}
