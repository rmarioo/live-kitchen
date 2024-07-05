package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.Recipe
import com.rmarioo.live.kitchen.core.port.RecipeRepository

class InMemoryRecipeRepository(private val recipes: MutableList<Recipe>) : RecipeRepository {
    override fun findRecipeById(id: Int): Recipe? {
        return null
    }


    override fun findRecipeByName(name: String): Recipe? {
       return recipes.firstOrNull { recipe: Recipe -> recipe.name == name }
    }

    override fun add(recipe: Recipe): Int {
        recipes.add(recipe)
        return 1
    }

    override fun save(recipe: Recipe, recipeId: Int) {
        TODO("Not yet implemented")
    }

    override fun deleteRecipeById(recipeId: Int) {
        TODO("Not yet implemented")
    }

}
