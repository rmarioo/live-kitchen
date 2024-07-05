package com.rmarioo.live.kitchen.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("foods")
data class Food(
    @Id
    val id: Int = 0,
    val name: String,
    val quantity: Int?,
    val foodStorageId: Int
)

