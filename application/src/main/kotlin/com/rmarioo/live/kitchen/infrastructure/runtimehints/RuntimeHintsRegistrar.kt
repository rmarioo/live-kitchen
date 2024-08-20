package com.rmarioo.live.kitchen.infrastructure.runtimehints

import com.rmarioo.live.kitchen.core.model.*
import com.rmarioo.live.kitchen.infrastructure.service.RemoteChefService
import com.rmarioo.live.kitchen.infrastructure.web.MainController
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeHint
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.stereotype.Component


@Component
@ImportRuntimeHints(SpelRuntimeHints::class)
class SpelRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-with-GraalVM#core-container
        // To use SpEL expressions in a native image, runtime hints must be provided to enable the reflective calls that the expression requires
        hints.reflection().registerType(Recipe::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })
        hints.reflection().registerType(Step::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })
        hints.reflection().registerType(Ingredients::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })
        hints.reflection().registerType(Food::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })
        hints.reflection().registerType(MainController.QuantityView::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })
        hints.reflection().registerType(FoodStorage::class.java, { builder: TypeHint.Builder -> builder.withMembers(MemberCategory.INVOKE_PUBLIC_METHODS) })

        // serialization hints
        hints.serialization().registerType(RemoteChefService.DishResponse::class.java)
    }
}
