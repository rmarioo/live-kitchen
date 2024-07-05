package com.rmarioo.live.kitchen

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "chefserver")
class ChefServerProperties {
    lateinit var host: String

}
