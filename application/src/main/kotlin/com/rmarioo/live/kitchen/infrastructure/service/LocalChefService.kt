package com.rmarioo.live.kitchen.infrastructure.service

import com.rmarioo.live.kitchen.core.model.Step
import com.rmarioo.live.kitchen.core.port.ChefService
import org.slf4j.LoggerFactory

class LocalChefService : ChefService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(step: Step) {
        log.info("step [${step.name}] require  ${step.time} milliseconds...")
        val waitTime = step.time
        Thread.sleep(waitTime)
        log.info("step [${step.name}] completed!!!")

    }

}
