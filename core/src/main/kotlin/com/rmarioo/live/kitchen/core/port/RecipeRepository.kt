package com.rmarioo.live.kitchen.core.port

import com.rmarioo.live.kitchen.core.model.Recipe

interface RecipeRepository {
    fun findRecipeById(id: Int): Recipe?
    fun findRecipeByName(name: String): Recipe?
    fun add(recipe: Recipe): Int
    fun save(recipe: Recipe, recipeId: Int)
    fun deleteRecipeById(recipeId: Int)
}
