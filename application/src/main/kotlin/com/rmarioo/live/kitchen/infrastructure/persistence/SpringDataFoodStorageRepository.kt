package com.rmarioo.live.kitchen.infrastructure.persistence

import com.rmarioo.live.kitchen.core.model.EndlessQuantity
import com.rmarioo.live.kitchen.core.model.FiniteQuantity
import com.rmarioo.live.kitchen.core.model.FoodStorage
import com.rmarioo.live.kitchen.core.model.Quantity
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.model.Food as DomainFood

class SpringDataFoodStorageRepository(
    private val persistenceFoodStorageRepository: PersistenceFoodStorageRepository,
    private val foodRepository: FoodRepository): FoodStorageRepository {


    override fun find(): FoodStorage {
        val foodStorages = persistenceFoodStorageRepository.findAll()

        val domainFood = foodStorages
            .flatMap { fs -> foodRepository.findByFoodStorageId(fs.id) }
            .map { DomainFood(it.name, quantityFrom(it.quantity)) }

        return FoodStorage(domainFood)
    }

    private fun quantityFrom(quantity: Int?): Quantity {
        return quantity?.let { FiniteQuantity(it)  } ?: EndlessQuantity
    }
}
