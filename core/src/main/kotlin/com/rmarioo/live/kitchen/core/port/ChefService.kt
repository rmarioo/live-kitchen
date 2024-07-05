package com.rmarioo.live.kitchen.core.port

import com.rmarioo.live.kitchen.core.model.Step

interface ChefService {

    fun execute(step: Step)
}
