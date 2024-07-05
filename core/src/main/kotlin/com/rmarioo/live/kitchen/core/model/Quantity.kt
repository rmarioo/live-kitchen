package com.rmarioo.live.kitchen.core.model

sealed class Quantity
data class FiniteQuantity(val value: Int): Quantity()
data object EndlessQuantity: Quantity()
