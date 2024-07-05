package com.rmarioo.live.kitchen.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("recipes")
data class Recipe(
    @Id
    val id: Int = 0,
    val name: String
)
