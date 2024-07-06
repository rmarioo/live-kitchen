package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.Recipe
import com.rmarioo.live.kitchen.core.port.RecipeRepository

class InMemoryRecipeRepository(private val recipes: MutableMap<Int,Recipe>) : RecipeRepository {
    override fun findRecipeById(id: Int): Recipe? {
        return recipes[id]
    }


    override fun findRecipeByName(name: String): Recipe? {
       return recipes.values.firstOrNull { recipe: Recipe -> recipe.name == name }
    }

    override fun add(recipe: Recipe): Int {

        val newId = if (recipes.keys.isEmpty()) 1 else recipes.keys.max() + 1
        save(recipe,newId)
        return newId
    }

    override fun save(recipe: Recipe, recipeId: Int) {
        recipes[recipeId] = recipe
    }

    override fun deleteRecipeById(recipeId: Int) {
        recipes.remove(recipeId)
    }

}
