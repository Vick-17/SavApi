
-- Table users :

-- id: clé primaire, auto-incrémentée grâce à SERIAL.
-- email: adresse e-mail unique, non nulle.
-- name: nom de l'utilisateur, non nul.
-- password: mot de passe de l'utilisateur, non nul.
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Table roles :

-- id: clé primaire, auto-incrémentée grâce à SERIAL.
-- name: nom du rôle, unique et non nul.
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Table user_roles :

-- user_id: référence à l'ID de la table users, non nulle.
-- role_id: référence à l'ID de la table roles, non nulle.
-- Clés étrangères user_id et role_id pour maintenir l'intégrité référentielle et suppression en cascade.
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id,role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);