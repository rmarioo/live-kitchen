package com.rmarioo.live.kitchen.infrastructure.persistence

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.infrastructure.persistence.model.Ingredient
import com.rmarioo.live.kitchen.infrastructure.persistence.model.Recipe
import com.rmarioo.live.kitchen.infrastructure.persistence.model.Step
import com.rmarioo.live.kitchen.infrastructure.persistence.model.StepDependencies
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull
import com.rmarioo.live.kitchen.core.model.Recipe as DomainRecipe
import com.rmarioo.live.kitchen.core.model.Step as DomainStep

open class SpringDataRecipeRepository(
    private val persistenceRecipeRepository: PersistenceRecipeRepository,
    private val stepRepository: StepRepository,
    private val ingredientRepository: IngredientRepository,
    private val stepDependencyRepository: StepDependencyRepository
): RecipeRepository {

    @Transactional(readOnly = true)
    override fun findRecipeByName(name: String): DomainRecipe? {
        val recipeId: Int = persistenceRecipeRepository.findIdByName(name) ?: return null
        val recipe = Recipe(id =recipeId,name=name)
        return fullRecipeFrom(recipe)
    }

    @Transactional(readOnly = true)
    override fun findRecipeById(recipeId: Int): DomainRecipe? {
        val recipe: Recipe = persistenceRecipeRepository.findById(recipeId).getOrNull() ?: return null
        return fullRecipeFrom(recipe)
    }

    private fun fullRecipeFrom(recipe: Recipe): com.rmarioo.live.kitchen.core.model.Recipe {
        val allSteps: MutableIterable<Step> = stepRepository.findAll()
        val stepDependencies = stepDependencyRepository.findAll()
        val allStepForRecipe = allSteps.filter { it.recipeId == recipe.id }
        val ingredients = ingredientRepository.findByRecipeId(recipe.id)
        return DomainRecipe(
            recipe.name,
            stepsFrom(allStepForRecipe, stepDependencies, allSteps),
            ingredientsFrom(ingredients)
        )
    }

    @Transactional
    override fun add(recipe: DomainRecipe): Int {

        val savedRecipe: Recipe = persistenceRecipeRepository.save(Recipe(name = recipe.name))
        saveDependantEntities(recipe, savedRecipe)
        return savedRecipe.id
    }

    @Transactional
    override fun save(recipe: DomainRecipe,recipeId: Int) {

        val savedRecipe: Recipe = persistenceRecipeRepository.save(Recipe(name = recipe.name, id = recipeId))
        saveDependantEntities(recipe, savedRecipe)
    }


    @Transactional
    override fun deleteRecipeById(recipeId: Int) {
        ingredientRepository.deleteByRecipeId(recipeId)
        val stepIds: List<Int> = stepRepository.findStepIdsByRecipeId(recipeId)
        stepIds.forEach { stepId -> stepDependencyRepository.deleteByStepId(stepId) }
        stepIds.forEach { stepId -> stepRepository.deleteById(stepId) }
        persistenceRecipeRepository.deleteById(recipeId)
    }

    private fun saveDependantEntities(
        recipe: com.rmarioo.live.kitchen.core.model.Recipe,
        savedRecipe: Recipe
    ) {
        val recipeSteps: List<Step> = convertFrom(recipe.steps, savedRecipe.id)
        val savedSteps = recipeSteps.map { step -> stepRepository.save(step.copy(id = stepRepository.findIdByNameAndRecipeId(step.name,savedRecipe.id) ?: 0)) }
        val stepDependencies: List<StepDependencies> = stepDependencies(recipe, savedSteps)
        stepDependencies.forEach { stepDependencyRepository.save(it) }

        recipe.ingredients.food.forEach { food: Food ->
            val id: Int = ingredientRepository.findIdByNameAndRecipeId(food.name,savedRecipe.id) ?: 0
            ingredientRepository.save(
                Ingredient(
                    id= id,
                    name = food.name,
                    quantity = persistenceQuantityFrom(food.quantity),
                    recipeId = savedRecipe.id
                )
            )
        }
    }

    private fun persistenceQuantityFrom(quantity: Quantity): Int? {
        return when(quantity) {
            EndlessQuantity -> null
            is FiniteQuantity -> quantity.value
        }
    }

    private fun stepDependencies(
        recipe: com.rmarioo.live.kitchen.core.model.Recipe,
        savedSteps: List<Step>
    ): List<StepDependencies> {
        return recipe.steps.flatMap { step: com.rmarioo.live.kitchen.core.model.Step ->

            val id: Int = findStepId(step.name, savedSteps)
            val dependsOnSteNames: List<String> = step.dependsOnStep.map { it.name }

            dependsOnSteNames.map { name ->
                val depId = findStepId(name, savedSteps)
                StepDependencies(stepId = id, dependsOnId = depId)
            }
        }
    }

    private fun findStepId(name: String, savedSteps: List<Step>): Int {
        return savedSteps.first { it.name == name }.id
    }

    private fun convertFrom(steps: List<DomainStep>,recipeId: Int): List<Step> {
        return steps.map { Step(name =it.name,time =it.time,recipeId = recipeId) }
    }

    private fun ingredientsFrom(ingredients: List<Ingredient>): Ingredients {
        return Ingredients(ingredients.map { ingredient: Ingredient ->
            Food(
                ingredient.name,
                quantityFrom(ingredient.quantity)
            )
        })
    }

    private fun quantityFrom(quantity: Int?): Quantity {
        return quantity?.let { FiniteQuantity(it) } ?: EndlessQuantity
    }

    private fun stepsFrom(steps: List<Step>, stepDependencies: Iterable<StepDependencies>, allSteps: MutableIterable<Step>): List<DomainStep> {
        return steps.map {  step -> DomainStep(step.name,step.time,dependingStepsFrom(step.id,stepDependencies,allSteps)) }
    }

    private fun dependingStepsFrom(id: Int, stepDependencies: Iterable<StepDependencies>, allSteps: MutableIterable<Step>): List<DomainStep> {
        val filter: List<StepDependencies> = stepDependencies.filter { s -> s.stepId == id }
        val map: List<Step> = filter.map { sd -> allSteps.first { step -> step.id == sd.dependsOnId } }
        return stepsFrom(map,stepDependencies, allSteps)
    }


}
