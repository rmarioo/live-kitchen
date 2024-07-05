package com.rmarioo.live.kitchen.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("steps")
data class Step(
    @Id
    val id: Int = 0,
    val name: String,
    val time: Long,
    val recipeId: Int
)
