package com.stringconcat.ddd.shop.domain.menu

import com.stringconcat.ddd.shop.domain.meal
import com.stringconcat.ddd.shop.domain.mealDescription
import com.stringconcat.ddd.shop.domain.mealId
import com.stringconcat.ddd.shop.domain.mealName
import com.stringconcat.ddd.shop.domain.price
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class MealTest {

    val mealId = mealId()

    private val idGenerator = object : MealIdGenerator {
        override fun generate() = mealId
    }

    @Test
    fun `add meal - success`() {

        val price = price()
        val name = mealName()
        val description = mealDescription()

        val result = Meal.addMealToMenu(
            idGenerator = idGenerator,
            mealExists = { false },
            name = name,
            description = description,
            price = price
        )

       val meal = result.shouldBeRight()

        meal.id shouldBe mealId
        meal.name shouldBe name
        meal.description shouldBe description
        meal.price shouldBe price
        meal.visible() shouldBe true
        meal.popEvents() shouldContainExactly listOf(MealAddedToMenuDomainEvent(mealId))
    }

    @Test
    fun `add meal to menu - already exists with the same name`() {

        val result = Meal.addMealToMenu(
            idGenerator = idGenerator,
            mealExists = { true },
            name = mealName(),
            description = mealDescription(),
            price = price()
        )

        result shouldBeLeft AlreadyExistsWithSameNameError
    }

    @Test
    fun `remove meal from menu - success`() {
        val meal = meal(removed = false)
        meal.removeMealFromMenu()

        meal.removed shouldBe true
        meal.visible() shouldBe false
        meal.popEvents() shouldContainExactly listOf(MealRemovedFromMenuDomainEvent(meal.id))
    }

    @Test
    fun `remove meal from menu - already removed`() {
        val meal = meal(removed = true)
        meal.removeMealFromMenu()

        meal.removed shouldBe true
        meal.visible() shouldBe false
        meal.popEvents().shouldBeEmpty()
    }
}