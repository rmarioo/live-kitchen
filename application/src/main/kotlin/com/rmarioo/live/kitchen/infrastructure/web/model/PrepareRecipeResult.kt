package com.rmarioo.live.kitchen.infrastructure.web.model

data class PrepareRecipeResult(val status: Status, val events: List<Event>)

enum class Status {
    IN_PROGRESS,DONE,ERROR
}

data class Event(val name: String,val description: String)
