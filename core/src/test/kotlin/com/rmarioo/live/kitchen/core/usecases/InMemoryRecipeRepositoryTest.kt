package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.Recipe
import com.rmarioo.live.kitchen.core.usecases.RecipeBuilder.Companion.recipe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InMemoryRecipeRepositoryTest {


    @Test
    fun `return null if no recipe is found`() {
        val underTest = InMemoryRecipeRepository(mutableListOf())
        val recipe: Recipe? = underTest.findRecipeByName("a recipe")

        assertThat(recipe).isNull()
    }

    @Test
    fun `add recipe`() {
        val underTest = InMemoryRecipeRepository(mutableListOf())

        underTest.add(recipe)

        val recipeFound = underTest.findRecipeByName(recipe.name)
        assertThat(recipeFound).isEqualTo(recipe)
    }

    @Test
    fun `found a previously added recipe`() {
        val underTest = InMemoryRecipeRepository(mutableListOf(recipe))
        val recipeFound: Recipe? = underTest.findRecipeByName(recipe.name)

        assertThat(recipeFound).isEqualTo(recipe)
    }

    companion object {
       val recipe= recipe()
    }
}



