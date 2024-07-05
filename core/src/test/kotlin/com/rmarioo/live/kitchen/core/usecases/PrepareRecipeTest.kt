package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.model.NotEnoughFood
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.ChefService
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.core.usecases.FoodBuilder.Companion.food
import com.rmarioo.live.kitchen.core.usecases.RecipeBuilder.Companion.recipe
import com.rmarioo.live.kitchen.core.usecases.StepBuilder.Companion.step
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.time.measureTimedValue

class PrepareRecipeTest {

    @Test
    fun `prepared recipe with dependant steps`() {

        val foodStorage = FoodStorage(
            mutableListOf(
                food(name = "water", EndlessQuantity),
                food(name = "tomato", EndlessQuantity),
                food(name = "spaghetti", EndlessQuantity)
            )
        )
        val foodStorageRepository : FoodStorageRepository = InMemoryFoodStorageRepository(foodStorage)

        val ingredients = Ingredients(
            listOf(
                food(name = "water", FiniteQuantity(1)),
                food(name = "tomato", FiniteQuantity(200)),
                food(name = "spaghetti", FiniteQuantity(500))
            )
        )

        val boilWater     = step(name = "boil water"   , timeToComplete = 2000)
        val prepareSouce  = step(name = "prepare souce", timeToComplete = 1000)
        val cookSpaghetti = step(name = "cook spaghetti", dependOnSteps = listOf(boilWater), timeToComplete = 800)
        val stirFry       = step(name = "stir-fry spaghetti and sauce", dependOnSteps = listOf(cookSpaghetti, prepareSouce), timeToComplete = 200)

        val recipe = recipe(name = "spaghetti with tomato",
            ingredients = ingredients,
            steps = listOf(boilWater, prepareSouce, cookSpaghetti, stirFry))


        val chefService: ChefService = StubChefService()
        val recipeRepository: RecipeRepository = InMemoryRecipeRepository(mutableListOf(recipe))

        val (prepareRecipeResult, duration) = measureTimedValue {
            PrepareRecipe(recipeRepository, foodStorageRepository, chefService, Kitchen()).prepare(recipe)
        }
        assertThat(prepareRecipeResult).isInstanceOf(CompletedDish::class.java).extracting { it as CompletedDish }
            .extracting { it.dish.name }
            .isEqualTo(recipe.name)

        assertThat(duration.inWholeSeconds).isEqualTo(2+1)

    }


    @Test
    fun `prepared recipe independant steps`() {

        val foodStorageRepository = InMemoryFoodStorageRepository(FoodStorage(mutableListOf(food(name = "cake", EndlessQuantity))))

        val recipe = recipe(
            name = "dummy recipe",
            ingredients = Ingredients(listOf(food(name = "cake", FiniteQuantity(1)))),
            steps = listOf(step(name = "get already prepared cake", timeToComplete = 1000))
        )


        val chefService: ChefService = StubChefService()
        val recipeRepository: RecipeRepository = InMemoryRecipeRepository(mutableListOf(recipe))

        val (prepareRecipeResult, duration) = measureTimedValue {
            PrepareRecipe(recipeRepository, foodStorageRepository, chefService, Kitchen()).prepare(recipe)
        }
        assertThat(prepareRecipeResult).isInstanceOf(CompletedDish::class.java).extracting { it as CompletedDish }
            .extracting { it.dish.name }
            .isEqualTo(recipe.name)

        assertThat(duration.inWholeSeconds).isEqualTo(1)
    }


/*
    @Test
    fun `execute not existent recipe`() {

        val recipeRepository: RecipeRepository = InMemoryRecipeRepository(mutableListOf())
        val foodStorageRepository : FoodStorageRepository = InMemoryFoodStorageRepository(FoodStorage(mutableListOf()))
        val kitchenService: KitchenService = StubKitchenService()

        val prepareRecipeResult = PrepareRecipe(recipeRepository, foodStorageRepository, kitchenService, Kitchen()).prepare("not existing recipe")

        assertThat(prepareRecipeResult).isInstanceOf(RecipeNotFoundResult::class.java).extracting { it as RecipeNotFoundResult }
            .extracting { it.name }
            .isEqualTo("not existing recipe")

    }
*/

    @Test
    fun `execute recipe with not enough spaghetti `() {

        val foodStorage = FoodStorage(
            mutableListOf(
                food(name = "water", EndlessQuantity),
                food(name = "tomato", EndlessQuantity),
                food(name = "spaghetti", FiniteQuantity(200))
            )
        )
        val foodStorageRepository : FoodStorageRepository = InMemoryFoodStorageRepository(foodStorage)

        val ingredients = Ingredients(
            listOf(
                food(name = "water", FiniteQuantity(1)),
                food(name = "tomato", FiniteQuantity(200)),
                food(name = "spaghetti", FiniteQuantity(500))
            )
        )

        val recipe = recipe(name = "spaghetti with tomato",
            ingredients = ingredients,
            steps = listOf())


        val chefService: ChefService = StubChefService()
        val recipeRepository: RecipeRepository = InMemoryRecipeRepository(mutableListOf(recipe))

        val prepareRecipeResult =  PrepareRecipe(recipeRepository, foodStorageRepository, chefService, Kitchen()).prepare(recipe)


        assertThat(prepareRecipeResult).isInstanceOf(NotEnoughFood::class.java).extracting { it as NotEnoughFood }
            .extracting { it.name }
            .isEqualTo("spaghetti")

    }


}

