package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.FoodStorage
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository

class InMemoryFoodStorageRepository(private val foodStorage: FoodStorage) : FoodStorageRepository {
    override fun find(): FoodStorage {
        return foodStorage
    }


}
