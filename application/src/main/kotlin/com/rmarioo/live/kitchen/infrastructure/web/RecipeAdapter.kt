package com.rmarioo.live.kitchen.infrastructure.web

import com.rmarioo.live.kitchen.core.model.EndlessQuantity
import com.rmarioo.live.kitchen.core.model.FiniteQuantity
import com.rmarioo.live.kitchen.core.model.Food
import com.rmarioo.live.kitchen.core.model.Ingredients
import com.rmarioo.live.kitchen.infrastructure.web.model.Ingredient
import com.rmarioo.live.kitchen.infrastructure.web.model.Recipe
import com.rmarioo.live.kitchen.infrastructure.web.model.Step
import com.rmarioo.live.kitchen.core.model.Recipe as DomainRecipe

class RecipeAdapter {
    fun toDomain(recipe: Recipe): DomainRecipe {
        return DomainRecipe(
            recipe.name,
            domainStepsFrom(recipe.steps),
            domainIngredients(recipe.ingredients)
        )
    }

    private fun domainIngredients(ingredients: List<Ingredient>): Ingredients {
        return Ingredients(ingredients.map { ing ->
            Food(ing.name, ing.quantity?.let { FiniteQuantity(it) } ?: EndlessQuantity)
        })
    }

    fun toWeb(recipe: DomainRecipe): Recipe {
        return Recipe(
            id = null,
            name = recipe.name,
            steps = stepsFrom(recipe.steps),
            ingredients = fromDomain(recipe.ingredients)
        )
    }

    private fun fromDomain(ingredients: Ingredients): List<Ingredient> {

        return ingredients.food.map { food: Food -> Ingredient(food.name,
            when(food.quantity) {
                EndlessQuantity -> null
                is FiniteQuantity -> (food.quantity as FiniteQuantity).value
            }
            ) }
    }


    private fun stepsFrom(steps: List<com.rmarioo.live.kitchen.core.model.Step>): List<Step> {
        return steps.map {
            Step(name = it.name, time = it.time, dependantStepNames(it.dependsOnStep)
            )
        }
    }

    private fun dependantStepNames(dependsOnStep: List<com.rmarioo.live.kitchen.core.model.Step>): List<String> {
        return dependsOnStep.map { it.name }
    }

    private fun domainStepsFrom(steps: List<Step>): List<com.rmarioo.live.kitchen.core.model.Step> {
        return steps.map {
            com.rmarioo.live.kitchen.core.model.Step(
                it.name,
                it.time,
                adaptDependantSteps(it.dependsOnSteps, steps)
            )
        }

    }

    private fun adaptDependantSteps(dependsOnSteps: List<String>, steps: List<Step>): List<com.rmarioo.live.kitchen.core.model.Step> {
        val dependantSteps = steps.filter { step: Step -> dependsOnSteps.contains(step.name) }
        return domainStepsFrom(dependantSteps)
    }
}
