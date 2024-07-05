package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.usecases.FoodBuilder.Companion.food
import com.rmarioo.live.kitchen.core.usecases.StepBuilder.Companion.step

class RecipeBuilder {

    companion object {

        fun recipe(name: String="anyName", steps: List<Step> = listOf(
            step(name = "step1"),
            step(name = "step2")
        ),
        ingredients: Ingredients = Ingredients(listOf(food()))
        ): Recipe {
            return Recipe(name = name,steps,ingredients)
        }
    }
}

class StepBuilder {

    companion object {

        fun step(name: String = "anyStep", dependOnSteps: List<Step> = listOf(), timeToComplete: Long = 10): Step {
            return Step(name, timeToComplete, dependOnSteps)
        }
    }
}

class FoodBuilder {

    companion object {

        fun food(name: String="a food", quantity: Quantity = EndlessQuantity): Food {

            return Food(name, quantity)
        }

    }
}


