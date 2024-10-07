package com.springTemplate.springTemplate.services.user;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springTemplate.springTemplate.models.User;
import com.springTemplate.springTemplate.repositories.UserRepository;

import jakarta.transaction.Transactional;

/**
 * Classe encapsulant le code permettant de gérer les utilisateurs.
 * Doit nécessairement implémenter "UserDetailsService" qui sera utilisée par
 * "AuthenticationManager"
 */
@Service
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "L'utilisateur avec le nom %s n'existe pas.";
    private static final String USER_FOUND_MESSAGE = "L'utilisateur avec le nom %s existe en base de données.";

    @Autowired
    private UserRepository userRepository;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Instancie un objet "User" qui est une instance d'une classe héritant de
     * "UserDetails"
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ATTENTION -> objet de la classe "fr.afpahostel.models.User"
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // pas d'utilisateur, on renvoie une exception
            String message = String.format(USER_NOT_FOUND_MESSAGE, username);
            logger.error(message);
            throw new UsernameNotFoundException(message);
        } else {
            // utilisateur retrouvé, on instancie une liste d' "authorities" qui
            // correspondent à des roles
            logger.debug(USER_FOUND_MESSAGE, username);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });

            /**
             * ATTENTION -> instanciation d'un objet de la classe "User" provenant du
             * package "org.springframework.security.core.userdetails"
             * Cette classe hérite de "UserDetails" et est propre au framework de sécurité
             * de Spring.
             */
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    authorities);
        }
    }
}
