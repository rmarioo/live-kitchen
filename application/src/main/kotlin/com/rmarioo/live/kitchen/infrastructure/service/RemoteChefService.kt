package com.rmarioo.live.kitchen.infrastructure.service

import com.rmarioo.live.kitchen.core.model.Step
import com.rmarioo.live.kitchen.core.port.ChefService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations
import java.io.Serializable

class RemoteChefService(val restOperations: RestOperations,val chefServerHost : String): ChefService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(step: Step) {

        val url = "${chefServerHost}/executeStep?delay=${step.time}&description=${normalize(step.name)}"
        log.info("calling chef-server with url: $url")

        val entity: ResponseEntity<DishResponse> = restOperations.getForEntity(url, DishResponse::class.java)

        if (!entity.statusCode.is2xxSuccessful) {
            throw RuntimeException("error chef server status ${entity.statusCode}")
        }
        log.info("chef-server returned ${entity.body}")
    }

    private fun normalize(name: String) = name.lowercase().replace(",","").replace(" ", "_")

    data class DishResponse(val preparedBy: String,val description: String,val delay: Int ): Serializable
}
