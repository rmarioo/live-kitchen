package com.rmarioo.live.kitchen.infrastructure.web.model

data class Recipe(
    val id: Int? = null,
    val name: String,
    val steps: List<Step>,
    val ingredients: List<Ingredient>
)
