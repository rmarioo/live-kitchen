package com.rmarioo.live.kitchen.core.model

data class Step(val name: String, val time: Long, val dependsOnStep: List<Step>)
