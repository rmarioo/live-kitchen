package com.rmarioo.live.kitchen.infrastructure.persistence

import com.rmarioo.live.kitchen.infrastructure.persistence.model.*
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersistenceRecipeRepository : CrudRepository<Recipe, Int> {

    @Query("SELECT id FROM recipes WHERE name = :name")
    fun findIdByName(name: String): Int?
}

@Repository
interface StepRepository : CrudRepository<Step, Int> {

    @Query("SELECT id FROM steps WHERE name = :name AND recipe_id= :recipeId")
    fun findIdByNameAndRecipeId(name: String,recipeId: Int): Int?
    @Query("SELECT id FROM steps WHERE recipe_id= :recipeId")
    fun findStepIdsByRecipeId(recipeId: Int): List<Int>
}

@Repository
interface StepDependencyRepository : CrudRepository<StepDependencies, Int> {
    @Modifying
    @Query("DELETE  FROM steps_dependencies WHERE step_id= :stepId")
    fun deleteByStepId(stepId: Int)
}

@Repository
interface IngredientRepository : CrudRepository<Ingredient, Int> {
    fun findByRecipeId(recipeId: Int): List<Ingredient>
    @Query("SELECT id FROM ingredients WHERE name = :name AND recipe_id= :recipeId")
    fun findIdByNameAndRecipeId(name: String,recipeId: Int): Int?

    @Modifying
    @Query("DELETE  FROM ingredients WHERE recipe_id= :recipeId")
    fun deleteByRecipeId(recipeId: Int)
}

@Repository
interface PersistenceFoodStorageRepository : CrudRepository<FoodStorage, Int>

@Repository
interface FoodRepository : CrudRepository<Food, Int> {

    fun findByFoodStorageId(foodStorageId: Int): List<Food>
}
