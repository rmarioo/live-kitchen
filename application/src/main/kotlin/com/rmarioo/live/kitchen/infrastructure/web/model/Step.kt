package com.rmarioo.live.kitchen.infrastructure.web.model

data class Step(
    val name: String,
    val time: Long,
    val dependsOnSteps: List<String>
)

