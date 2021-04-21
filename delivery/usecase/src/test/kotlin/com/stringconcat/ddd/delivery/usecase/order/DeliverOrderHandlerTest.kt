package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderDeliveredDomainEvent
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliverOrderHandlerTest {

    @Test
    fun `should successfully deliver the order`() {
        // Given
        val orderExtractor = TestDeliveryOrderExtractor()
        val order = order()
        val orderId = order.id
        orderExtractor[orderId] = order
        val orderPersister = TestDeliveryOrderPersister()
        val useCase = DeliverOrderHandler(orderExtractor, orderPersister)

        // When
        val request = DeliverOrderRequest(orderId)
        val result = useCase.execute(request)

        // Then
        result.shouldBeRight()
        orderPersister[orderId]?.delivered shouldBe true
        orderPersister[orderId]?.popEvents() shouldContainExactly listOf(DeliveryOrderDeliveredDomainEvent(orderId))
    }

    @Test
    fun `order not found`() {
        // Given
        val orderExtractor = TestDeliveryOrderExtractor()
        val orderPersister = TestDeliveryOrderPersister()
        val useCase = DeliverOrderHandler(orderExtractor, orderPersister)

        // When
        val request = DeliverOrderRequest(DeliveryOrderId(42))
        val result = useCase.execute(request)

        // Then
        result shouldBeLeft DeliverOrderUseCaseError.OrderNotFound
    }

    @Test
    fun `order already delivered`() {
        // Given
        val orderExtractor = TestDeliveryOrderExtractor()
        val order = order()
        order.deliver()
        val orderId = order.id
        orderExtractor[orderId] = order
        val orderPersister = TestDeliveryOrderPersister()
        val useCase = DeliverOrderHandler(orderExtractor, orderPersister)

        // When
        val request = DeliverOrderRequest(orderId)
        val result = useCase.execute(request)

        // Then
        result shouldBeLeft DeliverOrderUseCaseError.OrderAlreadyDelivered
    }
}
