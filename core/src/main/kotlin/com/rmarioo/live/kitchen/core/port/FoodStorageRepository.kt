package com.rmarioo.live.kitchen.core.port

import com.rmarioo.live.kitchen.core.model.FoodStorage

interface FoodStorageRepository {
    fun find(): FoodStorage

}
