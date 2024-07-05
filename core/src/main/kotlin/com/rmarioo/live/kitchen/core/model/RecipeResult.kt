package com.rmarioo.live.kitchen.core.model

sealed class RecipeResult
data class CompletedDish(val dish: Dish): RecipeResult()
data class RecipeNotFoundResult(val name: String): RecipeResult()
data class NotEnoughFood(val name: String): RecipeResult()

