package com.rmarioo.live.kitchen.infrastructure.web

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.usecases.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/events")
class EventController(val prepareRecipe: PrepareRecipe) {

    val stepTemplate= """<ul class="step __status_class__">
              <li><strong>__step__</strong></li>
            </ul>"""

    val recipeTemplate= """<div class="__status_class__">__step__</div>"""



    @GetMapping("/recipeAsync/{requestId}/{recipeId}")
    fun recipeAsync(@PathVariable requestId: String,@PathVariable recipeId: Int): ResponseEntity<String> {

       // log.info("received request for /events/recipeAsync")

        val events = prepareRecipe.prepareAsync(requestId, recipeId)


        val also = events.joinToString(separator = "") {
            when (it) {
                is RecipeStarted -> recipeTemplate.replace("__step__","Preparing ${it.name} ...").replace("__status_class__","recipeStarted")
                is RecipeCompleted -> "✓ ${it.name}"
                is RecipeUpdated ->  recipeTemplate.replace("__step__","✓ ${it.name} completed in ${it.completionTime} milliseconds !").replace("__status_class__","recipeCompleted")
                is StepStarted -> stepTemplate.replace("__step__", "- ${it.name} ...").replace("__status_class__","started")
                is StepUpdated -> stepTemplate.replace("__step__", "✓ ${it.name}").replace("__status_class__","completed")
                is StepCompleted -> "- completed step ${it.name}"
                is NotEnoughFoodInStorage -> stepTemplate.replace("__step__", "KO - not enough ${it.name}").replace("__status_class__","failed")
                is RecipeNotFound -> "not found recipe ${it.name}"
                is RecipeError -> recipeTemplate.replace("__step__","KO ${it.error} ").replace("__status_class__","failed")
            }
        }


        val finalEventReceived = events.firstOrNull { e -> e.requestId== requestId &&  e is RecipeUpdated || e is NotEnoughFoodInStorage || e is RecipeNotFound }
        return if (finalEventReceived != null) {
            ResponseEntity.status(286).body(also)
        } else ResponseEntity.ok(also)
    }

}
