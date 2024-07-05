package com.rmarioo.live.kitchen.core.model

data class FoodStorage(val foodList: List<Food>) {

    fun enoughFood(food: Food): Boolean {

        val matchingFood: Food = foodList.firstOrNull { it.name == food.name } ?: return false

        return matchingFood.let {
            when(it.quantity) {
                is EndlessQuantity -> true
                is FiniteQuantity -> it.quantity.value >= (food.quantity as FiniteQuantity).value
            }
        }

    }
}
