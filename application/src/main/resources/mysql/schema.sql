drop schema recipes;

CREATE SCHEMA IF NOT EXISTS recipes;

use recipes;

CREATE TABLE if not exists recipes
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists steps
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    time            INT          NOT NULL,
    recipe_id       INT,
    FOREIGN KEY (recipe_id) REFERENCES recipes (id)
);

CREATE TABLE if not exists steps_dependencies
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    step_id  INT,
    depends_on_id   INT,
    FOREIGN KEY (step_id) REFERENCES steps (id),
    FOREIGN KEY (depends_on_id) REFERENCES steps(id)
);

CREATE TABLE if not exists ingredients
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    quantity  INT          NULL,
    recipe_id INT,
    FOREIGN KEY (recipe_id) REFERENCES recipes (id)
);

CREATE TABLE if not exists foodstorage
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists foods
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    quantity       INT          NULL,
    food_storage_id INT,
    FOREIGN KEY (food_storage_id) REFERENCES foodstorage (id)
);



