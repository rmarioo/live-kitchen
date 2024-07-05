package com.rmarioo.live.kitchen.infrastructure.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("steps_dependencies")
data class StepDependencies(
    @Id
    val id: Int = 0,
    val stepId: Int,
    val dependsOnId: Int
)
