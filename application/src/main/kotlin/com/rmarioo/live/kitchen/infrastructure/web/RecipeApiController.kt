package com.rmarioo.live.kitchen.infrastructure.web


import com.rmarioo.live.kitchen.core.model.KitchenEvent
import com.rmarioo.live.kitchen.core.model.RecipeError
import com.rmarioo.live.kitchen.core.model.RecipeUpdated
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.core.usecases.PrepareRecipe
import com.rmarioo.live.kitchen.infrastructure.web.model.PrepareRecipeResult
import com.rmarioo.live.kitchen.infrastructure.web.model.Event
import com.rmarioo.live.kitchen.infrastructure.web.model.Recipe
import com.rmarioo.live.kitchen.infrastructure.web.model.Status
import com.rmarioo.live.kitchen.infrastructure.web.model.Status.DONE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/recipes")
class RecipeApiController(
    val recipeRepository: RecipeRepository,
    val recipeAdapter: RecipeAdapter,
    val prepareRecipe: PrepareRecipe
) {

    @PostMapping("/prepare")
    fun prepareRecipe(@RequestParam requestId: String, @RequestParam recipeId: Int): ResponseEntity<PrepareRecipeResult> {

        val kitchenEvents: List<KitchenEvent> = prepareRecipe.prepareAsync(requestId, recipeId)
        val status = statusFrom(kitchenEvents)

        return ok(PrepareRecipeResult(status,eventsFrom(kitchenEvents)) )
    }

    private fun statusFrom(kitchenEvents: List<KitchenEvent>): Status {

        return if (kitchenEvents.any { it is RecipeUpdated })
            DONE
        else if (kitchenEvents.any { it is RecipeError })
            Status.ERROR
        else
            Status.IN_PROGRESS

    }

    private fun eventsFrom(kitchenEvents: List<KitchenEvent>): List<Event> {
        return kitchenEvents.map { Event(it.javaClass.simpleName,it.name) }
    }

    @PostMapping
    fun createRecipe(@RequestBody recipe: Recipe): String {
       recipeRepository.add(recipeAdapter.toDomain(recipe))
       return "OK"
    }


    @GetMapping("/{id}")
    fun getRecipeById(@PathVariable id: Int): Recipe? {
        val recipe  = recipeRepository.findRecipeById(id) ?: return null
       return recipeAdapter.toWeb(recipe)

    }


}


