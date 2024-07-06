package com.rmarioo.live.kitchen.core.model

class Kitchen(private var events: List<KitchenEvent> = listOf()) {

    fun add(event: KitchenEvent) {
      //  println("Kitchen: received request to add $event previous list is $events ")
        events = events + event
    }

    fun eventsFor(requestId: String): List<KitchenEvent> {
        val events = events.filter { it.requestId == requestId }


        return consolidateEvents(events)
    }

    private fun consolidateEvents(events: List<KitchenEvent>): MutableList<KitchenEvent> {

        val consolidatedEvents: MutableList<KitchenEvent> = mutableListOf()
        events.forEach { e ->
            when (e) {
                is StepStarted -> {
                    val stepCompleted: KitchenEvent? =
                        events.firstOrNull { event -> event is StepCompleted && event.name == e.name }
                    if (stepCompleted != null)
                        consolidatedEvents.add(StepUpdated(e.requestId, e.name,System.currentTimeMillis()))
                    else
                        consolidatedEvents.add(e)
                }
                is RecipeStarted -> {
                    val recipeCompleted: KitchenEvent? =
                        events.firstOrNull { event -> event is RecipeCompleted && event.name == e.name }
                    if (recipeCompleted != null) {
                        val now = System.currentTimeMillis()
                        val completionTime: Long = now - e.creationTime
                        consolidatedEvents.add(RecipeUpdated(e.requestId, e.name,now,completionTime))
                    } else
                        consolidatedEvents.add(e)
                }

                is StepCompleted -> {}
                is RecipeCompleted -> {}
                else -> {
                    consolidatedEvents.add(e)
                }
            }
        }
        return consolidatedEvents
    }

    fun hasRequest(requestId: String): Boolean {
        return events.any { it.requestId == requestId }
    }

}


sealed class KitchenEvent { abstract val requestId: String; abstract val name: String; abstract val creationTime: Long}
data class RecipeStarted(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
data class RecipeUpdated(override val requestId: String, override val name: String, override val creationTime: Long, val completionTime: Long): KitchenEvent()
data class RecipeCompleted(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
data class RecipeError(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()

data class StepStarted(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
data class StepCompleted(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
data class StepUpdated(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()

data class NotEnoughFoodInStorage(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
data class RecipeNotFound(override val requestId: String, override val name: String, override val creationTime: Long): KitchenEvent()
