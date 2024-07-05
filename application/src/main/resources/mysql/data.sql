
use recipes;

INSERT INTO recipes (name) VALUES ('Classic Tomato Spaghetti');

-- Steps for 'Spaghetti Bolognese'
INSERT INTO ingredients (name, quantity, recipe_id) VALUES
                                                        ('Spaghetti', 200, 1),
                                                        ('Tomatoes', 200, 1),
                                                        ('Olive oil', 300, 1),
                                                        ('Garlic', 2, 1),
                                                        ('Basil', null, 1),
                                                        ('Salt', null, 1),
                                                        ('Pepper', null, 1);


-- Dependencies for 'Spaghetti Bolognese'
INSERT INTO steps (name, time, recipe_id) VALUES
                                                         ('Boil water in a large pot', 4000, 1),
                                                         ('Add spaghetti to boiling water and cook as per package instructions', 8000, 1),
                                                         ('While boil water, chop the tomatoes and garlic', 9000, 1),
                                                         ('In a saucepan, heat olive oil over medium heat and sauté garlic until golden', 9000, 1),
                                                         ('Add chopped tomatoes to the saucepan, season with salt and pepper, and cook until soft', 7000, 1),
                                                         ('Tear basil leaves and add to the sauce', 7000, 1),
                                                         ('Drain the spaghetti and mix with the tomato sauce', 7000, 1),
                                                         ('Serve hot, garnished with basil leaves', 7000, 1);


-- Ingredients for 'Spaghetti Bolognese'
INSERT INTO steps_dependencies (step_id, depends_on_id) VALUES
                                                            (2, 1), -- 'Add spaghetti to boiling water' depends on 'Boil water'
                                                            (5, 3), -- 'Add chopped tomatoes to the saucepan' depends on 'While the spaghetti is cooking, chop the tomatoes and garlic'
                                                            (6, 4), -- 'Tear basil leaves and add to the sauce' depends on 'In a saucepan, heat olive oil'
                                                            (7, 6), -- 'Drain the spaghetti and mix with the tomato sauce' depends on 'Tear basil leaves and add to the sauce'
                                                            (8, 7); -- 'Serve hot, garnished with basil leaves' depends on 'Drain the spaghetti and mix with the tomato sauce'


INSERT INTO recipes (name) VALUES ('Chocolate cake');

-- Steps for 'Chocolate Cake'
INSERT INTO steps (name, time, recipe_id) VALUES
                                                         ('Preheat oven', 600, 2),    -- 9
                                                         ('Mix ingredients', 900, 2), -- 10
                                                         ('Bake cake', 1800, 2),      -- 11
                                                         ('Cool cake', 1200, 2);      -- 12


-- Dependencies for 'Chocolate Cake'
INSERT INTO steps_dependencies (step_id, depends_on_id) VALUES
                                                            (10, 9), -- 'Mix ingredients' depends on 'Preheat oven'
                                                            (11, 10), -- 'Bake cake' depends on 'Mix ingredients'
                                                            (12, 11); -- 'Cool cake' depends on 'Bake cake'

-- Ingredients for 'Chocolate Cake'
INSERT INTO ingredients (name, quantity, recipe_id) VALUES
                                                        ('Flour', 250, 2),
                                                        ('Sugar', 200, 2),
                                                        ('Cocoa Powder', 50, 2),
                                                        ('Eggs', 3, 2),
                                                        ('Butter', 100, 2);

INSERT INTO foodstorage (name) VALUES ('Fridge');

INSERT INTO foods (name, quantity, food_storage_id) VALUES
                                                       ('Spaghetti', 1000, 1),
                                                       ('Tomatoes', 300, 1),
                                                       ('Olive oil', 500, 1),
                                                       ('Garlic', 20, 1),
                                                       ('Basil', null, 1),
                                                       ('Salt', null, 1),
                                                       ('Pepper', null, 1),

                                                       ('Onion', 10, 1),
                                                       ('Flour', 500, 1),
                                                       ('Sugar', 1000, 1),
                                                       ('Cocoa Powder', 200, 1);


