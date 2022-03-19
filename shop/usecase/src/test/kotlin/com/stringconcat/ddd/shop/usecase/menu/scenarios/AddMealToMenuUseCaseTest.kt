package com.stringconcat.ddd.shop.usecase.menu.scenarios

import com.stringconcat.ddd.shop.domain.mealDescription
import com.stringconcat.ddd.shop.domain.mealId
import com.stringconcat.ddd.shop.domain.mealName
import com.stringconcat.ddd.shop.domain.menu.MealIdGenerator
import com.stringconcat.ddd.shop.domain.price
import com.stringconcat.ddd.shop.usecase.MockMealPersister
import com.stringconcat.ddd.shop.usecase.menu.AddMealToMenuUseCaseError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class AddMealToMenuUseCaseTest {

    @Test
    fun `successfully added`() {

        val name = mealName()
        val description = mealDescription()
        val price = price()

        val persister = MockMealPersister()

        val result = AddMealToMenuUseCase(
            mealPersister = persister,
            idGenerator = TestMealIdGenerator,
            mealExists = { false }
        ).execute(
            name = name,
            description = description,
            price = price
        )

        val id = TestMealIdGenerator.id

        result.shouldBeRight().should {
            it shouldBe id
        }

        persister.verifyInvoked(id, name, description, price)
    }

    @Test
    fun `meal already exists`() {

        val name = mealName()
        val description = mealDescription()
        val price = price()

        val persister = MockMealPersister()

        val result = AddMealToMenuUseCase(
            mealPersister = persister,
            idGenerator = TestMealIdGenerator,
            mealExists = { true }
        ).execute(
            name = name,
            description = description,
            price = price
        )

        result shouldBeLeft AddMealToMenuUseCaseError.AlreadyExists
        persister.verifyEmpty()
    }

    object TestMealIdGenerator : MealIdGenerator {
        val id = mealId()
        override fun generate() = id
    }
}