package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.ChefService
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.core.usecases.FoodBuilder.Companion.food
import com.rmarioo.live.kitchen.core.usecases.RecipeBuilder.Companion.recipe
import com.rmarioo.live.kitchen.core.usecases.StepBuilder.Companion.step
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PrepareRecipeTest {


    @Test
    fun `async prepared recipe with dependant steps`() {


        val boilWater = step(name = "boil water", timeToComplete = 300)
        val prepareSouce = step(name = "prepare souce", timeToComplete = 200)
        val cookSpaghetti = step(name = "cook spaghetti", dependOnSteps = listOf(boilWater), timeToComplete = 80)
        val stirFry = step(
            name = "stir-fry spaghetti and sauce",
            dependOnSteps = listOf(cookSpaghetti, prepareSouce),
            timeToComplete = 20
        )

        val recipe = recipe(name = "spaghetti with tomato", ingredients = ingredients, steps = listOf(boilWater, prepareSouce, cookSpaghetti, stirFry))


        val underTest = PrepareRecipe(InMemoryRecipeRepository(mutableMapOf(recipeId to recipe)), foodStorageRepository, chefService, kitchen)

        var events: List<KitchenEvent>
        do {
            events = underTest.prepareAsync("1", recipeId).also { Thread.sleep(2) }
        } while (notFoundRecipeUpdatedEvent(events))

        println("completion time ${recipeUpdatedEvent(events).completionTime} total step time ${recipe.steps.sumOf { it.time }}")
        assertThat(recipeUpdatedEvent(events).completionTime).`as`("completion time higher than steps time") .isLessThan(recipe.steps.sumOf { it.time })

    }

    private fun recipeUpdatedEvent(events: List<KitchenEvent>) =
        events.filterIsInstance<RecipeUpdated>().map { it }.firstOrNull()!!


    @Test
    fun `execute recipe with not enough ingredients `() {

        val foodStorage = FoodStorage(
            mutableListOf(
                food(name = "water", EndlessQuantity),
                food(name = "tomato", EndlessQuantity),
                food(name = "spaghetti", FiniteQuantity(200))
            )
        )
        val foodStorageRepository: FoodStorageRepository = InMemoryFoodStorageRepository(foodStorage)

        val ingredients = Ingredients(
            listOf(
                food(name = "water", FiniteQuantity(1)),
                food(name = "tomato", FiniteQuantity(200)),
                food(name = "spaghetti", FiniteQuantity(500))
            )
        )

        val boilWater = step(name = "boil water", timeToComplete = 300)
        val recipe = recipe(
            name = "spaghetti with tomato",
            ingredients = ingredients,
            steps = listOf(boilWater)
        )

        val underTest = PrepareRecipe(InMemoryRecipeRepository(mutableMapOf(1 to recipe)), foodStorageRepository, StubChefService(), Kitchen())


        var events: List<KitchenEvent>

        val maxWaitTime = recipe.steps.sumOf { it.time + 10 }
        val startTime = System.currentTimeMillis()
        do {
            events = underTest.prepareAsync("1", 1).also { Thread.sleep(10) }

        } while (notFoundRecipeUpdatedEvent(events) && timeSpentFrom(startTime) < maxWaitTime)

        val notEnoughFoodInStorage =
            events.filter { it is NotEnoughFoodInStorage }.map { it as NotEnoughFoodInStorage }.firstOrNull()

        assertThat(notEnoughFoodInStorage?.name).`as`("wrong missing food").isEqualTo("spaghetti")

    }

    private fun timeSpentFrom(startTime: Long) = System.currentTimeMillis() - startTime

    private fun notFoundRecipeUpdatedEvent(events: List<KitchenEvent>) =
        events.filterIsInstance<RecipeUpdated>().map { it }.firstOrNull() == null


    companion object {

        val recipeId = 1
        val foodStorage = FoodStorage(
            mutableListOf(
                food(name = "water", EndlessQuantity),
                food(name = "tomato", EndlessQuantity),
                food(name = "spaghetti", EndlessQuantity)
            )
        )
        val foodStorageRepository: FoodStorageRepository = InMemoryFoodStorageRepository(foodStorage)

        val ingredients = Ingredients(
            listOf(
                food(name = "water", FiniteQuantity(1)),
                food(name = "tomato", FiniteQuantity(200)),
                food(name = "spaghetti", FiniteQuantity(500))
            )
        )


        val chefService: ChefService = StubChefService()

        val kitchen = Kitchen()
    }

}

