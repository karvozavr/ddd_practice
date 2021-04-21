package com.stringconcat.ddd.delivery.domain.order

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliveryOrderTest {

    @Test
    fun `should create delivery order`() {
        // Given
        val address = address()
        val id = orderId()

        // When
        val order = DeliveryOrder.create(
            id = id,
            deliveryAddress = address
        )

        // Then
        order.id shouldBe id
        order.version.value shouldBe 0
        order.deliveryAddress shouldBe address
        order.delivered shouldBe false

        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(id))
    }

    @Test
    fun `should deliver the order`() {
        // Given
        val address = address()
        val id = orderId()
        val order = DeliveryOrder.create(
            id = id,
            deliveryAddress = address
        )
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(id))

        // When
        val result = order.deliver()

        // Then
        result.shouldBeRight()
        order.delivered shouldBe true
        order.popEvents() shouldContainExactly listOf(DeliveryOrderDeliveredDomainEvent(id))
    }

    @Test
    fun `should fail, if order is already delivered`() {
        // Given
        val address = address()
        val id = orderId()
        val order = DeliveryOrder.create(
            id = id,
            deliveryAddress = address
        ).also {
            it.deliver()
        }

        val createdAndDelivered = listOf(DeliveryOrderCreatedDomainEvent(id), DeliveryOrderDeliveredDomainEvent(id))
        order.popEvents() shouldContainExactly createdAndDelivered

        // When
        val result = order.deliver()

        // Then
        result shouldBeLeft OrderAlreadyDelivered
        order.delivered shouldBe true
    }
}
