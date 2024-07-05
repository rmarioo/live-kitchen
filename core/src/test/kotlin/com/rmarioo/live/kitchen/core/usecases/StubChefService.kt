package com.rmarioo.live.kitchen.core.usecases

import com.rmarioo.live.kitchen.core.model.Step
import com.rmarioo.live.kitchen.core.port.ChefService

class StubChefService : ChefService {



    override fun execute(step: Step) {
        val waitTime = step.time
        Thread.sleep(waitTime)

    }

}
