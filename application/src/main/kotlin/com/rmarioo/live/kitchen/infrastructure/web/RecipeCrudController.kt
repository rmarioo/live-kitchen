package com.rmarioo.live.kitchen.infrastructure.web

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.infrastructure.persistence.FoodRepository
import com.rmarioo.live.kitchen.infrastructure.persistence.PersistenceFoodStorageRepository
import com.rmarioo.live.kitchen.infrastructure.persistence.PersistenceRecipeRepository
import com.rmarioo.live.kitchen.infrastructure.persistence.model.FoodStorage
import com.rmarioo.live.kitchen.infrastructure.web.MainController.QuantityView
import com.rmarioo.live.kitchen.infrastructure.web.model.Step
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView


@RestController
class RecipeCrudController(
    val foodStorageRepository: FoodStorageRepository,
    val persistenceRecipeRepository: PersistenceRecipeRepository,
    val recipeRepository: RecipeRepository,
    val persistenceFoodStorageRepository: PersistenceFoodStorageRepository,
    val foodRepository: FoodRepository
) {


    @GetMapping("/allrecipes")
    fun allRecipes(model: Model): ModelAndView {

        val allRecipes: MutableList<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = mutableListOf()
        val recipeIterable: MutableIterable<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = persistenceRecipeRepository.findAll()
        recipeIterable.iterator().forEach { allRecipes.add(it) }
        model.addAttribute("allRecipes",allRecipes)
        return ModelAndView("allrecipes")
    }

    @GetMapping("/createrecipe")
    fun createRecipe(model: Model): ModelAndView {


        val foodStorage = foodStorageRepository.find()
        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage",foodStorage)
        return ModelAndView("createrecipe")
    }

    @GetMapping("/newrecipe")
    fun newRecipe(@RequestParam recipeName: String,model: Model): ModelAndView {

        findOrCreateRecipe(recipeName)
        val allRecipes: MutableList<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = mutableListOf()
        val recipeIterable: MutableIterable<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = persistenceRecipeRepository.findAll()
        recipeIterable.iterator().forEach { allRecipes.add(it) }


        val foodStorage = foodStorageRepository.find()
        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage",foodStorage)
        model.addAttribute("allRecipes",allRecipes)
        return ModelAndView("allrecipes")
    }

    @GetMapping("/deleterecipe/{recipeId}")
    fun deleterecipe(@PathVariable recipeId: Int, model: Model): ModelAndView {

        recipeRepository.deleteRecipeById(recipeId)

        val allRecipes: MutableList<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = mutableListOf()
        val recipeIterable: MutableIterable<com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe> = persistenceRecipeRepository.findAll()
        recipeIterable.iterator().forEach { allRecipes.add(it) }
        model.addAttribute("allRecipes",allRecipes)
        return ModelAndView("allrecipes")
    }

    @GetMapping("/editrecipe/{recipeId}")
    fun editRecipe(@PathVariable recipeId: Int, model: Model): ModelAndView {

        val recipe = recipeRepository.findRecipeById(recipeId)
//        val recipe = findOrCreateRecipe(recipeName)
//        val recipeId = persistenceRecipeRepository.findIdByName(recipeName)

        val foodStorage = foodStorageRepository.find()
        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage",foodStorage)
        model.addAttribute("recipe",recipe)
        model.addAttribute("recipeId",recipeId)
        return ModelAndView("editrecipe")
    }

    @PostMapping("/editrecipe/addstep")
    fun addStepToRecipe(@RequestParam time: Int,
                        @RequestParam name: String,
                        @RequestParam dependsOn: String?,
                        @RequestParam recipeId: Int, model: Model): ModelAndView {

        var recipe: Recipe = recipeRepository.findRecipeById(recipeId) ?: throw RuntimeException("recipe not found for $recipeId")
        recipe = recipe.copy(steps = recipe.steps + stepFrom(name, time, emptyToNull(dependsOn)))
        recipeRepository.save(recipe,recipeId)
        val updatedRecipe = recipeRepository.findRecipeById(recipeId)
        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage", foodStorageRepository.find())
        model.addAttribute("recipe",updatedRecipe)
        model.addAttribute("recipeId",recipeId)
        return ModelAndView("editrecipe")
    }

    private fun emptyToNull(dependsOn: String?) = if (dependsOn.isNullOrEmpty()) {
        null
    } else dependsOn

    @PostMapping("/editrecipe/addingredient")
    fun addIngredientToRecipe(
                        @RequestParam name: String,
                        @RequestParam quantity: Int?,
                        @RequestParam recipeId: Int, model: Model): ModelAndView {

        var recipe: Recipe = recipeRepository.findRecipeById(recipeId) ?: throw RuntimeException("recipe not found for $recipeId")
        recipe = recipe.copy(ingredients = Ingredients(recipe.ingredients.food + Food(name=name,quantityFrom(quantity))))
        recipeRepository.save(recipe,recipeId)
        val updatedRecipe = recipeRepository.findRecipeById(recipeId)

        val foodStorageId: Int = loadFirstFoodStorageId()

        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage", foodStorageRepository.find())
        model.addAttribute("recipe",updatedRecipe)
        model.addAttribute("recipeId",recipeId)
        model.addAttribute("foodStorageId",foodStorageId)
        return ModelAndView("editrecipe")
    }

    @PostMapping("/editrecipe/addfoodtostorage")
    fun addfoodtostorage(
                        @RequestParam name: String,
                        @RequestParam quantity: Int?,
                        @RequestParam recipeId: Int, model: Model): ModelAndView {

        val recipe: Recipe = recipeRepository.findRecipeById(recipeId) ?: throw RuntimeException("recipe not found for $recipeId")

        val foodStorageId: Int = loadFirstFoodStorageId()

        val existingFoods = foodRepository.findByFoodStorageId(foodStorageId)
        val existingFoodId = existingFoods.firstOrNull { it.name == name }?.id ?: 0
        foodRepository.save(com.rmarioo.live.kitchen.infrastructure.persistence.model.Food(id =existingFoodId, name=name, quantity = quantity, foodStorageId = foodStorageId))
        model.addAttribute("quantityView", QuantityView())
        model.addAttribute("foodStorage", foodStorageRepository.find())
        model.addAttribute("recipe",recipe)
        model.addAttribute("recipeId",recipeId)
        model.addAttribute("foodStorageId",foodStorageId)
        return ModelAndView("editrecipe")
    }

    private fun loadFirstFoodStorageId(): Int {
        val foodStorages: MutableIterable<FoodStorage> = persistenceFoodStorageRepository.findAll()
        return foodStorages.iterator().next().id
    }

    private fun quantityFrom(quantity: Int?): Quantity {
        if (quantity == null) return EndlessQuantity

        return FiniteQuantity(quantity)
    }


    private fun stepFrom(name: String, time: Int, dependsOn: String?): com.rmarioo.live.kitchen.core.model.Step {
        val dependsOnStep =
            dependsOn?.let { listOf(com.rmarioo.live.kitchen.core.model.Step(name = it, time = 0, listOf())) }
                ?: listOf()
        return Step(
            name = name,
            time = time.toLong(),
            dependsOnStep = dependsOnStep
        )
    }

    private fun adaptDependantSteps(dependsOnSteps: List<String>, steps: List<Step>): List<com.rmarioo.live.kitchen.core.model.Step> {
        val dependantSteps = steps.filter { step: Step -> dependsOnSteps.contains(step.name) }
        return domainStepsFrom(dependantSteps)
    }

    private fun domainStepsFrom(steps: List<Step>): List<com.rmarioo.live.kitchen.core.model.Step> {
        return steps.map {
            Step(
                it.name,
                it.time,
                adaptDependantSteps(it.dependsOnSteps, steps)
            )
        }

    }

    private fun findOrCreateRecipe(recipeName: String) = recipeRepository.findRecipeByName(recipeName) ?: run {
        val id = recipeRepository.add(
            Recipe(
                name = recipeName,
                steps = listOf(),
                ingredients = Ingredients(listOf())
            )
        )
        recipeRepository.findRecipeById(id) ?: throw RuntimeException("")
    }

}

