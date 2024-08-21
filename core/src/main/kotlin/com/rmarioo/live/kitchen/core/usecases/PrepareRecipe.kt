package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.ChefService
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.time.measureTime

class PrepareRecipe(
    private val recipeRepository: RecipeRepository,
    private val foodStorageRepository: FoodStorageRepository,
    private val chefService: ChefService,
    private val kitchen: Kitchen
) {

    private val virtualThreadDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    fun prepareAsync(requestId: String,recipeId: Int): List<KitchenEvent> {

        val recipe = recipeRepository.findRecipeById(recipeId) ?: return listOf()
        if (!kitchen.hasRequest(requestId)) {
            kitchen.add(RecipeStarted(requestId, recipe.name,System.currentTimeMillis()))
            CoroutineScope(virtualThreadDispatcher).launch {
                startRecipePreparation(recipe, requestId)
            }
        }
        return kitchen.eventsFor(requestId)

    }


    private fun startRecipePreparation(recipe: Recipe, requestId: String) {

        val foodStorage = foodStorageRepository.find()

        recipe.ingredients.food.firstOrNull { !foodStorage.enoughFood(it) }
            ?.let { kitchen.add(NotEnoughFoodInStorage(requestId, it.name,System.currentTimeMillis())); return }

        parallelStepsFrom(recipe.steps).forEach { parallelStep ->

            measureTime {
                runBlocking(virtualThreadDispatcher) {
                    try {
                        parallelStep.map { step: Step -> async {

                            kitchen.add(StepStarted(requestId, step.name,System.currentTimeMillis()))
                            chefService.execute(step)
                            kitchen.add(StepCompleted(requestId, step.name,System.currentTimeMillis()))
                        }
                        }.awaitAll()
                    }
                    catch (exception: Exception) {
                        kitchen.add(RecipeError(requestId,exception.message ?: "error",System.currentTimeMillis()))
                        throw exception
                    }

                }
            }
        }

        kitchen.add(RecipeCompleted(requestId, recipe.name,System.currentTimeMillis()))
    }


    private fun parallelStepsFrom(steps: List<Step>): MutableList<List<Step>> {
        val parallelSteps: MutableList<List<Step>> = mutableListOf()
        var mutableSteps: List<Step> = steps


        var executionCounter = 1

        while (mutableSteps.isNotEmpty()) {
            val stepsWithoutDependecies: List<Step> = mutableSteps.filter { it.dependsOnStep.isEmpty() }

            parallelSteps.add(stepsWithoutDependecies)

            val names = stepsWithoutDependecies.map { it.name }
            executionCounter += 1

            mutableSteps = mutableSteps.map { step ->
                step.copy(dependsOnStep = step.dependsOnStep - step.dependsOnStep.filter { names.contains(it.name) }
                    .toSet())
            }
            mutableSteps = mutableSteps - stepsWithoutDependecies.toSet()
        }
        return parallelSteps
    }

}





