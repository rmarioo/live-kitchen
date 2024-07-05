package com.rmarioo.live.kitchen.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "foodstorage")
data class FoodStorage(
    @Id
    val id: Int = 0,
    val name: String
)

