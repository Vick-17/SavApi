package com.springTemplate.springTemplate.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springTemplate.springTemplate.models.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * Permet de retrouver un utilisateur suivant son nom.
     * 
     * Si une fonction commence par "findBy" et est suivi d'un nom de colonne il
     * n'est pas nécessaire
     * d'écrire une requête JPQL (pour information :
     * https://gayerie.dev/epsi-b3-orm/javaee_orm/jpa_queries.html)
     * 
     * @param name Le nom de l'utilisateur recherché
     * @return L'utilisateur retrouvé
     */
    User findByUsername(String username);
}
