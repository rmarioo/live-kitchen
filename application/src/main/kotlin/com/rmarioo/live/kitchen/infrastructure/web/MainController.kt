package com.rmarioo.live.kitchen.infrastructure.web

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.util.*

@Controller
class MainController(
    val recipeRepository: RecipeRepository,
    val foodStorageRepository: FoodStorageRepository
) {

    @GetMapping("/")
    fun index(): ModelAndView {
        return ModelAndView("index")
    }

    @GetMapping("/preparerecipe/{recipeId}")
    fun showrecipe(@PathVariable recipeId: Int, model: Model): ModelAndView {

        val quantityView = QuantityView()
        val recipe: Recipe? = recipeRepository.findRecipeById(recipeId)

        val foodStorage = foodStorageRepository.find()


        val totalStepTime = recipe!!.steps.sumOf { it.time }
        model.addAttribute("recipe",recipe)
        model.addAttribute("recipeId",recipeId)
        model.addAttribute("quantityView",quantityView)
        model.addAttribute("totalStepTime",totalStepTime)
        model.addAttribute("foodStorage",foodStorage)
        return ModelAndView("preparerecipe")
    }




    @GetMapping("/pollingsteps/{recipeId}")
    fun pollingsteps(@PathVariable recipeId: Int,model: Model): ModelAndView {
        val recipe: Recipe = recipeRepository.findRecipeById(recipeId) ?: throw RuntimeException("not found")
        val totalStepTime = recipe.steps.sumOf { it.time }
        val modelAndView = ModelAndView("fragments/polling_steps.html")
        modelAndView.model["requestId"] = UUID.randomUUID()
        modelAndView.model["recipeId"] = recipeId
        modelAndView.model["recipe"] = recipe
        modelAndView.model["totalStepTime"] = totalStepTime
        return modelAndView
    }



    class QuantityView {
        fun toView(quantity: Quantity): String {
            return when(quantity) {
                EndlessQuantity -> "to taste"
                is FiniteQuantity -> "${quantity.value}"
            }
        }
    }

}
