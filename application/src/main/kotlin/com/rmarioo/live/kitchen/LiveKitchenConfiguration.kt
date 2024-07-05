package com.rmarioo.live.kitchen

import com.rmarioo.live.kitchen.core.model.Kitchen
import com.rmarioo.live.kitchen.core.port.ChefService
import com.rmarioo.live.kitchen.core.port.FoodStorageRepository
import com.rmarioo.live.kitchen.core.port.RecipeRepository
import com.rmarioo.live.kitchen.core.usecases.PrepareRecipe
import com.rmarioo.live.kitchen.infrastructure.persistence.*
import com.rmarioo.live.kitchen.infrastructure.service.LocalChefService
import com.rmarioo.live.kitchen.infrastructure.service.RemoteChefService
import com.rmarioo.live.kitchen.infrastructure.web.RecipeAdapter
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration.ofMillis


@Configuration
class LiveKitchenConfiguration {


    @Bean
    fun remoteChefService(chefServerProperties: ChefServerProperties): RemoteChefService {
        val restTemplate = RestTemplateBuilder().setConnectTimeout(ofMillis(1000))
            .setReadTimeout(ofMillis(30000))
            .build()
        return RemoteChefService(restTemplate,chefServerProperties.host)
    }

    @Bean
    fun localChefService(): LocalChefService {
        return LocalChefService()
    }

    @Bean
    fun prepareRecipe(recipeRepository: RecipeRepository,
                      foodStorageRepository: FoodStorageRepository,
                      remoteChefService: RemoteChefService
    ): PrepareRecipe {

        return PrepareRecipe(recipeRepository, foodStorageRepository, remoteChefService , Kitchen())
    }



    @Bean
    fun recipeRepository(persistenceRecipeRepository: PersistenceRecipeRepository,
                         stepRepository: StepRepository,
                         ingredientRepository: IngredientRepository,
                         stepDependencyRepository: StepDependencyRepository
    ): RecipeRepository {
        return SpringDataRecipeRepository(persistenceRecipeRepository, stepRepository, ingredientRepository, stepDependencyRepository)
    }

    @Bean
    fun foodStorageRepository(persistenceFoodStorageRepository: PersistenceFoodStorageRepository, foodRepository: FoodRepository) : FoodStorageRepository {
        return SpringDataFoodStorageRepository(persistenceFoodStorageRepository, foodRepository)
    }

    @Bean
    fun recipeAdapter(): RecipeAdapter {
        return RecipeAdapter()
    }


}
