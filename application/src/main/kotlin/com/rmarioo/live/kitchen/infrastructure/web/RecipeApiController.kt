package com.rmarioo.live.kitchen.infrastructure.web


import com.rmarioo.live.kitchen.core.model.CompletedDish
import com.rmarioo.live.kitchen.core.model.NotEnoughFood
import com.rmarioo.live.kitchen.core.model.RecipeNotFoundResult
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.core.usecases.PrepareRecipe
import com.rmarioo.live.kitchen.infrastructure.web.model.Dish
import com.rmarioo.live.kitchen.infrastructure.web.model.Recipe
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.*
import kotlin.time.measureTimedValue


@RestController
@RequestMapping("/recipes")
class RecipeApiController(
    val recipeRepository: RecipeRepository,
    val recipeAdapter: RecipeAdapter,
    val prepareRecipe: PrepareRecipe
) {

    private val log = LoggerFactory.getLogger(this::class.java)


    @PostMapping("/prepare/{recipeId}")
    fun prepareRecipe(@PathVariable recipeId: Int): ResponseEntity<*> {

        val recipe = recipeRepository.findRecipeById(recipeId) ?: return status(BAD_REQUEST).body(recipeNotFoundMessage(RecipeNotFoundResult("$recipeId")))
        log.info("tring to prepare recipe ${recipe.name}")
        val (recipeResult, duration) = measureTimedValue { prepareRecipe.prepare(recipe) }

        return when (recipeResult) {
            is CompletedDish ->  ok(Dish(recipeResult.dish.name)).also { log.info("successfully prepared ${recipeResult.dish.name}! in ${duration.inWholeMilliseconds} milliseconds") }
            is NotEnoughFood ->  status(BAD_REQUEST).body(notEnoughFood(recipeResult)).also { log.error(notEnoughFood(recipeResult)); }
            is RecipeNotFoundResult ->  status(BAD_REQUEST).body(recipeNotFoundMessage(recipeResult)).also { log.error(recipeNotFoundMessage(recipeResult)); }
        }
    }

    private fun notEnoughFood(recipeResult: NotEnoughFood) =
        "not enough food in storage missing ${recipeResult.name}"

    private fun recipeNotFoundMessage(recipeResult: RecipeNotFoundResult) =
        "recipe not found ${recipeResult.name}"


    @PostMapping
    fun createRecipe(@RequestBody recipe: Recipe): String {
       recipeRepository.add(recipeAdapter.toDomain(recipe))
       return "OK"
    }


    @GetMapping("/{id}")
    fun getRecipeByName(@PathVariable id: Int): Recipe? {
        val recipe  = recipeRepository.findRecipeById(id) ?: return null
       return recipeAdapter.toWeb(recipe)

    }


}


